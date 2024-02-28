package com.sisofttech.onlinechess.engine.constants;

public final class PawnOffsets {
    public static final int[] WHITE = {16, 32, 17, 15};
    public static final int[] BLACK = {-16, -32, -17, -15};

    public static final int[] W = WHITE; // Alias
    public static final int[] B = BLACK;

    public static int[] get(char color) {
        return color == PieceColors.WHITE ? WHITE : BLACK;
    }
}
