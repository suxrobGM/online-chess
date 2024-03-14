package com.silyosbekov.chessmate.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Game entity. Represents a game between two players.
 * It contains the players' ids, the current turn player's id, the game's status, and the game's PGN.
 */
@Entity
@Table(name = "games")
public class Game extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "white_player_id")
    private Player whitePlayer;

    @Column(name = "white_anonymous_player_id")
    private UUID whiteAnonymousPlayerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "black_player_id")
    private Player blackPlayer;

    @Column(name = "black_anonymous_player_id")
    private UUID blackAnonymousPlayerId;

    @Column(name = "winner_player_id")
    private UUID winnerPlayerId;

    @Column(name = "current_turn_player_id", nullable = false)
    private UUID currentTurnPlayerId;

    @Column(name = "is_timer_enabled")
    private boolean isTimerEnabled = false;

    @Column(name = "is_ranked")
    private boolean isRanked = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.OPEN;

    @Column(nullable = false)
    private String pgn = "";

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

    public UUID getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public void setWinnerPlayerId(UUID winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public UUID getWhiteAnonymousPlayerId() {
        return whiteAnonymousPlayerId;
    }

    public void setWhiteAnonymousPlayerId(UUID whiteAnonymousPlayerId) {
        this.whiteAnonymousPlayerId = whiteAnonymousPlayerId;
    }

    public UUID getBlackAnonymousPlayerId() {
        return blackAnonymousPlayerId;
    }

    public void setBlackAnonymousPlayerId(UUID blackAnonymousPlayerId) {
        this.blackAnonymousPlayerId = blackAnonymousPlayerId;
    }

    public UUID getWhitePlayerId() {
        return whitePlayer != null ? whitePlayer.getId() : whiteAnonymousPlayerId;
    }

    public UUID getBlackPlayerId() {
        return blackPlayer != null ? blackPlayer.getId() : blackAnonymousPlayerId;
    }

    public String getWhitePlayerUsername() {
        return whitePlayer != null ? whitePlayer.getUsername() : "Anonymous";
    }

    public String getBlackPlayerUsername() {
        return blackPlayer != null ? blackPlayer.getUsername() : "Anonymous";
    }

    public boolean isTimerEnabled() {
        return isTimerEnabled;
    }

    public void setTimerEnabled(boolean isTimerEnabled) {
        this.isTimerEnabled = isTimerEnabled;
    }

    public boolean isRanked() {
        return isRanked;
    }

    public void setRanked(boolean isRanked) {
        this.isRanked = isRanked;
    }
}
