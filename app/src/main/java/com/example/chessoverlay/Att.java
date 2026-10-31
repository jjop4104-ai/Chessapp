package com.example.chessoverlay;

final class Att {
    static final int[][] N = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
    static final int[][] K = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
    static final int[][] ORTH = {{1,0},{-1,0},{0,1},{0,-1}};
    static final int[][] DIAG = {{1,1},{1,-1},{-1,1},{-1,-1}};

    private static int at(int[] b, int r, int f) {
        return r < 0 || r > 7 || f < 0 || f > 7 ? 9 : b[r * 8 + f];
    }

    private static int ray(int[] b, int r, int f, int[] d) {
        r += d[0]; f += d[1];
        while (r >= 0 && r < 8 && f >= 0 && f < 8) {
            if (b[r * 8 + f] != 0) return b[r * 8 + f];
            r += d[0]; f += d[1];
        }
        return 0;
    }

    static boolean attacked(int[] b, int sq, int by) {
        int r = sq >> 3, f = sq & 7, pr = r - by;
        if (pr >= 0 && pr < 8) {
            if (f > 0 && b[pr * 8 + f - 1] == by) return true;
            if (f < 7 && b[pr * 8 + f + 1] == by) return true;
        }
        for (int[] d : N) if (at(b, r + d[0], f + d[1]) == 2 * by) return true;
        for (int[] d : K) if (at(b, r + d[0], f + d[1]) == 6 * by) return true;
        for (int[] d : ORTH) { int q = ray(b, r, f, d); if (q == 4 * by || q == 5 * by) return true; }
        for (int[] d : DIAG) { int q = ray(b, r, f, d); if (q == 3 * by || q == 5 * by) return true; }
        return false;
    }
}
