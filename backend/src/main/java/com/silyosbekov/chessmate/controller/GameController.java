package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.CreateGameCommand;
import com.silyosbekov.chessmate.dto.GameDto;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Create a new chess game
     * @param command - the game to create, with the white player's ID and optionally the black player's ID
     * @return The newly created game as a DTO
     */
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameCommand command) {
        var game = gameService.createNewGame(command.whitePlayerId(), command.blackPlayerId());
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }
}
