package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.*;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.service.MatchService;
import com.silyosbekov.chessmate.service.OnlinePlayersService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
public class MatchController {
    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OnlinePlayersService onlinePlayersService;

    public MatchController(
            MatchService matchService,
            SimpMessagingTemplate simpMessagingTemplate,
            OnlinePlayersService onlinePlayersService
    )
    {
        this.matchService = matchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.onlinePlayersService = onlinePlayersService;
    }

    /**
     * Handles a player's move in a game.
     * @param command represents the move made by a player, including game ID, player ID, and the move details.
     */
    @MessageMapping("/match/move")
    //@SendToUser("/topic/match.update")
    public void makeMove(MakeMoveCommand command) {
        var moveDto = matchService.makeMove(command.gameId(), command.from(), command.to());
        sendToPlayer(moveDto.whitePlayerId(), "/topic/match.update", moveDto);
        sendToPlayer(moveDto.blackPlayerId(), "/topic/match.update", moveDto);
    }

    /**
     * Starts a new game match.
     * @param command contains information necessary to start a new game, such as player IDs.
     */
    @MessageMapping("/match/join")
    //@SendToUser("/topic/match.join")
    public void joinGame(JoinGameCommand command) {
        var game = matchService.joinGame(command.gameId(), command.playerId());
        sendToPlayer(game.getWhitePlayerId(), "/topic/match.join", game);
        sendToPlayer(game.getBlackPlayerId(), "/topic/match.join", game);
    }

    /**
     * Starts a new anonymous game match.
     * @param command contains information necessary to start a new game, such as player IDs.
     */
    @MessageMapping("/match/joinAnonymous")
    //@SendToUser("/topic/match.join")
    public void joinAnonymousGame(JoinGameCommand command) {
        var game = matchService.joinAnonymousGame(command.gameId(), command.playerId());
        sendToPlayer(game.getWhitePlayerId(), "/topic/match.join", game);
        sendToPlayer(game.getBlackPlayerId(), "/topic/match.join", game);
    }

    /**
     * Ends the current game match.
     * @param command contains information necessary to end the game, such as player IDs.
     */
    @MessageMapping("/match/leave")
    //@SendToUser("/topic/match.leave")
    public void leaveGame(LeaveGameCommand command) {
        var game = matchService.leaveGame(command.gameId(), command.playerId());
        sendToPlayer(game.getWhitePlayerId(), "/topic/match.leave", game);
        sendToPlayer(game.getBlackPlayerId(), "/topic/match.leave", game);
    }

    private void sendToPlayer(UUID playerId, String destination, Object payload) {
        var sessionId = onlinePlayersService.getSessionId(playerId);

        if (sessionId != null) {
            simpMessagingTemplate.convertAndSendToUser(sessionId, destination, payload);
        }
    }
}
