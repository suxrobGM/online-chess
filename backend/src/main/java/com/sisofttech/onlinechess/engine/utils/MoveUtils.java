package com.sisofttech.onlinechess.engine.utils;

import com.sisofttech.onlinechess.engine.InternalMove;
import com.sisofttech.onlinechess.engine.constants.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MoveUtils {
    /**
     * Extracts the zero-based rank of a 0x88 square.
     * @param square 0x88 square
     * @return rank
     */
    public static int rank(int square) {
        return square >> 4;
    }

    /**
     * Extracts the zero-based file of a 0x88 square.
     * @param square 0x88 square
     * @return file
     */
    public static int file(int square) {
        return square & 0xf;
    }

    /**
     * Converts a 0x88 square to algebraic notation.
     * @param square 0x88 square
     * @return algebraic notation
     */
    public static String algebraic(int square) {
        var file = file(square);
        var rank = rank(square);
        return "abcdefgh".substring(file, file + 1) + "87654321".substring(rank, rank + 1);
    }

    /**
     * Swaps the color of a piece.
     * @param color piece color
     * @return swapped color
     */
    public static char swapColor(char color) {
        return color == PieceColors.WHITE ? PieceColors.BLACK : PieceColors.WHITE;
    }

    /**
     * This function is used to uniquely identify ambiguous moves.
     * @param move move
     * @param moves list of moves
     * @return ambiguous move
     */
    public static String getDisambiguator(InternalMove move, List<InternalMove> moves) {
        var from = move.getFrom();
        var to = move.getTo();
        var piece = move.getPiece();

        var ambiguities = 0;
        var sameRank = 0;
        var sameFile = 0;

        for (var m : moves) {
            var ambigFrom = m.getFrom();
            var ambigTo = m.getTo();
            var ambigPiece = m.getPiece();

            /*
             * if a move of the same piece type ends on the same to square, we'll need
             * to add a disambiguator to the algebraic notation
             */
            if (piece == ambigPiece && from != ambigFrom && to == ambigTo) {
                ambiguities++;

                if (rank(from) == rank(ambigFrom)) {
                    sameRank++;
                }

                if (file(from) == file(ambigFrom)) {
                    sameFile++;
                }
            }
        }

        if (ambiguities > 0) {
            if (sameRank > 0 && sameFile > 0) {
                /*
                 * if there exists a similar moving piece on the same rank and file as
                 * the move in question, use the square as the disambiguator
                 */
                return algebraic(from);
            } else if (sameFile > 0) {
                /*
                 * if the moving piece rests on the same file, use the rank symbol as the
                 * disambiguator
                 */
                return String.valueOf(algebraic(from).charAt(1));
            } else {
                // else use the file symbol
                return String.valueOf(algebraic(from).charAt(0));
            }
        }

        return "";
    }

    /**
     * Adds a move to the list of moves.
     * @param moves list of moves
     * @param color piece color
     * @param from from square
     * @param to to square
     * @param piece piece
     * @param captured captured piece
     * @param flags move flags
     */
    public static void addMove(
        List<InternalMove> moves,
        char color,
        int from,
        int to,
        char piece,
        char captured,
        int flags
    )
    {
        var rank = rank(to);

        if (piece == PieceSymbols.PAWN && (rank == Ranks.RANK_1 || rank == Ranks.RANK_8)) {
            for (var promotion : ChessConstants.PROMOTIONS) {
                moves.add(new InternalMove(color, from, to, piece, captured, promotion, flags | Bits.PROMOTION));
            }
        } else {
            moves.add(new InternalMove(color, from, to, piece, captured, PieceSymbols.EMPTY, flags));
        }
    }

    public static char inferPieceType(String san) {
        var pieceType = san.charAt(0);

        if (pieceType >= 'a' && pieceType <= 'h') {
            if (Pattern.matches("[a-h]\\d.*[a-h]\\d", san)) {
                return PieceSymbols.EMPTY;
            }
            return PieceSymbols.PAWN;
        }

        pieceType = Character.toLowerCase(pieceType);

        if (pieceType == 'o') {
            return PieceSymbols.KING;
        }
        return pieceType;
    }

    /**
     * Parses all the decorators out of a SAN string
     * @param move SAN
     * @return stripped SAN
     */
    public static String strippedSan(String move) {
        return move.replace("=", "").replaceAll("[+#]?[?!]*$", "");
    }

    /**
     * remove last two fields in FEN string as they're not needed when checking
     * for repetition
     * @param fen FEN
     * @return trimmed FEN
     */
    public static String trimFen(String fen) {
        var parts = fen.split(" ");
        return Arrays.stream(parts).limit(4).collect(Collectors.joining(" "));
    }
}
