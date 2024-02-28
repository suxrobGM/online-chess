package com.sisofttech.onlinechess.engine.constants;

import com.sisofttech.onlinechess.engine.Tuple;

public final class Flags {
    public static final Tuple<Character, Integer> NORMAL = new Tuple<>('n', 1);
    public static final Tuple<Character, Integer> CAPTURE = new Tuple<>('c', 2);
    public static final Tuple<Character, Integer> BIG_PAWN = new Tuple<>('b', 4);
    public static final Tuple<Character, Integer> EP_CAPTURE = new Tuple<>('e', 8);
    public static final Tuple<Character, Integer> PROMOTION = new Tuple<>('p', 16);
    public static final Tuple<Character, Integer> KSIDE_CASTLE = new Tuple<>('k', 32);
    public static final Tuple<Character, Integer> QSIDE_CASTLE = new Tuple<>('q', 64);
}
