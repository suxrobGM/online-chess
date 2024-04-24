package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record MoveDto(
        UUID gameId,
        UUID whitePlayerId,
        UUID blackPlayerId,
        String from,
        String to,
        String san,
        String pgn)
{
}
