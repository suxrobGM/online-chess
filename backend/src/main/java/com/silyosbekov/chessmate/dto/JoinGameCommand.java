package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record JoinGameCommand(UUID gameId, UUID playerId) {
}
