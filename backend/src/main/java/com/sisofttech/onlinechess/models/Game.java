package com.sisofttech.onlinechess.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

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


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Move> moves = new ArrayList<>();

    public UUID getId() {
        return id;
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public enum GameStatus {
        OPEN,
        ONGOING,
        DRAW,
        RESIGNED,
        COMPLETED
    }
}
