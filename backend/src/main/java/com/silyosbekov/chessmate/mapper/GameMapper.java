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
                game.getId(),
                game.getHostPlayerId(),
                game.getHostPlayerUsername(),
                game.getHostPlayerColor(),
                game.getHostPlayerElo(),
                game.getWhitePlayerId(),
                game.getWhitePlayerUsername(),
                game.getWhitePlayerElo(),
                game.getBlackPlayerId(),
                game.getBlackPlayerUsername(),
                game.getBlackPlayerElo(),
                game.getWinnerPlayer(),
                game.getStatus().name(),
                game.getCurrentTurn(),
                game.isRanked(),
                game.isTimerEnabled(),
                game.getPgn(),
                game.getCreatedDate()
        );
    }
}
