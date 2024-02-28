package com.sisofttech.onlinechess.repositories;

import com.sisofttech.onlinechess.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
}
