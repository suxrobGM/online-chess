package com.silyosbekov.chessmate.engine.util;

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

    /**
     * Checks if an array contains a value.
     * @param array array
     * @param value value to find
     * @return true if the array contains the value, false otherwise
     */
    public static <T> boolean contains(T[] array, T value) {
        if (array == null) {
            return false;
        }

        for (var element : array) {
            if (element == null && value == null) {
                return true;
            }

            if (element != null && element.equals(value)) {
                return true;
            }
        }

        return false;
    }
}
