package com.silyosbekov.onlinechess.engine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilsTest {

    @Test
    void containsShouldReturnTrueWhenElementExistsInArray() {
        Integer[] array = {1, 2, 3, 4, 5};
        assertTrue(ArrayUtils.contains(array, 3));
    }

    @Test
    void containsShouldReturnFalseWhenElementDoesNotExistInArray() {
        Integer[] array = {1, 2, 3, 4, 5};
        assertFalse(ArrayUtils.contains(array, 6));
    }

    @Test
    void containsShouldReturnFalseWhenArrayIsEmpty() {
        Integer[] array = {};
        assertFalse(ArrayUtils.contains(array, 1));
    }

    @Test
    void containsShouldReturnFalseWhenArrayIsNull() {
        Integer[] array = null;
        assertFalse(ArrayUtils.contains(array, 1));
    }

    @Test
    void containsShouldReturnTrueWhenElementIsNullAndArrayContainsNull() {
        Integer[] array = {1, null, 3};
        assertTrue(ArrayUtils.contains(array, null));
    }

    @Test
    void containsShouldReturnFalseWhenElementIsNullAndArrayDoesNotContainNull() {
        Integer[] array = {1, 2, 3};
        assertFalse(ArrayUtils.contains(array, null));
    }
}