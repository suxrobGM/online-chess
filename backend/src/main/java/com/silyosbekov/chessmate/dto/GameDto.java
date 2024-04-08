package com.silyosbekov.chessmate.dto;

import java.time.Instant;
import java.util.UUID;

public record GameDto(
        UUID id,
        UUID whitePlayerId,
        String whitePlayerUsername,
        int whitePlayerElo,
        UUID blackPlayerId,
        String blackPlayerUsername,
        int blackPlayerElo,
        UUID winnerPlayerId,
        String status,
        UUID currentTurnPlayerId,
        boolean isRanked,
        boolean isTimerEnabled,
        String pgn,
        Instant createdDate)
{
}
