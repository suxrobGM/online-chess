package com.silyosbekov.onlinechess.mappers;

import com.silyosbekov.onlinechess.dto.GameDto;
import com.silyosbekov.onlinechess.models.Game;

/**
 * Mapper for Game entity. It maps Game entity to GameDto and vice versa.
 */
public final class GameMapper {
    private GameMapper() {
    }

    /**
     * Maps GameDto to Game entity.
     * @param game Game entity
     * @return GameDto
     */
    public static GameDto toDto(Game game) {
        if (game == null) {
            return null;
        }

        var dto = new GameDto();
        dto.setId(game.getId().toString());
        dto.setWhitePlayerId(game.getWhitePlayer().getId().toString());
        dto.setBlackPlayerId(game.getBlackPlayer().getId().toString());
        dto.setWinnerPlayerId(game.getWinnerPlayer() != null ? game.getWinnerPlayer().getId().toString() : null);
        dto.setStatus(game.getStatus().name());
        dto.setTurnPlayerId(game.getCurrentTurnPlayerId().toString());
        dto.setCreatedDate(game.getCreatedDate().toString());
        return dto;
    }
}
