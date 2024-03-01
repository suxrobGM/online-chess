package com.silyosbekov.onlinechess.repositories;

import com.silyosbekov.onlinechess.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * PlayerRepository interface.
 * Provides methods to interact with the players table in the database.
 */
public interface PlayerRepository extends JpaRepository<Player, UUID> {
}
