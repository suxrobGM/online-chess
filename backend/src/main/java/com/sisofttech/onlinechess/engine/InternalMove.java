package com.sisofttech.onlinechess.engine;

import com.sisofttech.onlinechess.engine.constants.PieceColors;

public class InternalMove {
    private char color;
    private int from;
    private int to;
    private char piece;
    private char captured;
    private char promotion;
    private int flags;

    public InternalMove(char color, int from, int to, char piece, char captured, char promotion, int flags) {
        this.color = color;
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.captured = captured;
        this.promotion = promotion;
        this.flags = flags;
    }


    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public char getPiece() {
        return piece;
    }

    public void setPiece(char piece) {
        this.piece = piece;
    }

    public char getCaptured() {
        return captured;
    }

    public void setCaptured(char captured) {
        this.captured = captured;
    }

    public char getPromotion() {
        return promotion;
    }

    public void setPromotion(char promotion) {
        this.promotion = promotion;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
