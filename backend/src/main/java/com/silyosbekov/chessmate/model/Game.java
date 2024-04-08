package com.silyosbekov.chessmate.model;

import com.silyosbekov.chessmate.constant.GameConst;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Game entity. Represents a game between two players.
 * It contains the players' ids, the current turn player's id, the game's status, and the game's PGN.
 */
@Entity
@Table(name = "games")
public class Game extends AuditableEntity {
    /**
     * The player who hosts the game.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_player_id")
    private Player hostPlayer;

    /**
     * The anonymous player's id who hosts the game.
     */
    @Column(name = "anonymous_host_player_id")
    private UUID anonymousHostPlayerId;

    /**
     * The color of the host player in the game.
     * If the host player is not set, the color is null.
     * It means that color will be assigned randomly when second player joins the game.
     */
    @Column(name = "host_player_color")
    private PlayerColor hostPlayerColor;

    /**
     * The player who plays as white.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "white_player_id")
    private Player whitePlayer;

    /**
     * The anonymous player's id who plays as white.
     */
    @Column(name = "white_anonymous_player_id")
    private UUID whiteAnonymousPlayerId;

    /**
     * The player who plays as black.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "black_player_id")
    private Player blackPlayer;

    /**
     * The anonymous player's id who plays as black.
     */
    @Column(name = "black_anonymous_player_id")
    private UUID blackAnonymousPlayerId;

    /**
     * The color of the player who won the game.
     * If the game is not finished yet, the winner player is null.
     */
    @Column(name = "winner_player")
    private PlayerColor winnerPlayer;

    /**
     * The player color who has the current turn in the game.
     * If game is not started yet, the current turn is null.
     */
    @Column(name = "current_turn")
    private PlayerColor currentTurn;

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

    public void setWhitePlayer(Player whitePlayerId) {
        this.whitePlayer = whitePlayerId;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player setBlackPlayer) {
        this.blackPlayer = setBlackPlayer;
    }

    public PlayerColor getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlayerColor currentTurn) {
        this.currentTurn = currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public PlayerColor getWinnerPlayer() {
        return winnerPlayer;
    }

    public void setWinnerPlayer(PlayerColor winnerPlayerColor) {
        this.winnerPlayer = winnerPlayerColor;
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

    public boolean isFull() {
        return whitePlayer != null && blackPlayer != null;
    }

    public PlayerColor getHostPlayerColor() {
        return hostPlayerColor;
    }

    public void setHostPlayerColor(PlayerColor hostPlayerColor) {
        this.hostPlayerColor = hostPlayerColor;
    }

    public Player getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(Player hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    public UUID getAnonymousHostPlayerId() {
        return anonymousHostPlayerId;
    }

    public void setAnonymousHostPlayerId(UUID anonymousHostPlayerId) {
        this.anonymousHostPlayerId = anonymousHostPlayerId;
    }

    public UUID getHostPlayerId() {
        return hostPlayer != null ? hostPlayer.getId() : anonymousHostPlayerId;
    }

    public String getHostPlayerUsername() {
        return hostPlayer != null ? hostPlayer.getUsername() : "Anonymous";
    }

    public int getHostPlayerElo() {
        return hostPlayer != null ? hostPlayer.getElo() : GameConst.DEFAULT_ELO;
    }

    public int getWhitePlayerElo() {
        return whitePlayer != null ? whitePlayer.getElo() : GameConst.DEFAULT_ELO;
    }

    public int getBlackPlayerElo() {
        return blackPlayer != null ? blackPlayer.getElo() : GameConst.DEFAULT_ELO;
    }
}
