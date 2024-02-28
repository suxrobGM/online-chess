package com.sisofttech.onlinechess.repositories;

import com.sisofttech.onlinechess.models.Game;
import com.sisofttech.onlinechess.models.Move;
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
