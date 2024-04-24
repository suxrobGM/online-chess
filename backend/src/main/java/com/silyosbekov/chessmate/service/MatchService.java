package com.silyosbekov.chessmate.service;

import com.silyosbekov.chessmate.core.Pair;
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
            }
            else {
                game.setWhitePlayer(player);
            }
        }
        else {
            if (secondPlayerName.equals("Anonymous")) {
                game.setBlackAnonymousPlayerId(playerId);
            }
            else {
                game.setBlackPlayer(player);
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
        game.setPgn(pgn.toString());
        // activeGames.put(game.getId(), Pair.of(game, new Chess()));
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
        game.setStatus(GameStatus.ABANDONED);

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

    public MoveDto makeMove(UUID gameId, String from, String to)  {
        var activeGame = activeGames.get(gameId);

        if (activeGame == null) {
            throw new NoSuchElementException("Game with ID '%s' does not exist".formatted(gameId));
        }

        var move = activeGame.item2().move(new MoveOptions(from, to, null, null, true));

        if (move == null) {
            throw new IllegalArgumentException("Invalid move");
        }

        var whitePlayerId = activeGame.item1().getWhitePlayerId();
        var blackPlayerId = activeGame.item1().getBlackPlayerId();

        // var moveSan = from + to;
        // var pgn = Pgn.fromString(activeGame.item1().getPgn());
        // return new MoveDto(gameId, from, to, moveSan, activeGame.item2().pgn());
        return new MoveDto(gameId, whitePlayerId, blackPlayerId, from, to, move.getSan(), activeGame.item1().getPgn());
    }
}
