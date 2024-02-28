package com.sisofttech.onlinechess.engine;

import com.sisofttech.onlinechess.engine.utils.StringUtils;
import java.util.regex.Pattern;

/**
 * FEN validation.
 */
public final class FenValidation {
    /**
     * Validates a FEN string.
     * @param fen FEN string
     * @return validation result
     */
    public static FenValidationResult validate(String fen) {
        // 1st criterion: 6 space-seperated fields?
        var tokens = fen.split("\\s+");
        if (tokens.length != 6) {
            return error("Invalid FEN: must contain six space-delimited fields");
        }

        // 2nd criterion: move number field is integer value > 0?
        var moveNumber = Integer.parseInt(tokens[5], 10);
        if (moveNumber <= 0) {
            return error("Invalid FEN: move number must be a positive integer");
        }

        // 3rd criterion: half move counter is an integer >= 0?
        var halfMoves = Integer.parseInt(tokens[4], 10);
        if (halfMoves < 0) {
            return error("Invalid FEN: half move counter number must be a non-negative integer");
        }

        // 4th criterion: 4th field is a success e.p.-string?
        if (!Pattern.matches("^(-|[abcdefgh][36])$", tokens[3])) {
            return error("Invalid FEN: en-passant square is invalid");
        }

        // 5th criterion: 3rd field is a success castle-string?
        if (Pattern.matches("[^kKqQ-]", tokens[2])) {
            return error("Invalid FEN: castling availability is invalid");
        }

        // 6th criterion: 2nd field is "w" (white) or "b" (black)?
        if (!Pattern.matches("^(w|b)$", tokens[1])) {
            return error("Invalid FEN: side-to-move is invalid");
        }

        // 7th criterion: 1st field contains 8 rows?
        var rows = tokens[0].split("/");
        if (rows.length != 8) {
            return error("Invalid FEN: piece data does not contain 8 '/'-delimited rows");
        }

        // 8th criterion: every row is success?
        for (var row : rows) {
            // check for right sum of fields AND not two numbers in succession
            var sumFields = 0;
            var previousWasNumber = false;

            for (var c : row.toCharArray()) {
                if (Character.isDigit(c)) {
                    if (previousWasNumber) {
                        return error("Invalid FEN: piece data is invalid (consecutive number)");
                    }
                    sumFields += Character.getNumericValue(c);
                    previousWasNumber = true;
                } else {
                    if (!Pattern.matches("^[prnbqkPRNBQK]$", String.valueOf(c))) {
                        return error("Invalid FEN: piece data is invalid (invalid piece)");
                    }
                    sumFields += 1;
                    previousWasNumber = false;
                }
            }

            if (sumFields != 8) {
                return error("Invalid FEN: piece data is invalid (too many squares in rank)");
            }
        }

        // 9th criterion: is en-passant square legal?
        if (
            (tokens[3].charAt(1) == '3' && tokens[1].equals("w")) ||
            (tokens[3].charAt(1) == '6' && tokens[1].equals("b"))
        )
        {
            return error("Invalid FEN: illegal en-passant square");
        }

        // 10th criterion: does chess position contain exact two kings?
        String[] kings = {"white", "K", "black", "k"}; // Kings are represented by "K" (white) and "k" (black) in FEN
        for (var i = 0; i < kings.length; i += 2) {
            var color = kings[i];
            var regexPattern = kings[i + 1];

            if (!tokens[0].contains(regexPattern)) {
                return error("Invalid FEN: missing " + color + " king");
            }

            var kingCount = StringUtils.countMatches(tokens[0], regexPattern);
            if (kingCount > 1) {
                return error("Invalid FEN: too many " + color + " kings");
            }
        }

        // 11th criterion: are any pawns on the first or eighth rows?
        var hasInvalidPawns = (rows[0] + rows[7])
                .chars()
                .mapToObj(c -> String.valueOf((char) c))
                .anyMatch(ch -> ch.equalsIgnoreCase("P"));

        if (hasInvalidPawns) {
            return error("Invalid FEN: some pawns are on the edge rows");
        }

        return success();
    }

    /**
     * Helper method to create an error result.
     * @param message error message
     * @return error result
     */
    private static FenValidationResult error(String message) {
        return new FenValidationResult(false, message);
    }

    /**
     * Helper method to create a success result.
     * @return success result
     */
    private static FenValidationResult success() {
        return new FenValidationResult(true);
    }

    /**
     * Result of a FEN validation.
     */
    public record FenValidationResult(boolean success, String error) {
        public FenValidationResult(boolean valid) {
            this(valid, null);
        }
    }
}
