package com.silyosbekov.chessmate.engine;

public class InternalMove {
    private char color;
    private int from;
    private int to;
    private char piece;
    private Character captured; // Nullable
    private Character promotion; // Nullable
    private int flags;

    public InternalMove(char color, int from, int to, char piece, int flags) {
        this.color = color;
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.flags = flags;
        captured = null;
        promotion = null;
    }

    public InternalMove(char color, int from, int to, char piece, Character captured, int flags) {
        this.color = color;
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.captured = captured;
        this.flags = flags;
        promotion = null;
    }

    public InternalMove(char color, int from, int to, char piece, Character captured, char promotion, int flags) {
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

    public Character getCaptured() {
        return captured;
    }

    public void setCaptured(Character captured) {
        this.captured = captured;
    }

    public Character getPromotion() {
        return promotion;
    }

    public void setPromotion(Character promotion) {
        this.promotion = promotion;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
