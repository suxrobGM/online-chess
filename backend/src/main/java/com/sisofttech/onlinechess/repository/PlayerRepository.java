package com.sisofttech.onlinechess.repository;

import com.sisofttech.onlinechess.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
}
