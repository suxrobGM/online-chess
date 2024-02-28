package com.sisofttech.onlinechess.engine;

public record Tuple<T1, T2>(T1 item1, T2 item2) {
    public static <T1, T2> Tuple<T1, T2> of(T1 item1, T2 item2) {
        return new Tuple<>(item1, item2);
    }
}
