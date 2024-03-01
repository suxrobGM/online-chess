package com.silyosbekov.chessmate.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameDtoTest {
    @Test
    void createGameDtoShouldInitializeWithGivenValues() {
        var whitePlayerId = "whitePlayerId";
        var blackPlayerId = "blackPlayerId";
        var createGameDto = new CreateGameDto(whitePlayerId, blackPlayerId);
        assertEquals(whitePlayerId, createGameDto.getWhitePlayerId());
        assertEquals(blackPlayerId, createGameDto.getBlackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForWhitePlayerIdWhenInitializedWithNull() {
        var blackPlayerId = "blackPlayerId";
        var createGameDto = new CreateGameDto(null, blackPlayerId);
        assertNull(createGameDto.getWhitePlayerId());
        assertEquals(blackPlayerId, createGameDto.getBlackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForBlackPlayerIdWhenInitializedWithNull() {
        var whitePlayerId = "whitePlayerId";
        var createGameDto = new CreateGameDto(whitePlayerId, null);
        assertEquals(whitePlayerId, createGameDto.getWhitePlayerId());
        assertNull(createGameDto.getBlackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForBothPlayerIdsWhenInitializedWithNull() {
        var createGameDto = new CreateGameDto(null, null);
        assertNull(createGameDto.getWhitePlayerId());
        assertNull(createGameDto.getBlackPlayerId());
    }
}