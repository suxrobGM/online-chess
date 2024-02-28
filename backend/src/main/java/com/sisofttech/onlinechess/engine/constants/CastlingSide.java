package com.sisofttech.onlinechess.engine.constants;

public final class CastlingSide {
    public static final int KINGSIDE = Bits.KSIDE_CASTLE;
    public static final int QUEENSIDE = Bits.QSIDE_CASTLE;

    public static final int K = KINGSIDE; // Alias
    public static final int Q = QUEENSIDE;

    public static final RookSide WHITE_ROOK_QUEENSIDE = new RookSide(Ox88.A1, QUEENSIDE);
    public static final RookSide WHITE_ROOK_KINGSIDE = new RookSide(Ox88.H1, KINGSIDE);
    public static final RookSide BLACK_ROOK_QUEENSIDE = new RookSide(Ox88.A8, QUEENSIDE);
    public static final RookSide BLACK_ROOK_KINGSIDE = new RookSide(Ox88.H8, KINGSIDE);

    public record RookSide(int square, int flag) {}
}
