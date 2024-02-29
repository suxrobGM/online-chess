package com.sisofttech.onlinechess.engine.utils;

import java.util.Arrays;

/**
 * Contains utility functions for arrays.
 */
public final class ArrayUtils {
    private ArrayUtils() {}

    /**
     * Concatenates two arrays.
     * @param array1 first array
     * @param array2 second array
     * @return concatenated array
     */
    public static <T> T[] concat(T[] array1, T[] array2) {
        var result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public static <T> boolean contains(T[] array, T value) {
       for (var element : array) {
           if (element.equals(value)) {
               return true;
           }
       }

       return false;
    }
}
