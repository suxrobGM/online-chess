package com.sisofttech.onlinechess.services;

import com.sisofttech.onlinechess.models.Game;
import com.sisofttech.onlinechess.models.Player;
import com.sisofttech.onlinechess.repositories.GameRepository;
import com.sisofttech.onlinechess.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Create a new game
     * @param whitePlayerId The ID of the player who will play as white
     * @param blackPlayerId The ID of the player who will play as black
     * @throws NoSuchElementException if the white player does not exist, or if the black player does not exist
     * @return The newly created game
     */
    public Game createGame(UUID whitePlayerId, UUID blackPlayerId) {
        var whitePlayer = playerRepository.findById(whitePlayerId).orElseThrow();
        Player blackPlayer = null;

        if (blackPlayerId != null) {
            blackPlayer = playerRepository.findById(blackPlayerId).orElseThrow();
        }

        var game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setCurrentTurnPlayerId(whitePlayer.getId());
        game.setStatus(Game.GameStatus.OPEN);
        return gameRepository.save(game);
    }

    /**
     * Join a game as the black player
     * @param gameId The ID of the game to join
     * @param blackPlayerId The ID of the player who will play as black
     * @throws IllegalStateException if the game is already full
     * @return The updated game
     */
    public Game joinGame(UUID gameId, UUID blackPlayerId) {
        var game = gameRepository.findById(gameId).orElseThrow();

        if (game.getBlackPlayer() != null) {
            throw new IllegalStateException("Game is already full.");
        }

        var player = playerRepository.findById(blackPlayerId).orElseThrow();
        game.setBlackPlayer(player);
        game.setCurrentTurnPlayerId(player.getId());
        game.setStatus(Game.GameStatus.ONGOING);
        return gameRepository.save(game);
    }

    /**
     * Set the current turn player
     * @param gameId The ID of the game
     * @param playerId The ID of the player whose turn it is
     * @return The updated game
     */
    public Game setCurrentTurnPlayer(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        game.setCurrentTurnPlayerId(playerId);
        return gameRepository.save(game);
    }

    /**
     * Complete a game
     * @param gameId The ID of the game to complete
     * @param winnerId The ID of the winning player
     * @return The updated game
     */
    public Game completeGame(UUID gameId, UUID winnerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        game.setStatus(Game.GameStatus.COMPLETED);

        if (winnerId != null) {
            var winner = playerRepository.findById(winnerId).orElseThrow();
            game.setWinnerPlayer(winner);
        }

        return gameRepository.save(game);
    }

    /**
     * Resign from a game
     * @param gameId The ID of the game to resign from
     * @param playerId The ID of the player who is resigning
     * @return The updated game
     */
    public Game resignGame(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        game.setStatus(Game.GameStatus.RESIGNED);
        var winner = game.getWhitePlayer().getId().equals(playerId) ? game.getBlackPlayer() : game.getWhitePlayer();
        game.setWinnerPlayer(winner);
        return gameRepository.save(game);
    }

    /**
     * Draw a game
     * @param gameId The ID of the game to draw
     * @return The updated game
     */
    public Game drawGame(UUID gameId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        game.setStatus(Game.GameStatus.DRAW);
        return gameRepository.save(game);
    }
}

