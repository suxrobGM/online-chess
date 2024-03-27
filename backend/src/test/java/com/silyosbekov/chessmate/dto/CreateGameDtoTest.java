package com.silyosbekov.chessmate.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameDtoTest {
    @Test
    void createGameDtoShouldInitializeWithGivenValues() {
        var gameId = "gameId";
        var whitePlayerId = "whitePlayerId";
        var blackPlayerId = "blackPlayerId";
        var createGameDto = new NewGameDto(gameId, whitePlayerId, blackPlayerId);
        assertEquals(whitePlayerId, createGameDto.whitePlayerId());
        assertEquals(blackPlayerId, createGameDto.blackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForWhitePlayerIdWhenInitializedWithNull() {
        var gameId = "gameId";
        var blackPlayerId = "blackPlayerId";
        var createGameDto = new NewGameDto(gameId, null, blackPlayerId);
        assertNull(createGameDto.whitePlayerId());
        assertEquals(blackPlayerId, createGameDto.blackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForBlackPlayerIdWhenInitializedWithNull() {
        var gameId = "gameId";
        var whitePlayerId = "whitePlayerId";
        var createGameDto = new NewGameDto(gameId, whitePlayerId, null);
        assertEquals(whitePlayerId, createGameDto.whitePlayerId());
        assertNull(createGameDto.blackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForBothPlayerIdsWhenInitializedWithNull() {
        var gameId = "gameId";
        var createGameDto = new NewGameDto(gameId, null, null);
        assertNull(createGameDto.whitePlayerId());
        assertNull(createGameDto.blackPlayerId());
    }
}