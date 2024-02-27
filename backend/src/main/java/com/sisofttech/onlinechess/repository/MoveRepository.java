package com.sisofttech.onlinechess.repository;

import com.sisofttech.onlinechess.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MoveRepository extends JpaRepository<Move, UUID> {
}
