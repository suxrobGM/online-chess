package com.sisofttech.onlinechess.engine.utils;

/**
 * This class contains utility methods for strings.
 */
public final class StringUtils {
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
}
