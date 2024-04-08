package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record MoveDto(
        UUID gameId,
        String from,
        String to,
        String san,
        String pgn)
{
}
