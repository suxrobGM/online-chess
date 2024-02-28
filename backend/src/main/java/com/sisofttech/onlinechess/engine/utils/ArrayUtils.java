package com.sisofttech.onlinechess.engine.utils;

import java.util.Arrays;

public final class ArrayUtils {
    public static <T> T[] concat(T[] array1, T[] array2) {
        var result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
}
