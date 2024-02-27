package com.sisofttech.onlinechess.repository;

import com.sisofttech.onlinechess.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
