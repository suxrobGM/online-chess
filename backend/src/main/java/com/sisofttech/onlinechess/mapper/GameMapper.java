package com.sisofttech.onlinechess.mapper;

import com.sisofttech.onlinechess.dto.GameDto;
import com.sisofttech.onlinechess.model.Game;

public class GameMapper {
    public static GameDto toDto(Game game) {
        var dto = new GameDto();
        dto.setId(game.getId().toString());
        dto.setWhitePlayerId(game.getWhitePlayer().getId().toString());
        dto.setBlackPlayerId(game.getBlackPlayer().getId().toString());
        dto.setWinnerPlayerId(game.getWinnerPlayer() != null ? game.getWinnerPlayer().getId().toString() : null);
        dto.setStatus(game.getStatus().name());
        dto.setTurnPlayerId(game.getCurrentTurnPlayerId().toString());
        dto.setCreatedAt(game.getCreatedAt().toString());
        return dto;
    }
}
