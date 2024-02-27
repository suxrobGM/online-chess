package com.sisofttech.onlinechess.repository;

import com.sisofttech.onlinechess.model.Game;
import com.sisofttech.onlinechess.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MoveRepository extends JpaRepository<Move, UUID> {
    /**
     * Find the last move of a game
     * @param game the game
     * @return the last move
     */
    Optional<Move> findTopByGameOrderByMoveNumberDesc(Game game);
}
