package com.sisofttech.onlinechess.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "moves")
public class Move {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "move_number")
    private int moveNumber;

    @Column(name = "from_position", nullable = false)
    private String fromPosition;

    @Column(name = "to_position", nullable = false)
    private String toPosition;

    @Column(name = "piece", nullable = false)
    private String piece;

    @Column(name = "captured_piece")
    private String capturedPiece;

    @Column(name = "is_check")
    private boolean isCheck;

    @Column(name = "is_checkmate")
    private boolean isCheckmate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public UUID getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(String fromPosition) {
        this.fromPosition = fromPosition;
    }

    public String getToPosition() {
        return toPosition;
    }

    public void setToPosition(String toPosition) {
        this.toPosition = toPosition;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public String getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(String capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean getIsCheckmate() {
        return isCheckmate;
    }

    public void setIsCheckmate(boolean isCheckmate) {
        this.isCheckmate = isCheckmate;
    }
}
