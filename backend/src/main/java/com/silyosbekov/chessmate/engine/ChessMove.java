package com.silyosbekov.chessmate.engine;

public class ChessMove {
    private char color;
    private Object from; // Can be int (InternalMove) or String (Move)
    private Object to; // Can be int (InternalMove) or String (Move)
    private char piece;
    private Character captured; // Nullable
    private Character promotion; // Nullable
    private Object flags; // Can be int (InternalMove) or String (Move)
    private String san; // Standard Algebraic Notation
    private String lan; // Long Algebraic Notation
    private String before; // Board state before move
    private String after; // Board state after move


    public ChessMove(char color, int from, int to, char piece, int flags) {
        this(color, from, to, piece, null, null, flags);
    }

    public ChessMove(char color, int from, int to, char piece, Character captured, int flags) {
        this(color, from, to, piece, captured, null, flags);
    }

    public ChessMove(char color, int from, int to, char piece, Character captured, Character promotion, int flags) {
        this.color = color;
        this.from = from; // Autoboxing will handle primitive to Object conversion
        this.to = to;
        this.piece = piece;
        this.captured = captured;
        this.promotion = promotion;
        this.flags = flags;
        this.san = null;
        this.lan = null;
        this.before = null;
        this.after = null;
    }

    public ChessMove(char color, String from, String to, char piece, String flags, String san, String lan, String before, String after) {
        this.color = color;
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.flags = flags;
        this.san = san;
        this.lan = lan;
        this.before = before;
        this.after = after;
        this.captured = null;
        this.promotion = null;
    }

    public void setFrom(Object from) {
        if (from instanceof Integer || from instanceof String) {
            this.from = from;
        }
        else {
            throw new IllegalArgumentException("From must be an integer or a string");
        }
    }

    public int getFromAsInt() {
        if (from instanceof Integer) {
            return (Integer) from;
        }
        else if (from instanceof String) {
            return -1;
        }
        else {
            return -1;
        }
    }

    public String getFromAsString() {
        if (from instanceof String) {
            return (String) from;
        }
        else if (from instanceof Integer) {
            return from.toString();
        }
        else {
            return null;
        }
    }


    public void setTo(Object to) {
        if (to instanceof Integer || to instanceof String) {
            this.to = to;
        }
        else {
            throw new IllegalArgumentException("From must be an integer or a string");
        }
    }

    public int getToAsInt() {
        if (to instanceof Integer) {
            return (Integer) to;
        }
        else if (to instanceof String) {
            return -1;
        }
        else {
            return -1;
        }
    }

    public String getToAsString() {
        if (to instanceof String) {
            return (String) to;
        }
        else if (to instanceof Integer) {
            return to.toString();
        }
        else {
            return null;
        }
    }


    public void setFlags(Object flags) {
        if (flags instanceof Integer || flags instanceof String) {
            this.flags = flags;
        }
        else {
            throw new IllegalArgumentException("Flags must be an integer or a string");
        }
    }

    public int getFlagsAsInt() {
        if (flags instanceof Integer) {
            return (Integer) flags;
        }
        else if (flags instanceof String) {
            // Conversion logic here, for simplicity, return a placeholder
            return -1; // Placeholder for conversion logic
        }
        else {
            return -1; // Invalid type or not set
        }
    }

    public String getFlagsAsString() {
        if (flags instanceof String) {
            return (String) flags;
        }
        else if (flags instanceof Integer) {
            return flags.toString();
        }
        else {
            return null;
        }
    }

    public char getColor() {
        return color;
    }

    public void setColor(final char color) {
        this.color = color;
    }

    public char getPiece() {
        return piece;
    }

    public void setPiece(final char piece) {
        this.piece = piece;
    }

    public Character getCaptured() {
        return captured;
    }

    public void setCaptured(final Character captured) {
        this.captured = captured;
    }

    public Character getPromotion() {
        return promotion;
    }

    public void setPromotion(final Character promotion) {
        this.promotion = promotion;
    }

    public String getSan() {
        return san;
    }

    public void setSan(final String san) {
        this.san = san;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(final String lan) {
        this.lan = lan;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(final String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(final String after) {
        this.after = after;
    }
}
