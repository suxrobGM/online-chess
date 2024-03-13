package com.silyosbekov.chessmate.engine.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Contains encoding and decoding utility functions.
 */
public final class EncodingUtils {
    private EncodingUtils() {}

    /**
     * Encodes a string to hexadecimal.
     * @param str string
     * @return hexadecimal representation
     */
    public static String encodeHex(String str) {
        var result = new StringBuilder();
        for (var c : str.toCharArray()) {
            if (c < 128) {
                result.append(Integer.toHexString(c));
            }
            else {
                result.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8)
                        .replace("%", "")
                        .toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * Decodes a hexadecimal string.
     * @param str hexadecimal string
     * @return decoded string
     */
    public static String decodeHex(String str) {
        if (str.isEmpty()) {
            return "";
        }

        var parts = str.split("(?<=\\G.{2})");
        var result = new StringBuilder();

        for (var part : parts) {
            result.append(URLDecoder.decode("%" + part, StandardCharsets.UTF_8));
        }

        return result.toString();
    }

    /**
     * Encodes a PGN comment string.
     * @param str comment string
     * @param newline newline character
     * @return encoded comment
     */
    public static String encodeComment(String str, String newline) {
        if (StringUtils.isNullOrEmpty(str)) {
            return "{}";
        }

        // var maskedNewline = newline.replace("\\", "\\\\");
        str = str.replace(newline, " ");
        return "{" + encodeHex(str.substring(1, str.length() - 1)) + "}";
    }

    /**
     * Decodes a PGN comment string.
     * @param s comment string
     * @return decoded comment
     */
    public static String decodeComment(String s) {
        if (s.startsWith("{") && s.endsWith("}")) {
            return decodeHex(s.substring(1, s.length() - 1));
        }
        return "";
    }
}
