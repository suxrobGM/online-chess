package com.sisofttech.onlinechess.engine.constants;

public final class PieceOffsets {
    public static final int[] KNIGHT = {-18, -33, -31, -14, 18, 33, 31, 14};
    public static final int[] BISHOP = {-17, -15, 17, 15};
    public static final int[] ROOK = {-16, 1, 16, -1};
    public static final int[] QUEEN = {-17, -16, -15, 1, 17, 16, 15, -1};
    public static final int[] KING = {-17, -16, -15, 1, 17, 16, 15, -1};

    public static final int[] N = KNIGHT; // Alias
    public static final int[] B = BISHOP;
    public static final int[] R = ROOK;
    public static final int[] Q = QUEEN;
    public static final int[] K = KING;
}
