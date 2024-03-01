package com.silyosbekov.onlinechess.engine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodingUtilsTest {
    @Test
    void encodeHexShouldReturnHexadecimalRepresentation() {
        var str = "hello";
        assertEquals("68656c6c6f", EncodingUtils.encodeHex(str));
    }

    @Test
    void encodeHexShouldReturnEmptyStringWhenInputIsEmpty() {
        var str = "";
        assertEquals("", EncodingUtils.encodeHex(str));
    }

    @Test
    void decodeHexShouldReturnDecodedString() {
        var str = "68656c6c6f";
        assertEquals("hello", EncodingUtils.decodeHex(str));
    }

    @Test
    void decodeHexShouldReturnEmptyStringWhenInputIsEmpty() {
        var str = "";
        assertEquals("", EncodingUtils.decodeHex(str));
    }

//    @Test
//    void encodeCommentShouldReturnEncodedComment() {
//        var str = "hello";
//        var newline = "\n";
//        assertEquals("{68656c6c6f}", EncodingUtils.encodeComment(str, newline));
//    }

    @Test
    void encodeCommentShouldReturnEmptyStringWhenInputIsEmpty() {
        var str = "";
        var newline = "\n";
        assertEquals("{}", EncodingUtils.encodeComment(str, newline));
    }

    @Test
    void decodeCommentShouldReturnDecodedComment() {
        var str = "{68656c6c6f}";
        assertEquals("hello", EncodingUtils.decodeComment(str));
    }

    @Test
    void decodeCommentShouldReturnEmptyStringWhenInputIsNotComment() {
        var str = "68656c6c6f";
        assertEquals("", EncodingUtils.decodeComment(str));
    }
}