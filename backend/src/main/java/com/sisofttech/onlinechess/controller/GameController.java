package com.sisofttech.onlinechess.controller;

import com.sisofttech.onlinechess.dto.CreateGameDto;
import com.sisofttech.onlinechess.dto.GameDto;
import com.sisofttech.onlinechess.mapper.GameMapper;
import com.sisofttech.onlinechess.service.GameService;
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

    // Create a new game
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameDto createGameDto) {
        var whitePlayerId = UUID.fromString(createGameDto.getWhitePlayerId());
        var blackPlayerId = createGameDto.getBlackPlayerId() != null ? UUID.fromString(createGameDto.getBlackPlayerId()) : null;
        var game = gameService.createGame(whitePlayerId, blackPlayerId);
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }
}
