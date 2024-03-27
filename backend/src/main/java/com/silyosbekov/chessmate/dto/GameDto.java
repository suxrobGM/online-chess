package com.silyosbekov.chessmate.dto;

public record GameDto(
    String id,
    String whitePlayerId,
    String whitePlayerUsername,
    int whitePlayerElo,
    String blackPlayerId,
    String blackPlayerUsername,
    int blackPlayerElo,
    String winnerPlayerId,
    String status,
    String currentTurnPlayerId,
    boolean isRanked,
    boolean isTimerEnabled,
    String pgn,
    String createdDate)
{
}
