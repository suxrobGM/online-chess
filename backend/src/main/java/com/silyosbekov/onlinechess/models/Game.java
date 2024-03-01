package com.silyosbekov.onlinechess.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Game entity. Represents a game between two players.
 * It contains the game's metadata and moves.
 */
@Entity
@Table(name = "games")
public class Game extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "white_player_id", nullable = false)
    private Player whitePlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "black_player_id", nullable = false)
    private Player blackPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_player_id")
    private Player winnerPlayer;

    @Column(name = "current_turn_player_id", nullable = false)
    private UUID currentTurnPlayerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Move> moves = new ArrayList<>();

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player playerOneId) {
        this.whitePlayer = playerOneId;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player playerTwoId) {
        this.blackPlayer = playerTwoId;
    }

    public UUID getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public void setCurrentTurnPlayerId(UUID currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Player getWinnerPlayer() {
        return winnerPlayer;
    }

    public void setWinnerPlayer(Player winnerPlayer) {
        this.winnerPlayer = winnerPlayer;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public enum GameStatus {
        OPEN,
        ONGOING,
        DRAW,
        RESIGNED,
        COMPLETED
    }
}
