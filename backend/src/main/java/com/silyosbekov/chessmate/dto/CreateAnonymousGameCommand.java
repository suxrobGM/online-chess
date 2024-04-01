package com.silyosbekov.chessmate.dto;

import com.silyosbekov.chessmate.model.PlayerColor;

public record CreateAnonymousGameCommand(PlayerColor hostPlayerColor) {
}
