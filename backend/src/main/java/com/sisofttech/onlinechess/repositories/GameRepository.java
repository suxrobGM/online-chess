package com.sisofttech.onlinechess.repositories;

import com.sisofttech.onlinechess.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
