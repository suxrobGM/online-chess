package com.sisofttech.onlinechess.controller;

import com.sisofttech.onlinechess.model.Game;
import com.sisofttech.onlinechess.repository.GameRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameRepository gameRepository;

    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "Test successful!";
    }

    // List all games
    @GetMapping()
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Create a new game
    @PostMapping
    public Game createGame(@RequestBody Game game) {
        return gameRepository.save(game);
    }
}
