package com.silyosbekov.chessmate.engine.constant;

import java.util.HashMap;

/**
 * Bitmask constants.
 * These constants are used to represent different types of moves.
 * Each constant is a power of 2.
 */
public final class Bits {
    public static final int NORMAL = 1;
    public static final int CAPTURE = 2;
    public static final int BIG_PAWN = 4;
    public static final int EP_CAPTURE = 8;
    public static final int PROMOTION = 16;
    public static final int KSIDE_CASTLE = 32;
    public static final int QSIDE_CASTLE = 64;

    /**
     * Rooks that are involved in castling.
     * <p>
     *     The key is the color of the rooks.
     *     The value is a list of rooks that are involved in castling.
     *     The first element is the queenside rook and the second element is the kingside rook.
     *     The first element is the rook on the A file and the second element is the rook on the H file.
     * </p>
     */
    public static final HashMap<Character, RookSide[]> ROOKS = new HashMap<>() {{
        put(PieceColors.WHITE, new RookSide[]{WHITE_ROOK_QSIDE_CASTLE, WHITE_ROOK_KSIDE_CASTLE});
        put(PieceColors.BLACK, new RookSide[]{BLACK_ROOK_QSIDE_CASTLE, BLACK_ROOK_KSIDE_CASTLE});
    }};

    public static final RookSide WHITE_ROOK_QSIDE_CASTLE = new RookSide(Ox88.A1, QSIDE_CASTLE);
    public static final RookSide WHITE_ROOK_KSIDE_CASTLE = new RookSide(Ox88.H1, KSIDE_CASTLE);
    public static final RookSide BLACK_ROOK_QSIDE_CASTLE = new RookSide(Ox88.A8, QSIDE_CASTLE);
    public static final RookSide BLACK_ROOK_KSIDE_CASTLE = new RookSide(Ox88.H8, KSIDE_CASTLE);

    /**
     * Returns the castling side of the given piece type.
     * @param side The castling side, either 'k' or 'q'
     * @return A bitmask value of the castling side
     */
    public static int getCastlingSideBit(char side) {
        return side == PieceTypes.KING ? KSIDE_CASTLE : QSIDE_CASTLE;
    }

    /**
     * Returns the bitmask flag value of the given bit.
     * @param bitmask The bitmask value
     * @return The bitmask flag value
     */
    public static char getBitFlag(int bitmask) {
        return switch (bitmask) {
            case NORMAL -> 'n';
            case CAPTURE -> 'c';
            case BIG_PAWN -> 'b';
            case EP_CAPTURE -> 'e';
            case PROMOTION -> 'p';
            case KSIDE_CASTLE -> 'k';
            case QSIDE_CASTLE -> 'q';
            default -> throw new IllegalArgumentException("Invalid bit");
        };
    }

    /**
     * Returns an array of bit values.
     */
    public static int[] getBits() {
        return new int[] {
            NORMAL,
            CAPTURE,
            BIG_PAWN,
            EP_CAPTURE,
            PROMOTION,
            KSIDE_CASTLE,
            QSIDE_CASTLE
        };
    }

    /**
     * A record representing a rook side for castling.
     */
    public record RookSide(int square, int flag) {}
}
