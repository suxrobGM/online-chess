package com.silyosbekov.chessmate.service;

import com.silyosbekov.chessmate.engine.Pgn;
import com.silyosbekov.chessmate.model.*;
import com.silyosbekov.chessmate.repository.GameRepository;
import com.silyosbekov.chessmate.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
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
    public Game createNewAnonymousGame(UUID hostPlayerId, PlayerColor hostPlayerColor) {
        return createGameWithHostPlayer(null, hostPlayerColor, hostPlayerId);
    }

    /**
     * Create a new game with a host player
     * @param hostPlayer The player who will host the game
     * @param hostPlayerColor The color of the host player, or null to randomly assign color to the host player
     * @param anonymousHostPlayerId The ID of the anonymous player
     * @return The newly created game
     */
    private Game createGameWithHostPlayer(Player hostPlayer, PlayerColor hostPlayerColor, UUID anonymousHostPlayerId) {
        var game = new Game();
        game.setHostPlayerColor(hostPlayerColor);
        game.setStatus(GameStatus.OPEN);

        if (hostPlayer != null && hostPlayerColor == PlayerColor.WHITE) {
            game.setHostPlayer(hostPlayer);
        }
        else {
            game.setAnonymousHostPlayerId(anonymousHostPlayerId);
        }

        var pgn = new Pgn();
        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Set the current turn player
     * @param gameId The ID of the game
     * @param playerId The ID of the player whose turn it is
     * @throws NoSuchElementException if the player does not exist in the game
     * @return The updated game
     */
    public Game setCurrentTurnPlayer(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        var pgn = Pgn.fromString(game.getPgn());

        if (game.getWhitePlayerId().equals(playerId)) {
            game.setCurrentTurn(PlayerColor.WHITE);
            pgn.setWhiteTurn();
        }
        else if (game.getBlackPlayerId().equals(playerId)) {
            game.setCurrentTurn(PlayerColor.BLACK);
            pgn.setBlackTurn();
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
     * @return The updated game
     */
    public Game resignGame(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        var pgn = Pgn.fromString(game.getPgn());

        if (game.getWhitePlayerId().equals(playerId)) { // White player resigned
            game.setWinnerPlayer(PlayerColor.WHITE); // Black player wins
            pgn.setBlackWinResult();
        }
        else if (game.getBlackPlayerId().equals(playerId)) { // Black player resigned
            game.setWinnerPlayer(PlayerColor.BLACK); // White player wins
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
     * @return The updated game
     */
    public Game drawGame(UUID gameId) {
        var game = gameRepository.findById(gameId).orElseThrow();
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
     * @return The updated game
     */
    public Game abortGame(UUID gameId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        game.setStatus(GameStatus.ABORTED);
        return gameRepository.save(game);
    }

    public Game cancelGame(UUID gameId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        game.setStatus(GameStatus.CANCELLED);
        return gameRepository.save(game);
    }
}
