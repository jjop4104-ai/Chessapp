package com.example.chessoverlay;

public final class Game {
    private Chess c = new Chess();
    private int pending;
    private boolean hasPending;
    private long ver;

    public synchronized void reset() { c = new Chess(); hasPending = false; ver++; }

    public synchronized int sideToMove() { return c.side; }

    private static int sq(String s, int i) {
        char f = s.charAt(i), r = s.charAt(i + 1);
        return (f < 'a' || f > 'h' || r < '1' || r > '8') ? -1 : (r - '1') * 8 + (f - 'a');
    }

    private int parse(String u) {
        if (u.length() < 4) return -1;
        int f = sq(u, 0), t = sq(u, 2);
        if (f < 0 || t < 0) return -1;
        int pr = u.length() > 4 ? "nbrq".indexOf(u.charAt(4)) + 2 : 5;
        if (pr < 2) pr = 5;
        int[] mv = new int[256];
        int n = Gen.legal(c, mv, false);
        for (int i = 0; i < n; i++) {
            int m = mv[i], mp = (m >> 12) & 7;
            if ((m & 63) == f && ((m >> 6) & 63) == t && (mp == 0 || mp == pr)) return m;
        }
        return -1;
    }

    /** Returns null if OK, otherwise an error message. */
    public synchronized String play(String text) {
        String u = text.toLowerCase().replaceAll("[^a-z0-9]", "");
        if (u.isEmpty()) return null;
        int m = parse(u);
        if (m < 0 && hasPending) {
            c.make(pending);
            m = parse(u);
            if (m < 0) c.unmake();
        } else if (m >= 0) {
            hasPending = false;
        }
        if (m < 0) return "Невозможный ход: " + u;
        hasPending = false;
        c.make(m);
        ver++;
        return null;
    }

    /** Returns "Мат", "Пат" or null if the game continues. */
    public synchronized String status() {
        int[] mv = new int[256];
        if (Gen.legal(c, mv, false) > 0) return null;
        int s = c.side;
        return c.attacked(c.kp[s == 1 ? 0 : 1], -s) ? "Мат" : "Пат";
    }

    public String suggest(int ms) {
        Chess copy;
        long v;
        synchronized (this) { copy = c.copy(); v = ver; }
        int m = new Search().best(copy, ms);
        if (m == 0) return null;
        synchronized (this) {
            if (v == ver) { pending = m; hasPending = true; }
        }
        String r = "" + (char) ('a' + (m & 7)) + (char) ('1' + ((m >> 3) & 7))
                + (char) ('a' + ((m >> 6) & 7)) + (char) ('1' + ((m >> 9) & 7));
        int pr = (m >> 12) & 7;
        return pr == 0 ? r : r + "nbrq".charAt(pr - 2);
    }
}
