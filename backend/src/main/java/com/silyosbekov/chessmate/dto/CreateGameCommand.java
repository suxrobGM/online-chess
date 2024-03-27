package com.silyosbekov.chessmate.dto;

public record CreateGameCommand(
    String whitePlayerId,
    String blackPlayerId)
{
}
