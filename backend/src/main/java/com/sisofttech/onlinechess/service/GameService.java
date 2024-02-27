package com.sisofttech.onlinechess.service;

import com.sisofttech.onlinechess.model.Game;
import com.sisofttech.onlinechess.repository.GameRepository;
import com.sisofttech.onlinechess.repository.PlayerRepository;
import org.springframework.stereotype.Service;
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
     * @return The newly created game
     */
    public Game createGame(UUID whitePlayerId) {
        var player = playerRepository.findById(whitePlayerId).orElseThrow();
        var game = new Game();
        game.setWhitePlayer(player);
        game.setStatus(Game.GameStatus.OPEN);
        return gameRepository.save(game);
    }

    /**
     * Join a game as the black player
     * @param gameId The ID of the game to join
     * @param blackPlayerId The ID of the player who will play as black
     * @return The updated game
     */
    public Game joinGame(UUID gameId, UUID blackPlayerId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        if (game.getBlackPlayer() != null) {
            throw new IllegalStateException("Game is already full.");
        }

        var player = playerRepository.findById(blackPlayerId).orElseThrow();
        game.setBlackPlayer(player);
        game.setStatus(Game.GameStatus.ONGOING);
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
            game.setWinner(winner);
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
        game.setWinner(winner);
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

