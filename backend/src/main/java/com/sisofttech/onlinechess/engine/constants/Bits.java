package com.sisofttech.onlinechess.engine.constants;

public final class Bits {
    public static final int NORMAL = 1;
    public static final int CAPTURE = 2;
    public static final int BIG_PAWN = 4;
    public static final int EP_CAPTURE = 8;
    public static final int PROMOTION = 16;
    public static final int KSIDE_CASTLE = 32;
    public static final int QSIDE_CASTLE = 64;

    public static char getBitSymbol(int bit) {
        return switch (bit) {
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

}
