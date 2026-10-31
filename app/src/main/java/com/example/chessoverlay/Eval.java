package com.example.chessoverlay;

final class Eval {
    static final int[] VAL = {0, 100, 320, 330, 500, 900, 0};

    static int eval(Chess c) {
        int s0 = 0, mat = 0;
        for (int sq = 0; sq < 64; sq++) {
            int p = c.b[sq];
            if (p == 0) continue;
            int s = p > 0 ? 1 : -1, a = Math.abs(p), r = sq >> 3, f = sq & 7;
            int rel = s == 1 ? r : 7 - r;
            int cen = 7 - Math.max(Math.abs(2 * f - 7), Math.abs(2 * r - 7));
            int v = VAL[a];
            if (a == 1) v += 4 * rel + 2 * cen;
            else if (a == 2) v += 6 * cen;
            else if (a == 3) v += 3 * cen;
            else if (a == 4) v += rel == 6 ? 15 : 0;
            if (a >= 2 && a <= 5) mat += VAL[a];
            if (a != 6) s0 += s * v;
        }
        for (int i = 0; i < 2; i++) {
            int s = i == 0 ? 1 : -1, k = c.kp[i], r = k >> 3, f = k & 7;
            int rel = s == 1 ? r : 7 - r;
            int cen = 7 - Math.max(Math.abs(2 * f - 7), Math.abs(2 * r - 7));
            s0 += s * (mat > 2400 ? -12 * rel : 6 * cen);
        }
        return s0 * c.side;
    }
}
