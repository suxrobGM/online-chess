package com.silyosbekov.chessmate.engine.constants;

/**
 * Piece offsets constants.
 */
public final class PieceOffsets {
    public static final int[] KNIGHT = {-18, -33, -31, -14, 18, 33, 31, 14};
    public static final int[] BISHOP = {-17, -15, 17, 15};
    public static final int[] ROOK = {-16, 1, 16, -1};
    public static final int[] QUEEN = {-17, -16, -15, 1, 17, 16, 15, -1};
    public static final int[] KING = {-17, -16, -15, 1, 17, 16, 15, -1};

    /**
     * Get the piece offsets for the given piece type.
     * @param pieceType The piece type (symbol).
     * @return The piece offsets, or null if the piece type is invalid.
     */
    public static int[] get(char pieceType) {
        return switch (pieceType) {
            case 'n' -> KNIGHT;
            case 'b' -> BISHOP;
            case 'r' -> ROOK;
            case 'q' -> QUEEN;
            case 'k' -> KING;
            default -> null;
        };
    }
}
