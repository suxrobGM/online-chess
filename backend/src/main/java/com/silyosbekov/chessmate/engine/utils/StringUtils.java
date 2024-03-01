package com.silyosbekov.chessmate.engine.utils;

/**
 * Contains utility functions for strings.
 */
public final class StringUtils {
    private StringUtils() {}

    /**
     * Counts the number of occurrences of a substring in a string.
     * @param str string
     * @param sub substring
     * @return number of occurrences
     */
    public static int countMatches(String str, String sub) {
        if (str.isEmpty() || sub.isEmpty()) {
            return 0;
        }

        var count = 0;
        var lastIndex = 0;

        while (lastIndex != -1) {
            lastIndex = str.indexOf(str, lastIndex);

            if(lastIndex != -1){
                count++;
                lastIndex += str.length();
            }
        }
        return count;
    }

    /**
     * Checks if a string is null or empty.
     * @param str string
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if a string contains a digit.
     * @param str string
     * @return true if the string contains a digit, false otherwise
     */
    public static boolean hasDigit(String str) {
        return str.chars().anyMatch(Character::isDigit);
    }
}
