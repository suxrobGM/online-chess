package com.silyosbekov.onlinechess.repositories;

import com.silyosbekov.onlinechess.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * GameRepository interface.
 * Provides methods to interact with the games table in the database.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {
}
