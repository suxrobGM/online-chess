package com.silyosbekov.chessmate.dto;

import java.util.UUID;

public record LeaveGameCommand(UUID gameId, UUID playerId) {
}
