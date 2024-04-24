package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.CreateAnonymousGameCommand;
import com.silyosbekov.chessmate.dto.CreateGameCommand;
import com.silyosbekov.chessmate.dto.GameDto;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
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
    @MessageMapping("/game/create")
    @SendTo("/topic/game.created")
    public GameDto createGame(@RequestBody CreateGameCommand command) {
        var game = gameService.createNewGame(command.hostPlayerId(), command.hostPlayerColor());
        return GameMapper.toDto(game);
    }

    /**
     * Create a new anonymous chess game
     * @return The newly created game as a DTO
     */
    @MessageMapping("/game/createAnonymous")
    @SendTo("/topic/game.created")
    public GameDto createAnonymousGame(@RequestBody CreateAnonymousGameCommand command) {
        var game = gameService.createNewAnonymousGame(command.hostPlayerId(), command.hostPlayerColor());
        return GameMapper.toDto(game);
    }
}
