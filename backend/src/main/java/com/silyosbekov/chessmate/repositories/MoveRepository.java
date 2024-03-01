package com.silyosbekov.chessmate.repositories;

import com.silyosbekov.chessmate.models.Game;
import com.silyosbekov.chessmate.models.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * MoveRepository interface.
 * Provides methods to interact with the moves table in the database.
 */
public interface MoveRepository extends JpaRepository<Move, UUID> {
    /**
     * Find the last move of a game
     * @param game the game
     * @return the last move
     */
    Optional<Move> findTopByGameOrderByMoveNumberDesc(Game game);
}