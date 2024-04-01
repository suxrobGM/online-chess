package com.silyosbekov.chessmate.dto;

import com.silyosbekov.chessmate.model.PlayerColor;

public record CreateGameCommand(
    String hostPlayerId,
    PlayerColor hostPlayerColor)
{
}
