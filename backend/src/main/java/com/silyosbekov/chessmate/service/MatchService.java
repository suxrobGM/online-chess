package com.silyosbekov.chessmate.service;

import com.silyosbekov.chessmate.core.Pair;
import com.silyosbekov.chessmate.dto.MakeMoveCommand;
import com.silyosbekov.chessmate.dto.MoveDto;
import com.silyosbekov.chessmate.engine.Chess;
import com.silyosbekov.chessmate.engine.Pgn;
import com.silyosbekov.chessmate.engine.option.MoveOptions;
import com.silyosbekov.chessmate.model.Game;
import com.silyosbekov.chessmate.model.GameStatus;
import com.silyosbekov.chessmate.model.Player;
import com.silyosbekov.chessmate.model.PlayerColor;
import com.silyosbekov.chessmate.repository.GameRepository;
import com.silyosbekov.chessmate.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MatchService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    /**
     * A map of active games with their UUIDs as keys and a pair of the game and its chess engine as values
     */
    private final Map<UUID, Pair<Game, Chess>> activeGames = new HashMap<>();

    public MatchService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Game getActiveGame(UUID gameId) {
        return activeGames.get(gameId).item1();
    }

    /**
     * Join a game with a player.
     * @param gameId The ID of the game to join
     * @param playerId The ID of the player joining the game
     * @return The updated game
     */
    public Game joinGame(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        var player = playerRepository.findById(playerId).orElseThrow();
        return joinGameCommon(game, playerId, player);
    }

    /**
     * Join an anonymous game with a player.
     * @param gameId The ID of the game to join
     * @param playerId The ID of the player joining the game
     * @return The updated game
     */
    public Game joinAnonymousGame(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        return joinGameCommon(game, playerId, null);
    }

    /**
     * Join a game with a player.
     * @param game The game to join
     * @param playerId The joining player's ID
     * @param player The joining player entity. Set to null if the player is anonymous
     * @return The updated game
     */
    private Game joinGameCommon(Game game, UUID playerId, Player player) {
        if (game.isFull()) {
            throw new IllegalStateException("Game is already full");
        }

        var secondPlayerColor = determineSecondPlayerColor(game);
        var secondPlayerName = player == null ? "Anonymous" : player.getUsername();

        if (secondPlayerColor == PlayerColor.WHITE) {
            if (secondPlayerName.equals("Anonymous")) {
                game.setWhiteAnonymousPlayerId(playerId);
                game.setBlackAnonymousPlayerId(game.getHostPlayerId());
            }
            else {
                game.setWhitePlayer(player);
                game.setBlackPlayer(game.getHostPlayer());
            }
        }
        else {
            if (secondPlayerName.equals("Anonymous")) {
                game.setBlackAnonymousPlayerId(playerId);
                game.setWhiteAnonymousPlayerId(game.getHostPlayerId());
            }
            else {
                game.setBlackPlayer(player);
                game.setWhitePlayer(game.getHostPlayer());
            }
        }

        game.setStatus(GameStatus.ONGOING);

        // Set the player's color in the PGN
        var pgn = Pgn.fromString(game.getPgn());

        if (secondPlayerColor == PlayerColor.WHITE) {
            pgn.setWhitePlayer(secondPlayerName);
        }
        else {
            pgn.setBlackPlayer(secondPlayerName);
        }

        pgn.setWhiteTurn();
        game.setCurrentTurn(PlayerColor.WHITE);
        game.setPgn(pgn.toString());
        activeGames.put(game.getId(), Pair.of(game, new Chess()));
        return gameRepository.save(game);
    }

    /**
     * Determine the color of the second player in the game.
     * If the host player color is not set, the color is randomly assigned.
     * @param game The game to determine the color for
     * @return The color of the second player
     */
    private PlayerColor determineSecondPlayerColor(Game game) {
        if (game.getHostPlayerColor() == null) {
            var isWhite = new Random().nextBoolean();
            game.setHostPlayerColor(isWhite ? PlayerColor.BLACK : PlayerColor.WHITE);
            return isWhite ? PlayerColor.WHITE : PlayerColor.BLACK;
        }
        else {
            return game.getHostPlayerColor() == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
        }
    }

    public Game leaveGame(UUID gameId, UUID playerId) {
        final Game game;
        var activeGame = activeGames.get(gameId).item1();

        if (activeGame == null) {
            activeGame = gameRepository.findById(gameId).orElseThrow();
        }

        game = activeGame;
        game.setStatus(GameStatus.CANCELLED);

        // Schedule a timer to complete the abandoned game after 1 minute
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                completeAbandonedGame(game, playerId);
            }
        }, 60000); // 60000 ms = 1 minute

        return gameRepository.save(game);
    }

    /**
     * Complete an abandoned game by declaring
     * the other player as the winner and updating the game status to completed.
     * @param game The game to complete
     * @param abandonedPlayerId The ID of the player who abandoned the game
     */
    private void completeAbandonedGame(Game game, UUID abandonedPlayerId) {
        var winnerPlayerId = game.getWhitePlayerId() == abandonedPlayerId ? game.getBlackPlayerId() : game.getWhitePlayerId();
        var pgn = Pgn.fromString(game.getPgn());

        if (winnerPlayerId.equals(game.getWhitePlayerId())){
            game.setWinnerPlayer(PlayerColor.WHITE);
            pgn.setWhiteWinResult();
        }
        else if (winnerPlayerId.equals(game.getBlackPlayerId())) {
            game.setWinnerPlayer(PlayerColor.BLACK);
            pgn.setBlackWinResult();
        }
        else {
            throw new NoSuchElementException("Winner with '%s' does not exist in the game".formatted(winnerPlayerId.toString()));
        }

        game.setStatus(GameStatus.COMPLETED);
        activeGames.remove(game.getId());
        gameRepository.save(game);
    }

    public MoveDto makeMove(MakeMoveCommand command)  {
        var activeGame = activeGames.get(command.gameId());

        if (activeGame == null) {
            throw new NoSuchElementException("Game with ID '%s' does not exist".formatted(command.gameId()));
        }

        // TODO: Fix engine move validation error
//        var move = activeGame.item2().move(new MoveOptions(command.from(), command.to(), null, null, true));
//
//        if (move == null) {
//            throw new IllegalArgumentException("Invalid move");
//        }

        var whitePlayerId = activeGame.item1().getWhitePlayerId();
        var blackPlayerId = activeGame.item1().getBlackPlayerId();

        return new MoveDto(
                command.gameId(),
                whitePlayerId,
                blackPlayerId,
                command.color(),
                command.from(),
                command.to(),
                command.isCheckmate(),
                command.isStalemate());
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
}
