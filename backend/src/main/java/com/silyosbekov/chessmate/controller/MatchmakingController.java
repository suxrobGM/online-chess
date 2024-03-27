package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.CreateGameCommand;
import com.silyosbekov.chessmate.dto.NewGameDto;
import com.silyosbekov.chessmate.service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MatchmakingController {
    private final GameService gameService;

    public MatchmakingController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/matchmaking/createGame")
    @SendTo("/topic/matchmaking/newGame")
    public NewGameDto createGame(@Payload CreateGameCommand command) {
        var newGame = gameService.createNewGame(command.whitePlayerId(), command.blackPlayerId());

        return new NewGameDto(
            newGame.getId().toString(),
            newGame.getWhitePlayerId().toString(),
            newGame.getBlackPlayerId().toString()
        );
    }
}
