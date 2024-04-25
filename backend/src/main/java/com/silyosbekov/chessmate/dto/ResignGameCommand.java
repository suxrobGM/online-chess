package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record ResignGameCommand(
        UUID gameId,
        UUID playerId)
{
}
