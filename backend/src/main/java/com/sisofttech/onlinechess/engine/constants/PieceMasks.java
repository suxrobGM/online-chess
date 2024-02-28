package com.sisofttech.onlinechess.engine.constants;

public final class PieceMasks {
    public static final int PAWN = 0x1;
    public static final int KNIGHT = 0x2;
    public static final int BISHOP = 0x4;
    public static final int ROOK = 0x8;
    public static final int QUEEN = 0x10;
    public static final int KING = 0x20;

    public static final int P = PAWN; // Alias
    public static final int N = KNIGHT;
    public static final int B = BISHOP;
    public static final int R = ROOK;
    public static final int Q = QUEEN;
    public static final int K = KING;

    /**
     * Get the piece mask for the given piece symbol.
     *
     * @param pieceSymbol The piece symbol.
     * @return The piece mask.
     */
    public static int get(char pieceSymbol) {
        return switch (pieceSymbol) {
            case 'p' -> PAWN;
            case 'n' -> KNIGHT;
            case 'b' -> BISHOP;
            case 'r' -> ROOK;
            case 'q' -> QUEEN;
            case 'k' -> KING;
            default -> -1;
        };
    }
}
