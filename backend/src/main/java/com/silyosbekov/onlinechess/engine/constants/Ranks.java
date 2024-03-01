package com.silyosbekov.onlinechess.engine.constants;

/**
 * Rank constants.
 */
public final class Ranks {
    public static final int RANK_1 = 7;
    public static final int RANK_2 = 6;
    public static final int RANK_3 = 5;
    public static final int RANK_4 = 4;
    public static final int RANK_5 = 3;
    public static final int RANK_6 = 2;
    public static final int RANK_7 = 1;
    public static final int RANK_8 = 0;

    public static final int SECOND_RANK_WHITE = RANK_2;
    public static final int SECOND_RANK_BLACK = RANK_7;

    /**
     * Get the second rank for the given color.
     * @param color The color.
     * @return The second rank.
     */
    public static int getSecondRank(char color) {
        return color == PieceColors.WHITE ? SECOND_RANK_WHITE : SECOND_RANK_BLACK;
    }
}
