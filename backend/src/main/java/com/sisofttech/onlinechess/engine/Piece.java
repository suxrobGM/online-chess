package com.sisofttech.onlinechess.engine;

public class Piece {
    private final char color;
    private final char type;

    public Piece(char color, char type) {
        this.color = color;
        this.type = type;
    }

    public char getColor() {
        return color;
    }

    public char getType() {
        return type;
    }
}
