package com.example.chessoverlay;

final class Search {
    static final int MATE = 100000, INF = 1000000;
    private Chess c;
    private long deadline, nodes;
    private boolean stop;
    private int rootBest, prevBest;
    private final int[][] buf = new int[64][256];
    private final int[][] sc = new int[64][256];

    private boolean timeUp() {
        if ((++nodes & 2047) == 0 && System.nanoTime() > deadline) stop = true;
        return stop;
    }

    private int quiesce(int alpha, int beta, int ply) {
        if (timeUp()) return 0;
        int st = Eval.eval(c);
        if (st >= beta || ply >= 60) return st;
        if (st > alpha) alpha = st;
        int[] mv = buf[ply];
        int n = Gen.legal(c, mv, true);
        Order.order(c, mv, n, sc[ply], 0);
        for (int i = 0; i < n; i++) {
            c.make(mv[i]);
            int v = -quiesce(-beta, -alpha, ply + 1);
            c.unmake();
            if (stop) return 0;
            if (v > alpha) { alpha = v; if (alpha >= beta) break; }
        }
        return alpha;
    }

    private int search(int depth, int alpha, int beta, int ply) {
        if (timeUp()) return 0;
        if (ply >= 60) return Eval.eval(c);
        boolean chk = c.attacked(c.kp[c.side == 1 ? 0 : 1], -c.side);
        if (chk) depth++;
        if (depth <= 0) return quiesce(alpha, beta, ply);
        int[] mv = buf[ply];
        int n = Gen.legal(c, mv, false);
        if (n == 0) return chk ? -MATE + ply : 0;
        Order.order(c, mv, n, sc[ply], ply == 0 ? prevBest : 0);
        for (int i = 0; i < n; i++) {
            c.make(mv[i]);
            int v = -search(depth - 1, -beta, -alpha, ply + 1);
            c.unmake();
            if (stop) return 0;
            if (v > alpha) {
                alpha = v;
                if (ply == 0) rootBest = mv[i];
                if (alpha >= beta) break;
            }
        }
        return alpha;
    }

    int best(Chess pos, int ms) {
        c = pos; stop = false; nodes = 0; prevBest = 0;
        deadline = System.nanoTime() + ms * 1_000_000L;
        int[] mv = new int[256];
        if (Gen.legal(c, mv, false) == 0) return 0;
        int best = mv[0];
        for (int d = 1; d <= 9; d++) {
            rootBest = 0;
            int v = search(d, -INF, INF, 0);
            if (stop) break;
            best = rootBest; prevBest = best;
            if (Math.abs(v) > MATE - 100) break;
        }
        return best;
    }
}
