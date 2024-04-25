package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record AcceptDrawCommand(
        UUID gameId,
        UUID playerId)
{
}
