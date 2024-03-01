package com.silyosbekov.chessmate.mappers;

import com.silyosbekov.chessmate.engine.utils.ArrayUtils;
import com.silyosbekov.chessmate.models.Game;
import com.silyosbekov.chessmate.models.Player;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    @Test
    void concatShouldReturnCombinedArrayWhenBothArraysAreNotEmpty() {
        Integer[] array1 = {1, 2, 3};
        Integer[] array2 = {4, 5, 6};
        Integer[] expected = {1, 2, 3, 4, 5, 6};
        assertArrayEquals(expected, ArrayUtils.concat(array1, array2));
    }

    @Test
    void concatShouldReturnFirstArrayWhenSecondArrayIsEmpty() {
        Integer[] array1 = {1, 2, 3};
        Integer[] array2 = {};
        Integer[] expected = {1, 2, 3};
        assertArrayEquals(expected, ArrayUtils.concat(array1, array2));
    }

    @Test
    void concatShouldReturnSecondArrayWhenFirstArrayIsEmpty() {
        Integer[] array1 = {};
        Integer[] array2 = {4, 5, 6};
        Integer[] expected = {4, 5, 6};
        assertArrayEquals(expected, ArrayUtils.concat(array1, array2));
    }

    @Test
    void concatShouldReturnEmptyArrayWhenBothArraysAreEmpty() {
        Integer[] array1 = {};
        Integer[] array2 = {};
        Integer[] expected = {};
        assertArrayEquals(expected, ArrayUtils.concat(array1, array2));
    }

    @Test
    void concatShouldReturnCombinedArrayWhenArraysContainNull() {
        Integer[] array1 = {1, null, 3};
        Integer[] array2 = {null, 5, 6};
        Integer[] expected = {1, null, 3, null, 5, 6};
        assertArrayEquals(expected, ArrayUtils.concat(array1, array2));
    }

    @Test
    void toDto_ShouldMapGameToDto_WhenGameIsNotNull() {
        // Arrange
        var game = new Game();
        game.setWhitePlayer(new Player());
        game.setBlackPlayer(new Player());
        game.setWinnerPlayer(new Player());
        game.setStatus(Game.GameStatus.OPEN);
        game.setCurrentTurnPlayerId(UUID.randomUUID());

        // Act
        var result = GameMapper.toDto(game);

        // Assert
        assertNotNull(result);
        assertEquals(game.getId().toString(), result.getId());
        assertEquals(game.getWhitePlayer().getId().toString(), result.getWhitePlayerId());
        assertEquals(game.getBlackPlayer().getId().toString(), result.getBlackPlayerId());
        assertEquals(game.getWinnerPlayer().getId().toString(), result.getWinnerPlayerId());
        assertEquals(game.getStatus().name(), result.getStatus());
        assertEquals(game.getCurrentTurnPlayerId().toString(), result.getTurnPlayerId());
        assertEquals(game.getCreatedDate().toString(), result.getCreatedDate());
    }

    @Test
    void toDto_ShouldReturnNull_WhenGameIsNull() {
        // Act
        var result = GameMapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDto_ShouldMapWinnerPlayerIdToNull_WhenWinnerPlayerIsNull() {
        // Arrange
        var game = new Game();
        game.setWhitePlayer(new Player());
        game.setBlackPlayer(new Player());
        game.setWinnerPlayer(null);
        game.setStatus(Game.GameStatus.OPEN);
        game.setCurrentTurnPlayerId(UUID.randomUUID());

        // Act
        var result = GameMapper.toDto(game);

        // Assert
        assertNotNull(result);
        assertNull(result.getWinnerPlayerId());
    }
}