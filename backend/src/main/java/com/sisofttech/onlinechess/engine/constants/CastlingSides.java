package com.sisofttech.onlinechess.engine.constants;

import java.util.HashMap;

public final class CastlingSides {
    public static final int KINGSIDE = Bits.KSIDE_CASTLE;
    public static final int QUEENSIDE = Bits.QSIDE_CASTLE;

    public static final int K = KINGSIDE; // Alias
    public static final int Q = QUEENSIDE;

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
        put(PieceColors.WHITE, new RookSide[]{WHITE_ROOK_QUEENSIDE, WHITE_ROOK_KINGSIDE});
        put(PieceColors.BLACK, new RookSide[]{BLACK_ROOK_QUEENSIDE, BLACK_ROOK_KINGSIDE});
    }};

    public static final RookSide WHITE_ROOK_QUEENSIDE = new RookSide(Ox88.A1, QUEENSIDE);
    public static final RookSide WHITE_ROOK_KINGSIDE = new RookSide(Ox88.H1, KINGSIDE);
    public static final RookSide BLACK_ROOK_QUEENSIDE = new RookSide(Ox88.A8, QUEENSIDE);
    public static final RookSide BLACK_ROOK_KINGSIDE = new RookSide(Ox88.H8, KINGSIDE);

    public record RookSide(int square, int flag) {}
}
