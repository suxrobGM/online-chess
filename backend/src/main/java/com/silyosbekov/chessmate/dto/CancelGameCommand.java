package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record CancelGameCommand(UUID gameId) {
}
