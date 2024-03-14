package com.silyosbekov.chessmate.engine;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Pgn {
    private static final Pattern headerPattern = Pattern.compile("\\[(.*?) \"(.*?)\"]");
    private static final Pattern movePattern = Pattern.compile("^(1-0|0-1|1/2-1/2|\\*)|(\\d+\\.\\s?([NBRQK]?[a-h]?[1-8]?x?[a-h][1-8](=[NBRQK])?\\+?|#)?\\s?)+$");
    private final Map<String, String> headers;
    private final StringBuilder moves;

    public Pgn() {
        this.headers = new LinkedHashMap<>();
        this.moves = new StringBuilder();
        addBasicHeaders();
    }

    public Pgn(String whitePlayer, String blackPlayer) {
        this();
        setWhitePlayer(whitePlayer);
        setBlackPlayer(blackPlayer);
    }

    public Pgn(String whitePlayer) {
        this();
        setWhitePlayer(whitePlayer);
    }

    private void addBasicHeaders() {
        addHeader("Date", Instant.now().toString());
        addHeader("White", "?");
        addHeader("Black", "?");
        addHeader("Turn", "White");
        addHeader("TimeControl", "?");
        addHeader("Result", "*");
    }

    public void setWhitePlayer(String whitePlayer) {
        addHeader("White", whitePlayer);
    }

    public void setBlackPlayer(String blackPlayer) {
        addHeader("Black", blackPlayer);
    }

    public void setDrawResult() {
        addHeader("Result", "1/2-1/2");
    }

    public void setWhiteWinResult() {
        addHeader("Result", "1-0");
    }

    public void setBlackWinResult() {
        addHeader("Result", "0-1");
    }

    public void setBlackTurn() {
        addHeader("Turn", "Black");
    }

    public void setWhiteTurn() {
        addHeader("Turn", "White");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addComment(String comment) {
        if (!moves.isEmpty()) {
            moves.append(" {").append(comment).append("} ");
        }
    }

    public boolean addMove(String moveSan) {
        var matcher = movePattern.matcher(moveSan.trim());
        if (!matcher.matches()) {
            return false;
        }
        if (!moves.isEmpty()) {
            moves.append(" ");
        }
        moves.append(moveSan);
        return true;
    }

    public static Pgn fromString(String pgnString) {
        var newPgn = new Pgn();
        var moveSection = new StringBuilder();
        var headerMatcher = headerPattern.matcher(pgnString);

        while (headerMatcher.find()) {
            newPgn.addHeader(headerMatcher.group(1), headerMatcher.group(2));
        }

        // Split after the last header (assuming headers are at the beginning)
        var splitPgn = pgnString.split("\n\n", 2);

        if (splitPgn.length == 0) {
            return newPgn;
        }

        // Process moves and comments
        var moveMatcher = Pattern.compile("([1-9]\\d*\\.\\s+[^{}]+)|(\\{[^}]+})").matcher(splitPgn[1]); // Matches moves and comments

        while (moveMatcher.find()) {
            var match = moveMatcher.group().trim();

            if (!match.isEmpty()) {
                moveSection.append(match).append(" ");
            }
        }

        newPgn.moves.append(moveSection.toString().trim());
        return newPgn;
    }

    @Override
    public String toString() {
        var pgnBuilder = new StringBuilder();

        for (var header : headers.entrySet()) {
            pgnBuilder
                    .append("[")
                    .append(header.getKey())
                    .append(" \"")
                    .append(header.getValue())
                    .append("\"]\n");
        }

        pgnBuilder.append("\n");
        pgnBuilder.append(moves.toString().trim());
        return pgnBuilder.toString();
    }
}
