package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.CreateAnonymousGameCommand;
import com.silyosbekov.chessmate.dto.CreateGameCommand;
import com.silyosbekov.chessmate.dto.GameDto;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/matchmaking")
public class MatchmakingController {
    private final GameService gameService;

    public MatchmakingController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Create a new chess game
     * @param command - the game to create, with the white player's ID and optionally the black player's ID
     * @return The newly created game as a DTO
     */
    @PostMapping("/game")
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameCommand command) {
        var game = gameService.createNewGame(command.hostPlayerId(), command.hostPlayerColor());
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }

    /**
     * Create a new anonymous chess game
     * @return The newly created game as a DTO
     */
    @PostMapping("/anonymousGame")
    public ResponseEntity<GameDto> createAnonymousGame(@RequestBody CreateAnonymousGameCommand command) {
        var game = gameService.createNewAnonymousGame(command.hostPlayerColor());
        var gameDto = GameMapper.toDto(game);
        return ResponseEntity.ok(gameDto);
    }
}
