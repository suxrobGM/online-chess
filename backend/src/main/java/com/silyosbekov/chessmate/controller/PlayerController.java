package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.dto.ConnectPlayerCommand;
import com.silyosbekov.chessmate.service.OnlinePlayersService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class PlayerController {
    private final OnlinePlayersService onlinePlayersService;

    public PlayerController(OnlinePlayersService onlinePlayersService) {
        this.onlinePlayersService = onlinePlayersService;
    }

    @MessageMapping("/player/connect")
    public void connectPlayer(ConnectPlayerCommand command, @Header("simpSessionId") String sessionId) {
        onlinePlayersService.addPlayer(command.playerId(), sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        var sessionId = (String)event.getMessage().getHeaders().get("simpSessionId");
        onlinePlayersService.removePlayerBySessionId(sessionId);
    }
}
