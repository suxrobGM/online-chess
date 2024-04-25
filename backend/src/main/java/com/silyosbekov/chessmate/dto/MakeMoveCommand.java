package com.silyosbekov.chessmate.dto;

import com.silyosbekov.chessmate.model.PlayerColor;

import java.util.UUID;

public record MakeMoveCommand(
        UUID gameId,
        PlayerColor color,
        String from,
        String to,
        boolean isCheckmate,
        boolean isStalemate)
{
}
