package com.silyosbekov.chessmate.core;

public record Pair<T1, T2>(T1 item1, T2 item2) {
    public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
        return new Pair<>(first, second);
    }
}
