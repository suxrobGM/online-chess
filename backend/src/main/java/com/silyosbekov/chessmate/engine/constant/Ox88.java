package com.silyosbekov.chessmate.engine.constant;

/*
 * NOTES ABOUT 0x88 MOVE GENERATION ALGORITHM
 * ----------------------------------------------------------------------------
 *
 * 1. 0x88 offers a very inexpensive "off the board" check. Bitwise AND (&) any
 *    square with 0x88, if the result is non-zero then the square is off the
 *    board. For example, assuming a knight square A8 (0 in 0x88 notation),
 *    there are 8 possible directions in which the knight can move. These
 *    directions are relative to the 8x16 board and are stored in the
 *    PIECE_OFFSETS map. One possible move is A8 - 18 (up one square, and two
 *    squares to the left - which is off the board). 0 - 18 = -18 & 0x88 = 0x88
 *    (because of two-complement representation of -18). The non-zero result
 *    means the square is off the board and the move are illegal. Take the
 *    opposite move (from A8 to C7), 0 + 18 = 18 & 0x88 = 0. A result of zero
 *    means the square is on the board.
 *
 * 2. The relative distance (or difference) between two squares on a 8x16 board
 *    is unique and can be used to inexpensively determine if a piece on a
 *    square can attack any other arbitrary square. For example, let's see if a
 *    pawn on E7 can attack E2. The difference between E7 (20) - E2 (100) is
 *    -80. We add 119 to make the ATTACKS array index non-negative (because the
 *    worst case difference is A8 - H1 = -119). The ATTACKS array contains a
 *    bitmask of pieces that can attack from that distance and direction.
 *    ATTACKS[-80 + 119=39] gives us 24 or 0b11000 in binary. Look at the
 *    PIECE_MASKS map to determine the mask for a given piece type. In our pawn
 *    example, we would check to see if 24 & 0x1 is non-zero, which it is
 *    not. So, naturally, a pawn on E7 can't attack a piece on E2. However, a
 *    rook can since 24 & 0x8 is non-zero. The only thing left to check is that
 *    there are no blocking pieces between E7 and E2. That's where the RAYS
 *    array comes in. It provides an offset (in this case 16) to add to E7 (20)
 *    to check for blocking pieces. E7 (20) + 16 = E6 (36) + 16 = E5 (52) etc.
 */


/**
 * Contains 0x88 representation of squares.
 */
public final class Ox88 {
    public static final int A8 = 0;
    public static final int A7 = 16;
    public static final int A6 = 32;
    public static final int A5 = 48;
    public static final int A4 = 64;
    public static final int A3 = 80;
    public static final int A2 = 96;
    public static final int A1 = 112;

    public static final int B8 = 1;
    public static final int B7 = 17;
    public static final int B6 = 33;
    public static final int B5 = 49;
    public static final int B4 = 65;
    public static final int B3 = 81;
    public static final int B2 = 97;
    public static final int B1 = 113;

    public static final int C8 = 2;
    public static final int C7 = 18;
    public static final int C6 = 34;
    public static final int C5 = 50;
    public static final int C4 = 66;
    public static final int C3 = 82;
    public static final int C2 = 98;
    public static final int C1 = 114;

    public static final int D8 = 3;
    public static final int D7 = 19;
    public static final int D6 = 35;
    public static final int D5 = 51;
    public static final int D4 = 67;
    public static final int D3 = 83;
    public static final int D2 = 99;
    public static final int D1 = 115;

    public static final int E8 = 4;
    public static final int E7 = 20;
    public static final int E6 = 36;
    public static final int E5 = 52;
    public static final int E4 = 68;
    public static final int E3 = 84;
    public static final int E2 = 100;
    public static final int E1 = 116;

    public static final int F8 = 5;
    public static final int F7 = 21;
    public static final int F6 = 37;
    public static final int F5 = 53;
    public static final int F4 = 69;
    public static final int F3 = 85;
    public static final int F2 = 101;
    public static final int F1 = 117;

    public static final int G8 = 6;
    public static final int G7 = 22;
    public static final int G6 = 38;
    public static final int G5 = 54;
    public static final int G4 = 70;
    public static final int G3 = 86;
    public static final int G2 = 102;
    public static final int G1 = 118;

    public static final int H8 = 7;
    public static final int H7 = 23;
    public static final int H6 = 39;
    public static final int H5 = 55;
    public static final int H4 = 71;
    public static final int H3 = 87;
    public static final int H2 = 103;
    public static final int H1 = 119;

    /**
     * Get the 0x88 representation of a square.
     * @param square algebraic notation of the square
     * @return 0x88 representation of the square, or -1 if the square is invalid
     */
    public static int get(String square) {
        return switch (square) {
            case "a8" -> A8;
            case "a7" -> A7;
            case "a6" -> A6;
            case "a5" -> A5;
            case "a4" -> A4;
            case "a3" -> A3;
            case "a2" -> A2;
            case "a1" -> A1;
            case "b8" -> B8;
            case "b7" -> B7;
            case "b6" -> B6;
            case "b5" -> B5;
            case "b4" -> B4;
            case "b3" -> B3;
            case "b2" -> B2;
            case "b1" -> B1;
            case "c8" -> C8;
            case "c7" -> C7;
            case "c6" -> C6;
            case "c5" -> C5;
            case "c4" -> C4;
            case "c3" -> C3;
            case "c2" -> C2;
            case "c1" -> C1;
            case "d8" -> D8;
            case "d7" -> D7;
            case "d6" -> D6;
            case "d5" -> D5;
            case "d4" -> D4;
            case "d3" -> D3;
            case "d2" -> D2;
            case "d1" -> D1;
            case "e8" -> E8;
            case "e7" -> E7;
            case "e6" -> E6;
            case "e5" -> E5;
            case "e4" -> E4;
            case "e3" -> E3;
            case "e2" -> E2;
            case "e1" -> E1;
            case "f8" -> F8;
            case "f7" -> F7;
            case "f6" -> F6;
            case "f5" -> F5;
            case "f4" -> F4;
            case "f3" -> F3;
            case "f2" -> F2;
            case "f1" -> F1;
            case "g8" -> G8;
            case "g7" -> G7;
            case "g6" -> G6;
            case "g5" -> G5;
            case "g4" -> G4;
            case "g3" -> G3;
            case "g2" -> G2;
            case "g1" -> G1;
            case "h8" -> H8;
            case "h7" -> H7;
            case "h6" -> H6;
            case "h5" -> H5;
            case "h4" -> H4;
            case "h3" -> H3;
            case "h2" -> H2;
            case "h1" -> H1;
            default -> -1;
        };
    }
}
