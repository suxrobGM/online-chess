package com.silyosbekov.onlinechess.engine;

/**
 * A class representing a piece on the board.
 * A piece has a color and a type.
 */
public class Piece {
    private final char color;
    private char type;

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

    public void setType(char type) {
        this.type = type;
    }
}
