package com.sisofttech.onlinechess.engine.constants;

/*
 * NOTES ABOUT 0x88 MOVE GENERATION ALGORITHM
 * ----------------------------------------------------------------------------
 * From https://github.com/jhlywa/chess.js/issues/230
 *
 * A lot of people are confused when they first see the internal representation
 * of chess.js. It uses the 0x88 Move Generation Algorithm which internally
 * stores the board as a 8x16 array. This is purely for efficiency but has a
 * couple of interesting benefits:
 *
 * 1. 0x88 offers a very inexpensive "off the board" check. Bitwise AND (&) any
 *    square with 0x88, if the result is non-zero then the square is off the
 *    board. For example, assuming a knight square A8 (0 in 0x88 notation),
 *    there are 8 possible directions in which the knight can move. These
 *    directions are relative to the 8x16 board and are stored in the
 *    PIECE_OFFSETS map. One possible move is A8 - 18 (up one square, and two
 *    squares to the left - which is off the board). 0 - 18 = -18 & 0x88 = 0x88
 *    (because of two-complement representation of -18). The non-zero result
 *    means the square is off the board and the move is illegal. Take the
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
}
