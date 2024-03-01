package com.silyosbekov.chessmate.engine;

import java.util.Map;

public class History {
    private InternalMove move;
    private Map<Character, Integer> kings;
    private char turn;
    private Map<Character, Integer> castling;
    private int epSquare;
    private int halfMoves;
    private int moveNumber;

    public History(
        InternalMove move,
        Map<Character, Integer> kings,
        char turn,
        Map<Character, Integer> castling,
        int epSquare,
        int halfMoves,
        int moveNumber
    )
    {
        this.move = move;
        this.kings = kings;
        this.turn = turn;
        this.castling = castling;
        this.epSquare = epSquare;
        this.halfMoves = halfMoves;
        this.moveNumber = moveNumber;
    }

    public InternalMove getMove() {
        return move;
    }

    public void setMove(InternalMove move) {
        this.move = move;
    }

    public Map<Character, Integer> getKings() {
        return kings;
    }

    public void setKings(Map<Character, Integer> kings) {
        this.kings = kings;
    }

    public char getTurn() {
        return turn;
    }

    public void setTurn(Character turn) {
        this.turn = turn;
    }

    public Map<Character, Integer> getCastling() {
        return castling;
    }

    public void setCastling(Map<Character, Integer> castling) {
        this.castling = castling;
    }

    public int getEpSquare() {
        return epSquare;
    }

    public void setEpSquare(int epSquare) {
        this.epSquare = epSquare;
    }

    public int getHalfMoves() {
        return halfMoves;
    }

    public void setHalfMoves(int halfMoves) {
        this.halfMoves = halfMoves;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }
}
