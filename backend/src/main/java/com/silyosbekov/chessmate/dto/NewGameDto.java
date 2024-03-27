package com.silyosbekov.chessmate.dto;

public record NewGameDto(
    String gameId,
    String whitePlayerId,
    String blackPlayerId)
{
}
