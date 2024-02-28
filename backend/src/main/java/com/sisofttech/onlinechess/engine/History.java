package com.sisofttech.onlinechess.engine;

import com.sisofttech.onlinechess.engine.constants.PieceColors;

import java.util.Map;

public class History {
    private InternalMove move;
    private Map<PieceColors, Integer> kings;
    private PieceColors turn;
    private Map<PieceColors, Integer> castling;
    private int epSquare;
    private int halfMoves;
    private int moveNumber;


    public InternalMove getMove() {
        return move;
    }

    public void setMove(InternalMove move) {
        this.move = move;
    }

    public Map<PieceColors, Integer> getKings() {
        return kings;
    }

    public void setKings(Map<PieceColors, Integer> kings) {
        this.kings = kings;
    }

    public PieceColors getTurn() {
        return turn;
    }

    public void setTurn(PieceColors turn) {
        this.turn = turn;
    }

    public Map<PieceColors, Integer> getCastling() {
        return castling;
    }

    public void setCastling(Map<PieceColors, Integer> castling) {
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
