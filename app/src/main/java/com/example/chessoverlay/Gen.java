package com.example.chessoverlay;

final class Gen {
    private static int promo(int[] mv, int n, int f, int t) {
        for (int pc = 5; pc >= 2; pc--) mv[n++] = f | t << 6 | pc << 12;
        return n;
    }

    static int gen(Chess c, int[] mv, boolean caps) {
        int n = 0, s = c.side;
        int[] b = c.b;
        for (int sq = 0; sq < 64; sq++) {
            int p = b[sq];
            if (p * s <= 0) continue;
            int a = Math.abs(p), r = sq >> 3, f = sq & 7;
            if (a == 1) {
                int fr = r + s;
                if (fr < 0 || fr > 7) continue;
                int t = fr * 8 + f;
                boolean pro = fr == 0 || fr == 7;
                if (b[t] == 0) {
                    if (pro) n = promo(mv, n, sq, t);
                    else if (!caps) {
                        mv[n++] = sq | t << 6;
                        if (r == (s == 1 ? 1 : 6) && b[t + 8 * s] == 0) mv[n++] = sq | (t + 8 * s) << 6;
                    }
                }
                for (int d = -1; d <= 1; d += 2) {
                    int ff = f + d;
                    if (ff < 0 || ff > 7) continue;
                    int tt = fr * 8 + ff;
                    if (b[tt] * s < 0 || tt == c.ep) {
                        if (pro) n = promo(mv, n, sq, tt); else mv[n++] = sq | tt << 6;
                    }
                }
            } else if (a == 2 || a == 6) {
                for (int[] d : a == 2 ? Att.N : Att.K) {
                    int rr = r + d[0], ff = f + d[1];
                    if (rr < 0 || rr > 7 || ff < 0 || ff > 7) continue;
                    int t = rr * 8 + ff;
                    if (b[t] * s > 0 || (caps && b[t] == 0)) continue;
                    mv[n++] = sq | t << 6;
                }
            } else {
                for (int[] d : a == 3 ? Att.DIAG : a == 4 ? Att.ORTH : Att.K) {
                    int rr = r + d[0], ff = f + d[1];
                    while (rr >= 0 && rr < 8 && ff >= 0 && ff < 8) {
                        int t = rr * 8 + ff;
                        if (b[t] * s > 0) break;
                        if (b[t] == 0) { if (!caps) mv[n++] = sq | t << 6; }
                        else { mv[n++] = sq | t << 6; break; }
                        rr += d[0]; ff += d[1];
                    }
                }
            }
        }
        if (!caps) n = Castle.add(c, mv, n);
        return n;
    }

    static int legal(Chess c, int[] mv, boolean caps) {
        int n = gen(c, mv, caps), k = 0, s = c.side;
        for (int i = 0; i < n; i++) {
            c.make(mv[i]);
            if (!c.attacked(c.kp[s == 1 ? 0 : 1], -s)) mv[k++] = mv[i];
            c.unmake();
        }
        return k;
    }
}
