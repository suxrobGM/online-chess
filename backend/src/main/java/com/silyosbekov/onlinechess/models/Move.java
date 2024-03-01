package com.silyosbekov.onlinechess.models;

import jakarta.persistence.*;

/**
 * Move entity. Represents a move in a game.
 */
@Entity
@Table(name = "moves")
public class Move extends AuditableEntity {

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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean isCheckmate) {
        this.isCheckmate = isCheckmate;
    }
}
