package com.silyosbekov.chessmate.service;

import com.silyosbekov.chessmate.engine.Pgn;
import com.silyosbekov.chessmate.model.Game;
import com.silyosbekov.chessmate.model.GameStatus;
import com.silyosbekov.chessmate.model.PlayerColor;
import com.silyosbekov.chessmate.repository.GameRepository;
import com.silyosbekov.chessmate.model.Player;
import com.silyosbekov.chessmate.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

/**
 * Game service class, manages chess game related such as creating a new game, inviting to the game, etc.
 */
@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Get a game by its ID
     * @param gameId The ID of the game
     * @throws NoSuchElementException if the game does not exist
     * @return The game
     */
    public Game getGameById(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow();
    }

    /**
     * Get all games
     * @param gameStatus Filter games by status, or null to get all games
     * @return List of all games
     */
    public List<Game> getGames(GameStatus gameStatus) {
        if (gameStatus != null) {
            return gameRepository.findByStatus(gameStatus);
        }
        return gameRepository.findAll();
    }

    /**
     * Create a new game
     * @param hostPlayerId The ID of the player who will host the game
     * @param hostPlayerColor The color of the host player
     * @throws NoSuchElementException if the host player does not exist
     * @throws IllegalStateException if the host player ID format is invalid UUID
     * @return The newly created game
     */
    public Game createNewGame(UUID hostPlayerId, PlayerColor hostPlayerColor) {
        var hostPlayer = playerRepository.findById(hostPlayerId).orElseThrow();
        return createGameWithHostPlayer(hostPlayer, hostPlayerColor, null);
    }

    /**
     * Create a new anonymous game
     * @param hostPlayerColor The color of the host player
     * @return The newly created game
     */
    public Game createNewAnonymousGame(PlayerColor hostPlayerColor) {
        var hostPlayerId = UUID.randomUUID(); // Generate UUID for anonymous player
        return createGameWithHostPlayer(null, hostPlayerColor, hostPlayerId);
    }

    /**
     * Create a new game with a host player
     * @param hostPlayer The player who will host the game
     * @param hostPlayerColor The color of the host player, or null to randomly assign color to the host player
     * @param anonymousPlayerId The ID of the anonymous player
     * @return The newly created game
     */
    private Game createGameWithHostPlayer(Player hostPlayer, PlayerColor hostPlayerColor, UUID anonymousPlayerId) {
        var game = new Game();

        // Randomly assign color if not specified
        if (hostPlayerColor == null) {
            hostPlayerColor = new Random().nextBoolean() ? PlayerColor.WHITE : PlayerColor.BLACK;
        }

        if (hostPlayerColor == PlayerColor.WHITE) {
            if (hostPlayer != null) {
                game.setWhitePlayer(hostPlayer);
            }
            else {
                game.setWhiteAnonymousPlayerId(anonymousPlayerId);
            }
        }
        else {
            if (hostPlayer != null) {
                game.setBlackPlayer(hostPlayer);
            }
            else {
                game.setBlackAnonymousPlayerId(anonymousPlayerId);
            }
        }

        game.setCurrentTurnPlayerId(game.getWhitePlayerId());
        game.setStatus(GameStatus.OPEN);

        var pgn = new Pgn();
        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Set the current turn player
     * @param gameId The ID of the game
     * @param playerId The ID of the player whose turn it is
     * @throws NoSuchElementException if the player does not exist in the game
     * @throws IllegalArgumentException if game ID or player ID format is invalid UUID
     * @return The updated game
     */
    public Game setCurrentTurnPlayer(String gameId, String playerId) {
        var gameUUID = UUID.fromString(gameId);
        var playerUUID = UUID.fromString(playerId);

        var game = gameRepository.findById(gameUUID).orElseThrow();
        var pgn = Pgn.fromString(game.getPgn());

        if (game.getBlackPlayerId().equals(playerUUID)) {
            game.setCurrentTurnPlayerId(game.getBlackPlayerId());
            pgn.setBlackTurn();
        }
        else if (game.getWhitePlayerId().equals(playerUUID)) {
            game.setCurrentTurnPlayerId(game.getWhitePlayerId());
            pgn.setWhiteTurn();
        }
        else {
            throw new NoSuchElementException("Player with '%s' does not exist in the game".formatted(playerId));
        }

        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Resign from a game
     * @param gameId The ID of the game to resign from
     * @param playerId The ID of the player who is resigning
     * @throws NoSuchElementException if the game does not exist or if the player does not exist in the game
     * @throws IllegalArgumentException if game ID or player ID format is invalid UUID
     * @return The updated game
     */
    public Game resignGame(String gameId, String playerId) {
        var gameUUID = UUID.fromString(gameId);
        var playerUUID = UUID.fromString(playerId);

        var game = gameRepository.findById(gameUUID).orElseThrow();
        var pgn = Pgn.fromString(game.getPgn());

        if (game.getWhitePlayerId().equals(playerUUID)) { // White player resigned
            game.setWinnerPlayerId(game.getBlackPlayerId()); // Black player wins
            pgn.setBlackWinResult();
        }
        else if (game.getBlackPlayerId().equals(playerUUID)) { // Black player resigned
            game.setWinnerPlayerId(game.getWhitePlayerId()); // White player wins
            pgn.setWhiteWinResult();
        }
        else {
            throw new NoSuchElementException("Player with '%s' does not exist in the game".formatted(playerId));
        }

        game.setStatus(GameStatus.RESIGNED);
        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Draw a game
     * @param gameId The ID of the game to draw
     * @throws NoSuchElementException if the game does not exist
     * @throws IllegalArgumentException if game ID format is invalid UUID
     * @return The updated game
     */
    public Game drawGame(String gameId) {
        var gameUUID = UUID.fromString(gameId);

        var game = gameRepository.findById(gameUUID).orElseThrow();
        var pgn = Pgn.fromString(game.getPgn());
        pgn.setDrawResult();

        game.setStatus(GameStatus.DRAW);
        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Abort a game
     * @param gameId The ID of the game to abort
     * @throws NoSuchElementException if the game does not exist
     * @throws IllegalArgumentException if game ID format is invalid UUID
     * @return The updated game
     */
    public Game abortGame(String gameId) {
        var gameUUID = UUID.fromString(gameId);

        var game = gameRepository.findById(gameUUID).orElseThrow();
        game.setStatus(GameStatus.ABORTED);
        return gameRepository.save(game);
    }
}
