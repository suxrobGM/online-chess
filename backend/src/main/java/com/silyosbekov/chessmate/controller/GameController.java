package com.silyosbekov.chessmate.controller;

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
     * @param id - The game ID
     * @return The newly created game as a DTO
     */
    @PostMapping("{id}")
    public ResponseEntity<GameDto> getGameById(@PathVariable("id") String id) {
        var game = gameService.getGameById(id);
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }
}
