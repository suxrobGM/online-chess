package com.silyosbekov.chessmate.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameDtoTest {
    @Test
    void createGameDtoShouldInitializeWithGivenValues() {
        var whitePlayerId = "whitePlayerId";
        var blackPlayerId = "blackPlayerId";
        var createGameDto = new NewGameDto(whitePlayerId, blackPlayerId);
        assertEquals(whitePlayerId, createGameDto.whitePlayerId());
        assertEquals(blackPlayerId, createGameDto.blackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForWhitePlayerIdWhenInitializedWithNull() {
        var blackPlayerId = "blackPlayerId";
        var createGameDto = new NewGameDto(null, blackPlayerId);
        assertNull(createGameDto.whitePlayerId());
        assertEquals(blackPlayerId, createGameDto.blackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForBlackPlayerIdWhenInitializedWithNull() {
        var whitePlayerId = "whitePlayerId";
        var createGameDto = new NewGameDto(whitePlayerId, null);
        assertEquals(whitePlayerId, createGameDto.whitePlayerId());
        assertNull(createGameDto.blackPlayerId());
    }

    @Test
    void createGameDtoShouldReturnNullForBothPlayerIdsWhenInitializedWithNull() {
        var createGameDto = new NewGameDto(null, null);
        assertNull(createGameDto.whitePlayerId());
        assertNull(createGameDto.blackPlayerId());
    }
}