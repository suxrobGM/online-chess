package com.silyosbekov.chessmate.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlinePlayersService {
    private final Map<UUID, String> players = new ConcurrentHashMap<>();

    public void addPlayer(UUID playerId, String sessionId) {
        players.put(playerId, sessionId);
    }

    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }

    public void removePlayerBySessionId(String sessionId) {
        players.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }

    public String getSessionId(UUID playerId) {
        return players.get(playerId);
    }

    public boolean isPlayerOnline(UUID playerId) {
        return players.containsKey(playerId);
    }
}
