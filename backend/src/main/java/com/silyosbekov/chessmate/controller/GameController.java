package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.dto.CreateGameDto;
import com.silyosbekov.chessmate.dto.GameDto;
import com.silyosbekov.chessmate.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/test")
    public String test() {
        return "Test successful!";
    }

    /**
     * Create a new chess game
     * @param createGameDto - the game to create, with the white player's ID and optionally the black player's ID
     * @return The newly created game as a DTO
     */
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameDto createGameDto) {
        var whitePlayerId = UUID.fromString(createGameDto.getWhitePlayerId());
        var blackPlayerId = createGameDto.getBlackPlayerId() != null ? UUID.fromString(createGameDto.getBlackPlayerId()) : null;
        var game = gameService.createNewGame(whitePlayerId, blackPlayerId);
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }
}
