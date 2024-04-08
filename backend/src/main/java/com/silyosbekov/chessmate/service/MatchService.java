package com.silyosbekov.chessmate.service;

import com.silyosbekov.chessmate.core.Pair;
import com.silyosbekov.chessmate.dto.MoveDto;
import com.silyosbekov.chessmate.engine.Chess;
import com.silyosbekov.chessmate.engine.Pgn;
import com.silyosbekov.chessmate.engine.option.MoveOptions;
import com.silyosbekov.chessmate.model.Game;
import com.silyosbekov.chessmate.model.GameStatus;
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

    public MatchService(
            GameRepository gameRepository,
            PlayerRepository playerRepository
    )
    {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Game joinGame(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        var player = playerRepository.findById(playerId).orElseThrow();
        return joinGameCommon(game, gameId, player.getUsername(), playerId);
    }

    public Game joinAnonymousGame(UUID gameId, UUID playerId) {
        var game = gameRepository.findById(gameId).orElseThrow();
        return joinGameCommon(game, gameId, "Anonymous", playerId);
    }

    private Game joinGameCommon(Game game, UUID gameId, String playerName, UUID playerId) {
        if (game.isFull()) {
            throw new IllegalStateException("Game is already full");
        }

        PlayerColor playerColor;

        if (playerName.equals("Anonymous")) {
            playerColor = game.setAnonymousPlayer(playerId);
        }
        else {
            var player = playerRepository.findById(playerId).orElseThrow();
            playerColor = game.setPlayer(player);
        }

        game.setStatus(GameStatus.ONGOING);

        var pgn = Pgn.fromString(game.getPgn());

        if (playerColor == PlayerColor.WHITE) {
            pgn.setWhitePlayer(playerName);
            pgn.setWhiteTurn();
        }
        else {
            pgn.setBlackPlayer(playerName);
            pgn.setBlackTurn();
        }

        game.setPgn(pgn.toString());
        activeGames.put(gameId, Pair.of(game, new Chess()));
        return gameRepository.save(game);
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
            game.setWinnerPlayerId(winnerPlayerId);
            pgn.setWhiteWinResult();
        }
        else if (winnerPlayerId.equals(game.getBlackPlayerId())) {
            game.setWinnerPlayerId(winnerPlayerId);
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

        return new MoveDto(gameId, from, to, move.getSan(), activeGame.item2().pgn());
    }
}
