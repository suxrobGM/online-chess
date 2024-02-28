package com.sisofttech.onlinechess.engine;

import com.sisofttech.onlinechess.engine.constants.PieceColors;
import com.sisofttech.onlinechess.engine.constants.PieceSymbols;

public class Move {
    private char color;
    private String from;
    private String to;
    private char piece;
    private char captured; // nullable
    private char promotion; // nullable
    private String flags;
    private String san;
    private String lan;
    private String before;
    private String after;

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
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

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
