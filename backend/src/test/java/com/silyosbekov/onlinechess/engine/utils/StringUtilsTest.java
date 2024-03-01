package com.silyosbekov.onlinechess.engine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    void countMatchesShouldReturnCorrectCountWhenSubStringExistsInString() {
        String str = "hello world hello world";
        String sub = "hello";
        assertEquals(2, StringUtils.countMatches(str, sub));
    }

    @Test
    void countMatchesShouldReturnZeroWhenSubStringDoesNotExistInString() {
        String str = "hello world hello world";
        String sub = "goodbye";
        assertEquals(0, StringUtils.countMatches(str, sub));
    }

    @Test
    void countMatchesShouldReturnZeroWhenStringIsEmpty() {
        String str = "";
        String sub = "hello";
        assertEquals(0, StringUtils.countMatches(str, sub));
    }

    @Test
    void countMatchesShouldReturnZeroWhenSubStringIsEmpty() {
        String str = "hello world hello world";
        String sub = "";
        assertEquals(0, StringUtils.countMatches(str, sub));
    }

    @Test
    void isNullOrEmptyShouldReturnTrueWhenStringIsNull() {
        String str = null;
        assertTrue(StringUtils.isNullOrEmpty(str));
    }

    @Test
    void isNullOrEmptyShouldReturnTrueWhenStringIsEmpty() {
        String str = "";
        assertTrue(StringUtils.isNullOrEmpty(str));
    }

    @Test
    void isNullOrEmptyShouldReturnFalseWhenStringIsNotEmpty() {
        String str = "hello world";
        assertFalse(StringUtils.isNullOrEmpty(str));
    }

    @Test
    void hasDigitShouldReturnTrueWhenStringContainsDigit() {
        String str = "hello world 1";
        assertTrue(StringUtils.hasDigit(str));
    }

    @Test
    void hasDigitShouldReturnFalseWhenStringDoesNotContainDigit() {
        String str = "hello world";
        assertFalse(StringUtils.hasDigit(str));
    }

    @Test
    void hasDigitShouldReturnFalseWhenStringIsEmpty() {
        String str = "";
        assertFalse(StringUtils.hasDigit(str));
    }
}