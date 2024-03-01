package com.silyosbekov.chessmate.engine;

public class Move {
    private char color;
    private String from;
    private String to;
    private char piece;
    private Character captured; // nullable
    private Character promotion; // nullable
    private String flags;
    private String san;
    private String lan;
    private String before;
    private String after;

    public Move(char color, String from, String to, char piece, String flags) {
        this.color = color;
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.flags = flags;
        captured = null;
        promotion = null;
        san = null;
        lan = null;
        before = null;
        after = null;
    }

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
