package com.silyosbekov.chessmate.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDtoTest {
    @Test
    void gameDtoShouldInitializeWithGivenValues() {
        var gameDto = new GameDto();
        var id = "id";
        var whitePlayerId = "whitePlayerId";
        var blackPlayerId = "blackPlayerId";
        var winnerPlayerId = "winnerPlayerId";
        var status = "status";
        var turnPlayerId = "turnPlayerId";
        var createdDate = "createdDate";

        gameDto.setId(id);
        gameDto.setWhitePlayerId(whitePlayerId);
        gameDto.setBlackPlayerId(blackPlayerId);
        gameDto.setWinnerPlayerId(winnerPlayerId);
        gameDto.setStatus(status);
        gameDto.setTurnPlayerId(turnPlayerId);
        gameDto.setCreatedDate(createdDate);

        assertEquals(id, gameDto.getId());
        assertEquals(whitePlayerId, gameDto.getWhitePlayerId());
        assertEquals(blackPlayerId, gameDto.getBlackPlayerId());
        assertEquals(winnerPlayerId, gameDto.getWinnerPlayerId());
        assertEquals(status, gameDto.getStatus());
        assertEquals(turnPlayerId, gameDto.getTurnPlayerId());
        assertEquals(createdDate, gameDto.getCreatedDate());
    }

    @Test
    void gameDtoShouldReturnNullForAllFieldsWhenNotSet() {
        var gameDto = new GameDto();

        assertNull(gameDto.getId());
        assertNull(gameDto.getWhitePlayerId());
        assertNull(gameDto.getBlackPlayerId());
        assertNull(gameDto.getWinnerPlayerId());
        assertNull(gameDto.getStatus());
        assertNull(gameDto.getTurnPlayerId());
        assertNull(gameDto.getCreatedDate());
    }
}