package com.silyosbekov.chessmate.service;

import com.silyosbekov.chessmate.engine.Pgn;
import com.silyosbekov.chessmate.model.Game;
import com.silyosbekov.chessmate.model.GameStatus;
import com.silyosbekov.chessmate.repository.GameRepository;
import com.silyosbekov.chessmate.model.Player;
import com.silyosbekov.chessmate.repository.PlayerRepository;
import org.springframework.stereotype.Service;
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
     * Create a new game
     * @param whitePlayerId The ID of the player who will play as white
     * @param blackPlayerId The ID of the player who will play as black, or null if the game is not full
     * @throws NoSuchElementException if the white player does not exist, or if the black player does not exist
     * @return The newly created game
     */
    public Game createNewGame(UUID whitePlayerId, UUID blackPlayerId) {
        var whitePlayer = playerRepository.findById(whitePlayerId).orElseThrow();
        Player blackPlayer = null;

        if (blackPlayerId != null) {
            blackPlayer = playerRepository.findById(blackPlayerId).orElseThrow();
        }

        var game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setCurrentTurnPlayerId(whitePlayer.getId());
        game.setStatus(GameStatus.OPEN);
        var pgn = new Pgn(whitePlayer.getUsername());

        if (blackPlayer != null) {
            pgn.setBlackPlayer(blackPlayer.getUsername());
        }

        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Create a new anonymous game
     * @param whiteAnonymousPlayerId The ID of the anonymous player who will play as white
     * @param blackAnonymousPlayerId The ID of the anonymous player who will play as black, or null if the game is not full
     * @return The newly created game
     */
    public Game createNewAnonymousGame(UUID whiteAnonymousPlayerId, UUID blackAnonymousPlayerId) {
        var game = new Game();
        game.setWhiteAnonymousPlayerId(whiteAnonymousPlayerId);
        game.setBlackAnonymousPlayerId(blackAnonymousPlayerId);
        game.setCurrentTurnPlayerId(whiteAnonymousPlayerId);
        game.setStatus(GameStatus.OPEN);
        var pgn = new Pgn("Anonymous");

        if (blackAnonymousPlayerId != null) {
            pgn.setBlackPlayer("Anonymous");
        }

        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Join a game as the black player
     * @param gameId The ID of the game to join
     * @param blackPlayerId The ID of the player who will play as black
     * @throws IllegalStateException if the game is already full
     * @throws NoSuchElementException if the game does not exist, or if the player does not exist
     * @return The updated game
     */
    public Game joinExistingGame(UUID gameId, UUID blackPlayerId) {
        var game = gameRepository.findById(gameId).orElseThrow();

        if (game.getBlackPlayer() != null) {
            throw new IllegalStateException("Game is already full");
        }

        var player = playerRepository.findById(blackPlayerId).orElseThrow();
        game.setBlackPlayer(player);
        game.setCurrentTurnPlayerId(player.getId());
        game.setStatus(GameStatus.ONGOING);

        var pgn = Pgn.fromString(game.getPgn());
        pgn.setBlackPlayer(player.getUsername());
        pgn.setBlackTurn();
        game.setPgn(pgn.toString());
        return gameRepository.save(game);
    }

    /**
     * Join a game as the black anonymous player
     * @param gameId The ID of the game to join
     * @param blackAnonymousPlayerId The ID of the anonymous player who will play as black
     * @throws IllegalStateException if the game is already full
     * @throws NoSuchElementException if the game does not exist
     * @return The updated game
     */
    public Game joinExistingGameAsAnonymous(UUID gameId, UUID blackAnonymousPlayerId) {
        var game = gameRepository.findById(gameId).orElseThrow();

        if (game.getBlackAnonymousPlayerId() != null) {
            throw new IllegalStateException("Game is already full");
        }

        game.setBlackAnonymousPlayerId(blackAnonymousPlayerId);
        game.setCurrentTurnPlayerId(blackAnonymousPlayerId);
        game.setStatus(GameStatus.ONGOING);

        var pgn = Pgn.fromString(game.getPgn());
        pgn.setBlackPlayer("Anonymous");
        pgn.setBlackTurn();
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

        if (game.getBlackPlayerId().equals(playerId)) {
            game.setCurrentTurnPlayerId(game.getBlackPlayerId());
            pgn.setBlackTurn();
        }
        else if (game.getWhitePlayerId().equals(playerId)) {
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
     * Complete a game
     * @param gameId The ID of the game to complete
     * @param winnerId The ID of the winning player
     * @throws NoSuchElementException if the winner does not exist or if the game does not exist
     * @return The updated game
     */
    public Game completeGame(UUID gameId, UUID winnerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        var pgn = Pgn.fromString(game.getPgn());

        if (winnerId != null && winnerId.equals(game.getWhitePlayerId())){
            game.setWinnerPlayerId(winnerId);
            pgn.setWhiteWinResult();
        }
        else if (winnerId != null && winnerId.equals(game.getBlackPlayerId())) {
            game.setWinnerPlayerId(winnerId);
            pgn.setBlackWinResult();
        }
        else {
            throw new NoSuchElementException("Winner with '%s' does not exist in the game".formatted(winnerId));
        }

        game.setStatus(GameStatus.COMPLETED);
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
            game.setWinnerPlayerId(game.getBlackPlayerId()); // Black player wins
            pgn.setBlackWinResult();
        }
        else if (game.getBlackPlayerId().equals(playerId)) { // Black player resigned
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
}

