package com.silyosbekov.chessmate.dto;

public record MoveDto(
        String gameId,
        String from,
        String to,
        String san,
        String pgn)
{
}
