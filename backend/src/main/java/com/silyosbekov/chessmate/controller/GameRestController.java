package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.*;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.model.GameStatus;
import com.silyosbekov.chessmate.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class GameRestController {
    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/api/games")
    public ResponseEntity<List<GameDto>> getGame(@RequestParam(name = "gameStatus", required = false) GameStatus gameStatus) {
        var games = gameService.getGames(gameStatus);
        var gamesDto = new ArrayList<GameDto>();

        for (var game : games) {
            gamesDto.add(GameMapper.toDto(game));
        }

        return ResponseEntity.ok(gamesDto);
    }

    /**
     * Get a game by its ID
     * @param id - The game ID
     * @return The game as a DTO
     */
    @GetMapping("/api/games/{id}")
    public ResponseEntity<GameDto> getGameById(@PathVariable("id") UUID id) {
        var game = gameService.getGameById(id);
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }

    /**
     * Create a new chess game
     * @param command - the game to create, with the white player's ID and optionally the black player's ID
     * @return The newly created game as a DTO
     */
    @PostMapping("/api/games")
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameCommand command) {
        var game = gameService.createNewGame(command.hostPlayerId(), command.hostPlayerColor());
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }

    /**
     * Create a new anonymous chess game
     * @return The newly created game as a DTO
     */
    @PostMapping("/api/games/anonymous")
    public ResponseEntity<GameDto> createAnonymousGame(@RequestBody CreateAnonymousGameCommand command) {
        var game = gameService.createNewAnonymousGame(command.hostPlayerId(), command.hostPlayerColor());
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }
}
