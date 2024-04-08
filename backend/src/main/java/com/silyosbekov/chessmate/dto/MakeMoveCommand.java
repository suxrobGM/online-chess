package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record MakeMoveCommand(
        UUID gameId,
        String from,
        String to)
{
}
