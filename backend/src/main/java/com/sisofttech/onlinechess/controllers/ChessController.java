package com.sisofttech.onlinechess.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChessController {

    @GetMapping("/")
    public String index() {
        return "Hello, World!";
    }

    @PostMapping("/chess/move")
    public String move() {
        return "Move";
    }
}
