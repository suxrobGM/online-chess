package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.*;
import com.silyosbekov.chessmate.mapper.GameMapper;
import com.silyosbekov.chessmate.service.MatchService;
import com.silyosbekov.chessmate.service.OnlinePlayersService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    @SendTo("/topic/match.moveReceived")
    public MoveDto makeMove(MakeMoveCommand command) {
        var moveDto = matchService.makeMove(command);
        //sendToPlayer(moveDto.whitePlayerId(), "/topic/match/move.received", moveDto);
        //sendToPlayer(moveDto.blackPlayerId(), "/topic/match/move.received", moveDto);
        return moveDto;
    }

    /**
     * Starts a new game match.
     * @param command contains information necessary to start a new game, such as player IDs.
     */
    @MessageMapping("/match/join")
    @SendTo("/topic/match.join")
    public GameDto joinGame(JoinGameCommand command) {
        var game = matchService.joinGame(command.gameId(), command.playerId());
        var gameDto = GameMapper.toDto(game);
        // sendToPlayer(command.playerId(), "/topic/match/join", gameDto);
        // sendToPlayer(game.getHostPlayerId(), "/topic/match/join", gameDto);
        return gameDto;
    }

    /**
     * Starts a new anonymous game match.
     * @param command contains information necessary to start a new game, such as player IDs.
     */
    @MessageMapping("/match/joinAnonymous")
    //@SendToUser("/topic/match.join")
    @SendTo("/topic/match.join")
    public GameDto joinAnonymousGame(JoinGameCommand command) {
        var game = matchService.joinAnonymousGame(command.gameId(), command.playerId());
        var gameDto = GameMapper.toDto(game);
        // sendToPlayer(command.playerId(), "/topic/match.join", gameDto);
        // sendToPlayer(game.getHostPlayerId(), "/topic/match.join", gameDto);
        return gameDto;
    }

    @MessageMapping("/match/offerDraw")
    @SendTo("/topic/match.receivedDrawOffer")
    public GameDto offerDraw(OfferDrawCommand command) {
        var game = matchService.getActiveGame(command.gameId());
        return GameMapper.toDto(game);
    }

    @MessageMapping("/match/acceptDraw")
    @SendTo("/topic/match.acceptedDraw")
    public GameDto acceptDraw(AcceptDrawCommand command) {
        var game = matchService.drawGame(command.gameId());
        return GameMapper.toDto(game);
    }

    @MessageMapping("/match/declineDraw")
    @SendTo("/topic/match.declinedDraw")
    public GameDto declineDraw(DeclineDrawCommand command) {
        var game = matchService.getActiveGame(command.gameId());
        return GameMapper.toDto(game);
    }

    @MessageMapping("/match/resign")
    @SendTo("/topic/match.resigned")
    public GameDto resignGame(ResignGameCommand command) {
        var game = matchService.resignGame(command.gameId(), command.playerId());
        return GameMapper.toDto(game);
    }

    /**
     * Ends the current game match.
     * @param command contains information necessary to end the game, such as player IDs.
     */
    @MessageMapping("/match/leave")
    @SendTo("/topic/match.playerLeft")
    public GameDto leaveGame(LeaveGameCommand command) {
        var game = matchService.leaveGame(command.gameId(), command.playerId());
        var gameDto = GameMapper.toDto(game);
        // sendToPlayer(command.playerId(), "/topic/match.playerLeft", gameDto);
        // sendToPlayer(game.getHostPlayerId(), "/topic/match.playerLeft", gameDto);
        return gameDto;
    }

    private void sendToPlayer(UUID playerId, String destination, Object payload) {
        var sessionId = onlinePlayersService.getSessionId(playerId);

        if (sessionId != null) {
            simpMessagingTemplate.convertAndSendToUser(sessionId, destination, payload);
        }
    }
}
