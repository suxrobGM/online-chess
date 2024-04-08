package com.silyosbekov.chessmate.dto;

import com.silyosbekov.chessmate.model.PlayerColor;
import java.time.Instant;
import java.util.UUID;

public record GameDto(
        UUID id,
        UUID hostPlayerId,
        String hostPlayerUsername,
        PlayerColor hostPlayerColor,
        int hostPlayerElo,
        UUID whitePlayerId,
        String whitePlayerUsername,
        int whitePlayerElo,
        UUID blackPlayerId,
        String blackPlayerUsername,
        int blackPlayerElo,
        PlayerColor winnerPlayer,
        String status,
        PlayerColor currentTurn,
        boolean isRanked,
        boolean isTimerEnabled,
        String pgn,
        Instant createdDate)
{
}
