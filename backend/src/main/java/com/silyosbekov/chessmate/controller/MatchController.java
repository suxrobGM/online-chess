package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.*;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.service.MatchService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Handles a player's move in a game.
     * @param command represents the move made by a player, including game ID, player ID, and the move details.
     */
    @MessageMapping("/match/move")
    @SendTo("/topic/match.update")
    public MoveDto makeMove(MakeMoveCommand command) {
        return matchService.makeMove(command.gameId(), command.from(), command.to());
    }

    /**
     * Starts a new game match.
     * @param command contains information necessary to start a new game, such as player IDs.
     * @return The game DTO.
     */
    @MessageMapping("/match/join")
    @SendTo("/topic/match.join")
    public GameDto joinGame(JoinGameCommand command) {
        var game = matchService.joinGame(command.gameId(), command.playerId());
        return GameMapper.toDto(game);
    }

    /**
     * Ends the current game match.
     * @param command contains information necessary to end the game, such as player IDs.
     */
    @MessageMapping("/match/leave")
    @SendTo("/topic/match.leave")
    public GameDto leaveGame(LeaveGameCommand command) {
        var game = matchService.leaveGame(command.gameId(), command.playerId());
        return GameMapper.toDto(game);
    }
}
