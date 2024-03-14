package com.silyosbekov.chessmate.mapper;

import com.silyosbekov.chessmate.dto.GameDto;
import com.silyosbekov.chessmate.model.Game;

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

        return new GameDto(
                game.getId().toString(),
                game.getWhitePlayerId().toString(),
                game.getWhitePlayerUsername(),
                game.getWhitePlayer() != null ? game.getWhitePlayer().getElo() : 0,
                game.getBlackPlayerId().toString(),
                game.getBlackPlayerUsername(),
                game.getBlackPlayer() != null ? game.getBlackPlayer().getElo() : 0,
                game.getWinnerPlayerId().toString(),
                game.getStatus().name(),
                game.getCurrentTurnPlayerId().toString(),
                game.isRanked(),
                game.isTimerEnabled(),
                game.getPgn(),
                game.getCreatedDate().toString()
        );
    }
}
