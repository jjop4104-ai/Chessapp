package com.example.chessoverlay;

final class Order {
    static void order(Chess c, int[] mv, int n, int[] k, int first) {
        for (int i = 0; i < n; i++) {
            int m = mv[i], f = m & 63, t = (m >> 6) & 63, pr = (m >> 12) & 7;
            int s = 0, cap = Math.abs(c.b[t]);
            if (m == first) s = 1 << 20;
            else {
                if (cap != 0) s = 10000 + 10 * Eval.VAL[cap] - Eval.VAL[Math.abs(c.b[f])] / 10;
                if (pr != 0) s += 5000 + Eval.VAL[pr];
            }
            k[i] = s;
        }
        for (int i = 1; i < n; i++) {
            int m = mv[i], s = k[i], j = i - 1;
            while (j >= 0 && k[j] < s) { mv[j + 1] = mv[j]; k[j + 1] = k[j]; j--; }
            mv[j + 1] = m; k[j + 1] = s;
        }
    }
}
