package com.silyosbekov.chessmate.controller;

import com.silyosbekov.chessmate.service.GameService;
import org.springframework.stereotype.Controller;

@Controller
public class ChessMatchController {
    private final GameService gameService;

    public ChessMatchController(GameService gameService) {
        this.gameService = gameService;
    }


}
