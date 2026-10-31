package com.example.chessoverlay;

final class Chess {
    static final int[] MASK = new int[64];
    static {
        java.util.Arrays.fill(MASK, 15);
        MASK[0] = ~2; MASK[7] = ~1; MASK[56] = ~8; MASK[63] = ~4;
        MASK[4] = ~3; MASK[60] = ~12;
    }

    int[] b = new int[64];
    int side = 1, castle = 15, ep = -1, sp = 0;
    int[] kp = {4, 60};
    int[] uMove = new int[600], uCap = new int[600], uCastle = new int[600], uEp = new int[600];
    boolean[] uEpFlag = new boolean[600];

    Chess() {
        int[] back = {4, 2, 3, 5, 6, 3, 2, 4};
        for (int f = 0; f < 8; f++) {
            b[f] = back[f]; b[8 + f] = 1; b[48 + f] = -1; b[56 + f] = -back[f];
        }
    }

    Chess copy() {
        Chess c = new Chess();
        c.b = b.clone(); c.side = side; c.castle = castle; c.ep = ep; c.kp = kp.clone();
        return c;
    }

    void make(int m) {
        int f = m & 63, t = (m >> 6) & 63, pr = (m >> 12) & 7;
        int p = b[f], s = side, a = Math.abs(p), cap = b[t];
        boolean e = a == 1 && (f & 7) != (t & 7) && cap == 0;
        if (e) { cap = b[t - 8 * s]; b[t - 8 * s] = 0; }
        uMove[sp] = m; uCap[sp] = cap; uCastle[sp] = castle; uEp[sp] = ep; uEpFlag[sp] = e;
        sp++;
        b[t] = pr != 0 ? pr * s : p;
        b[f] = 0;
        if (a == 6) {
            kp[s == 1 ? 0 : 1] = t;
            if (t - f == 2) { b[f + 1] = b[f + 3]; b[f + 3] = 0; }
            else if (f - t == 2) { b[f - 1] = b[f - 4]; b[f - 4] = 0; }
        }
        castle &= MASK[f] & MASK[t];
        ep = (a == 1 && Math.abs(t - f) == 16) ? (f + t) / 2 : -1;
        side = -s;
    }

    void unmake() {
        sp--;
        int m = uMove[sp], f = m & 63, t = (m >> 6) & 63, pr = (m >> 12) & 7;
        side = -side;
        int s = side, p = pr != 0 ? s : b[t];
        b[f] = p;
        if (uEpFlag[sp]) { b[t] = 0; b[t - 8 * s] = uCap[sp]; } else b[t] = uCap[sp];
        if (Math.abs(p) == 6) {
            kp[s == 1 ? 0 : 1] = f;
            if (t - f == 2) { b[f + 3] = b[f + 1]; b[f + 1] = 0; }
            else if (f - t == 2) { b[f - 4] = b[f - 1]; b[f - 1] = 0; }
        }
        castle = uCastle[sp]; ep = uEp[sp];
    }

    boolean attacked(int sq, int by) { return Att.attacked(b, sq, by); }
          }
