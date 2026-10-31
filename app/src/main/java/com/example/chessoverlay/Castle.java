package com.example.chessoverlay;

final class Castle {
    static int add(Chess c, int[] mv, int n) {
        int s = c.side, base = s == 1 ? 0 : 56, kb = s == 1 ? 1 : 4, qb = s == 1 ? 2 : 8;
        int[] b = c.b;
        if (b[base + 4] != 6 * s || c.attacked(base + 4, -s)) return n;
        if ((c.castle & kb) != 0 && b[base + 5] == 0 && b[base + 6] == 0
                && b[base + 7] == 4 * s && !c.attacked(base + 5, -s))
            mv[n++] = (base + 4) | (base + 6) << 6;
        if ((c.castle & qb) != 0 && b[base + 1] == 0 && b[base + 2] == 0 && b[base + 3] == 0
                && b[base] == 4 * s && !c.attacked(base + 3, -s))
            mv[n++] = (base + 4) | (base + 2) << 6;
        return n;
    }
}
