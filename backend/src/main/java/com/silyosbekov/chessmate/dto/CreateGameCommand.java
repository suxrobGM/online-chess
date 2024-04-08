package com.silyosbekov.chessmate.dto;

import com.silyosbekov.chessmate.model.PlayerColor;
import java.util.UUID;

public record CreateGameCommand(
    UUID hostPlayerId,
    PlayerColor hostPlayerColor)
{
}
