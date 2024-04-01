package com.silyosbekov.chessmate.dto;

public record MakeMoveCommand(
    String gameId,
    String from,
    String to)
{
}
