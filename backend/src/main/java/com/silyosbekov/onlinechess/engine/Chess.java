package com.silyosbekov.onlinechess.engine;

import com.silyosbekov.onlinechess.engine.constants.*;
import com.silyosbekov.onlinechess.engine.options.MoveOptions;
import com.silyosbekov.onlinechess.engine.utils.ArrayUtils;
import com.silyosbekov.onlinechess.engine.utils.EncodingUtils;
import com.silyosbekov.onlinechess.engine.utils.StringUtils;
import com.silyosbekov.onlinechess.engine.options.MovesOptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The main class of the engine that represents a chess game. It provides methods to manipulate the game state,
 * generate legal moves, validate moves, and convert moves to and from Standard Algebraic Notation (SAN).
 */
public class Chess {
    /**
     * The starting position of a chess game in Forsyth-Edwards Notation (FEN).
     */
    public static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /**
     * Empty square constant.
     */
    private static final int EMPTY = -1;

    /**
     * The board array. The board is represented as 1D array of 128 elements using 0x88 board representation.
     */
    private final Piece[] board = new Piece[128];

    /**
     * The headers of the game. PGN headers are key/value pairs that provide information about the game.
     */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * The kings' positions.
     */
    private Map<Character, Integer> kings = new HashMap<>();

    /**
     * The castling rights.
     */
    private Map<Character, Integer> castling = new HashMap<>();

    /**
     * The comments of the game in PGN format.
     */
    private Map<String, String> comments = new HashMap<>();

    /**
     * Tracks number of times a position has been seen for repetition checking
     */
    private final Map<String, Integer> positionCount = new HashMap<>();

    /**
     * The history of the game.
     */
    private final List<History> history = new ArrayList<>();

    /**
     * The color of the player whose turn it is.
     */
    private char turn = PieceColors.WHITE;

    /**
     * The en passant square.
     */
    private int epSquare = EMPTY;

    /**
     * The number of half moves.
     */
    private int halfMoves = 0;

    /**
     * The current move number.
     */
    private int moveNumber = 1;

    /**
     * Constructs a new chess game with the default starting position.
     */
    public Chess() {
        this.load(DEFAULT_POSITION, false, false);
    }

    /**
     * Constructs a new chess game with the given FEN string.
     *
     * @param fen The FEN string.
     */
    public Chess(String fen) {
        this.load(fen, false, false);
        kings.put(PieceColors.WHITE, EMPTY);
        kings.put(PieceColors.BLACK, EMPTY);
        castling.put(PieceColors.WHITE, 0);
        castling.put(PieceColors.BLACK, 0);
    }

    /**
     * Gets the current move number.
     */
    public int getMoveNumber() {
        return moveNumber;
    }

    /**
     * Clears the board. The headers are not preserved.
     */
    public void clear() {
        clear(false);
    }

    /**
     * Clears the board. If preserveHeaders is true, the headers are preserved.
     *
     * @param preserveHeaders Whether to preserve the headers.
     */
    public void clear(boolean preserveHeaders) {
        if (!preserveHeaders) {
            headers.clear();
        } else {
           this.headers.remove("SetUp");
           this.headers.remove("FEN");
        }

        for (var i = 0; i < 128; i++) {
            board[i] = null;
        }

        kings.put(PieceColors.WHITE, EMPTY);
        kings.put(PieceColors.BLACK, EMPTY);
        castling.put(PieceColors.WHITE, 0);
        castling.put(PieceColors.BLACK, 0);
        turn = PieceColors.WHITE;
        epSquare = EMPTY;
        halfMoves = 0;
        moveNumber = 1;
        history.clear();
        positionCount.clear();
        comments.clear();
    }

    /**
     * Removes the header with the given key.
     * @param key The key of the header to remove.
     */
    public void removeHeader(String key) {
        headers.remove(key);
    }

    /**
     * Loads the game from the given FEN string.
     * @param fen The FEN string.
     */
    public void load(String fen) {
        load(fen, false, false);
    }

    /**
     * Loads the game from the given FEN string.
     * @param fen The FEN string.
     * @param skipValidation Whether to skip validation.
     * @param preserveHeaders Whether to preserve the headers.
     */
    public void load(String fen, boolean skipValidation, boolean preserveHeaders) {
        var tokens = fen.split("\\s+");

        // append commonly omitted fen tokens
        if (tokens.length >= 2 && tokens.length < 6) {
            String[] adjustments = {"-", "-", "0", "1"};
            var slicedAdjustments = Arrays.copyOfRange(adjustments, adjustments.length - (6 - tokens.length), adjustments.length);
            fen = String.join(" ", ArrayUtils.concat(tokens, slicedAdjustments));
        }

        tokens = fen.split("\\s+");

        if (!skipValidation) {
            var result = FenValidation.validate(fen);

            if (!result.success()) {
                throw new IllegalArgumentException(result.error());
            }
        }

        var position = tokens[0];
        var square = 0;

        this.clear(preserveHeaders);

        for (int i = 0; i < position.length(); i++) {
            var piece = position.charAt(i);

            if (piece == '/') {
                square += 8;
            }
            else if (Character.isDigit(piece)) {
                square += Character.digit(piece, 10);
            } else {
                var color = piece < 'a' ? PieceColors.WHITE : PieceColors.BLACK;
                this.put(new Piece(Character.toLowerCase(piece), color), algebraic(square), false);
                square++;
            }
        }

        this.turn = tokens[1].charAt(0);

        if (tokens[2].contains("K")) {
            setCastlingBit(PieceColors.WHITE, Bits.KSIDE_CASTLE);
        }
        if (tokens[2].contains("Q")) {
            setCastlingBit(PieceColors.WHITE, Bits.QSIDE_CASTLE);
        }
        if (tokens[2].contains("k")) {
            setCastlingBit(PieceColors.BLACK, Bits.KSIDE_CASTLE);
        }
        if (tokens[2].contains("q")) {
            setCastlingBit(PieceColors.BLACK, Bits.QSIDE_CASTLE);
        }

        this.epSquare = tokens[3].equals("-") ? EMPTY : Ox88.get(tokens[3]);
        this.halfMoves = Integer.parseInt(tokens[4]);
        this.moveNumber = Integer.parseInt(tokens[5]);

        this.updateSetup(fen);
        this.incPositionCount(fen);
    }

    /**
     * Gets the FEN string of the current position.
     * @return The FEN string.
     */
    public String fen() {
        var empty = 0;
        var fenBuilder = new StringBuilder();

        for (int i = Ox88.A8; i <= Ox88.H1; i++) {
            if (board[i] != null) {
                if (empty > 0) {
                    fenBuilder.append(empty);
                    empty = 0;
                }
                var piece = this.board[i];
                var fenPiece = piece.getColor() == PieceColors.WHITE ? Character.toUpperCase(piece.getType()) : Character.toLowerCase(piece.getType());
                fenBuilder.append(fenPiece);
            } else {
                empty++;
            }

            if (((i + 1) & 0x88) != 0) {
                if (empty > 0) {
                    fenBuilder.append(empty);
                }

                if (i != Ox88.H1) {
                    fenBuilder.append('/');
                }

                empty = 0;
                i += 8;
            }
        }

        var castlingBuilder = new StringBuilder();
        if ((this.castling.get(PieceColors.WHITE) & Bits.KSIDE_CASTLE) != 0) {
            castlingBuilder.append('K');
        }
        if ((this.castling.get(PieceColors.WHITE) & Bits.QSIDE_CASTLE) != 0) {
            castlingBuilder.append('Q');
        }
        if ((this.castling.get(PieceColors.BLACK) & Bits.KSIDE_CASTLE) != 0) {
            castlingBuilder.append('k');
        }
        if ((this.castling.get(PieceColors.BLACK) & Bits.QSIDE_CASTLE) != 0) {
            castlingBuilder.append('q');
        }

        // do we have an empty castling name?
        var castlingStr = !castlingBuilder.isEmpty() ? castlingBuilder.toString() : "-";
        var epSquare = "-";

        /*
         * only print the ep square if en passant is a valid move (pawn is present
         * and ep capture is not pinned)
         */
        if (this.epSquare != EMPTY) {
            var bigPawnSquare = this.epSquare + (turn == PieceColors.WHITE ? 16 : -16);
            int[] squares = {bigPawnSquare + 1, bigPawnSquare - 1};

            for (var square : squares) {
                // is the square off the board?
                if ((square & 0x88) != 0) {
                    continue;
                }

                var color = turn;

                // is there a pawn that can capture the epSquare?
                if (
                    board[square] != null &&
                    board[square].getColor() == color &&
                    board[square].getType() == PieceTypes.PAWN
                )
                {
                    // if the pawn makes an ep capture, does it leave it's king in check?
                    var move = new InternalMove(
                        color,
                        square,
                        this.epSquare,
                        PieceTypes.PAWN,
                        PieceTypes.PAWN,
                        Bits.EP_CAPTURE
                    );

                    makeMove(move);
                    var isLegal = !isKingAttacked(color);
                    undoMove();

                    // if ep is legal, break and set the ep square in the FEN output
                    if (isLegal) {
                        epSquare = algebraic(this.epSquare);
                        break;
                    }
                }
            }
        }

        return String.join(
            " ",
            fenBuilder.toString(),
            Character.toString(turn),
            castlingStr,
            epSquare,
            Integer.toString(halfMoves),
            Integer.toString(this.moveNumber)
        );
    }

    /**
     * Resets the game to the default starting position.
     * Removes all headers.
     */
    public void reset() {
        load(DEFAULT_POSITION, false, false);
    }

    /**
     * Gets a piece from the given square.
     * @param square The board square in algebraic notation. e.g. "e4", "a1", "h8", etc.
     * @return The piece on the square, or null if the square is empty.
     */
    public Piece get(String square) {
        var sq = Ox88.get(square);
        return sq == -1 ? null : board[sq];
    }

    /**
     * Puts a piece on the given square.
     * @param piece The piece to put on the square.
     * @param square The board square in algebraic notation. e.g. "e4", "a1", "h8", etc.
     * @return True if the piece was successfully placed on the square, otherwise false.
     */
    public boolean put(Piece piece, String square) {
        return put(piece, square, true);
    }

    /**
     * Puts a piece on the given square.
     * @param piece The piece to put on the square.
     * @param square The board square in algebraic notation. e.g. "e4", "a1", "h8", etc.
     * @param updateSetup Whether to update the setup.
     * @return True if the piece was successfully placed on the square, otherwise false.
     */
    private boolean put(Piece piece, String square, boolean updateSetup) {
        // check for piece
        if (PieceTypes.SYMBOLS.indexOf(Character.toLowerCase(piece.getType())) == -1) {
            return false;
        }

        // check for valid square
        if (Ox88.get(square) == -1) {
            return false;
        }

        var sq = Ox88.get(square);

        // don't let the user place more than one king
        if (
            piece.getType() == PieceTypes.KING &&
            !(this.kings.get(piece.getColor()) == EMPTY || this.kings.get(piece.getColor()) == sq)
        )
        {
            return false;
        }

        var currentPieceOnSquare = this.board[sq];

        // if one of the kings will be replaced by the piece from args, set the `kings` respective entry to `EMPTY`
        if (currentPieceOnSquare != null && currentPieceOnSquare.getType() == PieceTypes.KING) {
            this.kings.put(currentPieceOnSquare.getColor(), EMPTY);
        }

        this.board[sq] = piece;

        if (piece.getType() == PieceTypes.KING) {
            this.kings.put(piece.getColor(), sq);
        }

        if (updateSetup) {
            updateCastlingRights();
            updateEnPassantSquare();
            updateSetup(fen());
        }

        return true;
    }

    /**
     * Removes a piece from the given square.
     * @param square The board square in algebraic notation. e.g. "e4", "a1", "h8", etc.
     * @return The piece that was removed from the square, or null if the square was empty.
     */
    public Piece remove(String square) {
        var piece = get(square);
        board[Ox88.get(square)] = null;

        if (piece != null && piece.getType() == PieceTypes.KING) {
            kings.put(piece.getColor(), EMPTY);
        }

        updateCastlingRights();
        updateEnPassantSquare();
        updateSetup(fen());
        return piece;
    }

    /**
     * Is the square attacked by the given color? (i.e. is the king in check?)
     * @param attackerColor The color that is attacking the square.
     * @param square The board square in algebraic notation. e.g. "e4", "a1", "h8", etc.
     * @return True if the square is attacked by the given color, otherwise false.
     */
    public boolean isAttacked(char attackerColor, String square) {
        var sq = Ox88.get(square);
        return isAttacked(attackerColor, sq);
    }

    /**
     * Is the square attacked by the given attackerColor? (i.e. is the king in check?)
     * @param attackerColor The color that is attacking the square.
     * @param square The board square in 0x88 notation. e.g. 0, 1, 2, 3, etc.
     * @return True if the square is attacked by the given attackerColor, otherwise false.
     */
    private boolean isAttacked(char attackerColor, int square) {
        for (int i = Ox88.A8; i <= Ox88.H1; i++) {
            // did we run off the end of the board
            if ((i & 0x88) != 0) {
                i += 7;
                continue;
            }

            // if empty square or wrong attackerColor
            if (board[i] == null || board[i].getColor() != attackerColor) {
                continue;
            }

            var piece = board[i];
            var difference = i - square;

            // skip - to/from square are the same
            if (difference == 0) {
                continue;
            }

            var index = difference + 119;

            if ((ChessConstants.ATTACKS[index] & PieceMasks.get(piece.getType())) != 0) {
                if (piece.getType() == PieceTypes.PAWN) {
                    if (difference > 0 && piece.getColor() == PieceColors.WHITE) {
                        return true;
                    }

                    if (difference < 0 && piece.getColor() == PieceColors.BLACK) {
                        return true;
                    }

                    continue;
                }

                // if the piece is a knight or a king
                if (piece.getType() == PieceTypes.KNIGHT || piece.getType() == PieceTypes.KING) {
                    return true;
                }

                var offset = ChessConstants.RAYS[index];
                var j = i + offset;
                var blocked = false;

                while (j != square) {
                    if (board[j] != null) {
                        blocked = true;
                        break;
                    }
                    j += offset;
                }

                if (!blocked) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Whether the specified side king is in check.
     * @param color The color of the king.
     * @return True if the king is in check, otherwise false.
     */
    private boolean isKingAttacked(char color) {
        var square = this.kings.get(color);
        return square != -1 && this.isAttacked(swapColor(color), square);
    }

    /**
     * Whether the current side king is in check.
     * @return True if the king is in check, otherwise false.
     */
    public boolean isCheck() {
        return isKingAttacked(turn);
    }

    /**
     * Whether the current side king is in checkmate.
     * @return True if the king is in checkmate, otherwise false.
     */
    public boolean isCheckmate() {
        return isCheck() && generateMovesInternal(new MovesOptions()).isEmpty();
    }

    /**
     * Whether the current side king is in stalemate.
     * @return True if the king is in stalemate, otherwise false.
     */
    public boolean isStalemate() {
        return !isCheck() && generateMovesInternal(new MovesOptions()).isEmpty();
    }

    /**
     * Whether the current side king is in insufficient material.
     * @return True if the king is in insufficient material, otherwise false.
     */
    public boolean isInsufficientMaterial() {
        /*
         * k.b. vs k.b. (of opposite colors) with mate in 1:
         * 8/8/8/8/1b6/8/B1k5/K7 b - - 0 1
         *
         * k.b. vs k.n. with mate in 1:
         * 8/8/8/8/1n6/8/B7/K1k5 b - - 2 1
         */
        var pieces = new HashMap<Character,Integer>();
        pieces.put(PieceTypes.BISHOP, 0);
        pieces.put(PieceTypes.KNIGHT, 0);
        pieces.put(PieceTypes.ROOK, 0);
        pieces.put(PieceTypes.QUEEN, 0);
        pieces.put(PieceTypes.KING, 0);
        pieces.put(PieceTypes.PAWN, 0);

        var bishops = new ArrayList<Integer>();
        var numPieces = 0;
        var squareColor = 0;

        for (var i = Ox88.A8; i <= Ox88.H1; i++) {
            squareColor = (squareColor + 1) % 2;
            if ((i & 0x88) != 0) {
                i += 7;
                continue;
            }

            var piece = board[i];
            if (piece != null) {
                pieces.put(piece.getType(), pieces.get(piece.getType()) + 1);
                if (piece.getType() == PieceTypes.BISHOP) {
                    bishops.add(squareColor);
                }
                numPieces++;
            }
        }

        // k vs. k
        if (numPieces == 2) {
            return true;
        }
        else if (numPieces == 3 && (pieces.get(PieceTypes.BISHOP) == 1 || pieces.get(PieceTypes.KNIGHT) == 1)) {
            // k vs. kn .... or .... k vs. kb
            return true;
        }
        else if (numPieces == pieces.get(PieceTypes.BISHOP) + 2) {
            // kb vs. kb where any number of bishops are all on the same color
            var sum = 0;
            for (var bishop : bishops) {
                sum += bishop;
            }
            return sum == 0 || sum == bishops.size();
        }

        return false;
    }

    /**
     * Whether the current position has occurred three times.
     * @return True if the position has occurred three times, otherwise false.
     */
    public boolean isThreefoldRepetition() {
        return positionCount.get(fen()) >= 3;
    }

    /**
     * Whether the current position is a draw.
     * A position is a draw if:
     * 1) the half-move clock is greater than or equal to 100,
     * 2) the current side is in stalemate,
     * 3) the current side has insufficient material to checkmate the opponent,
     * 4) the current position has occurred three times.
     * @return True if the position is a draw, otherwise false.
     */
    public boolean isDraw() {
        return halfMoves >= 100 || // 50 moves per side = 100 half moves
                isStalemate() ||
                isInsufficientMaterial() ||
                isThreefoldRepetition();
    }

    /**
     * Whether the game is over.
     * The game is over if the current position is a draw, checkmate, or stalemate.
     * @return True if the game is over, otherwise false.
     */
    public boolean isGameOver() {
        return isCheckmate() || isStalemate() || isDraw();
    }

    /**
     * Generates all possible moves for the current position.
     * @return An array of legal moves.
     */
    public Move[] generateMoves() {
        var moves = this.generateMovesInternal(new MovesOptions(true, false));
        return moves.stream().map(this::makePretty).toArray(Move[]::new);
    }

    /**
     * Generates all possible moves for the current position.
     * @return An array of moves in SAN format. e.g. "e4", "Nf3", "exd5", etc.
     */
    public String[] generateMovesAsSan() {
        var moves = generateMovesInternal(new MovesOptions());
        return moves.stream().map(move -> moveToSan(move, moves)).toArray(String[]::new);
    }

    /**
     * Generates all possible moves for the current position.
     * @param options The move options.
     * @return An array of moves in SAN format. e.g. "e4", "Nf3", "exd5", etc.
     */
    private List<InternalMove> generateMovesInternal(MovesOptions options) {
        var forSquare = options.square.map(String::toLowerCase).orElse(null);
        var forPiece = options.piece.orElse(null);

        var moves = new ArrayList<InternalMove>();
        var us = this.turn;
        var them = swapColor(us);

        var firstSquare = Ox88.A8;
        var lastSquare = Ox88.H1;
        var singleSquare = false;

        // are we generating moves for a single square?
        if (forSquare != null) {
            if (Ox88.get(forSquare) == -1) { // illegal square, return empty moves
                return new ArrayList<>();
            } else {
                firstSquare = lastSquare = Ox88.get(forSquare);
                singleSquare = true;
            }
        }

        for (var from = firstSquare; from <= lastSquare; from++) {
            // did we run off the end of the board
            if ((from & 0x88) != 0) {
                from += 7;
                continue;
            }

            // empty square or opponent, skip
            if (board[from] == null || board[from].getColor() == them) {
                continue;
            }

            var type = board[from].getType();

            if (forPiece != null && forPiece != type) {
                continue;
            }

            int to;
            if (type == PieceTypes.PAWN) {
                // single square, non-capturing
                to = from + PawnOffsets.get(us)[0];
                if (board[to] == null) {
                    addMove(moves, us, from, to, PieceTypes.PAWN, null, 0);

                    // double square
                    to = from + PawnOffsets.get(us)[1];
                    if (Ranks.getSecondRank(us) == rank(from) && board[to] == null) {
                        addMove(moves, us, from, to, PieceTypes.PAWN, null, Bits.BIG_PAWN);
                    }
                }

                // pawn captures
                for (int j = 2; j < 4; j++) {
                    to = from + PawnOffsets.get(us)[j];

                    if ((to & 0x88) != 0) {
                        continue;
                    }

                    if (board[to] != null && board[to].getColor() == them) {
                        addMove(moves, us, from, to, PieceTypes.PAWN, board[to].getType(), Bits.CAPTURE);
                    }
                    else if (to == epSquare) {
                        addMove(moves, us, from, to, PieceTypes.PAWN, PieceTypes.PAWN, Bits.EP_CAPTURE);
                    }
                }
            } else {
                for (var j = 0; j < PieceOffsets.get(type).length; j++) {
                    var offset = PieceOffsets.get(type)[j];
                    to = from;

                    while (true) {
                        to += offset;
                        if ((to & 0x88) != 0) {
                            break;
                        }

                        if (board[to] == null) {
                            addMove(moves, us, from, to, type, null, 0);
                        }
                        else {
                            // own color, stop loop
                            if (board[to].getColor() == us) {
                                break;
                            }

                            addMove(moves, us, from, to, type, board[to].getType(), Bits.CAPTURE);
                            break;
                        }

                        // break, if knight or king
                        if (type == PieceTypes.KNIGHT || type == PieceTypes.KING) {
                            break;
                        }
                    }
                }
            }
        }

        /*
         * check for castling if we're:
         *   a) generating all moves, or
         *   b) doing single square move generation on the king's square
         */
        if (forPiece == null || forPiece == PieceTypes.KING) {
            if (!singleSquare || lastSquare == kings.get(us)) {
                // king-side castling
                if ((castling.get(us) & Bits.KSIDE_CASTLE) != 0) {
                    var castlingFrom = kings.get(us);
                    var castlingTo = castlingFrom + 2;

                    if (
                        board[castlingFrom + 1] == null &&
                        board[castlingTo] == null &&
                        !this.isAttacked(them, kings.get(us)) &&
                        !this.isAttacked(them, castlingFrom + 1) &&
                        !this.isAttacked(them, castlingTo)
                    )
                    {
                        addMove(moves, us, kings.get(us), castlingTo, PieceTypes.KING, null, Bits.KSIDE_CASTLE);
                    }
                }

                // queen-side castling
                if ((castling.get(us) & Bits.QSIDE_CASTLE) != 0) {
                    var castlingFrom = kings.get(us);
                    var castlingTo = castlingFrom - 2;

                    if (
                        board[castlingFrom - 1] == null &&
                        board[castlingFrom - 2] == null &&
                        board[castlingFrom - 3] == null &&
                        !this.isAttacked(them, kings.get(us)) &&
                        !this.isAttacked(them, castlingFrom - 1) &&
                        !this.isAttacked(them, castlingTo)
                    )
                    {
                        addMove(moves, us, kings.get(us), castlingTo, PieceTypes.KING, null, Bits.QSIDE_CASTLE);
                    }
                }
            }
        }

        /*
         * return all pseudo-legal moves (this includes moves that allow the king
         * to be captured)
         */
        if (!options.legal || kings.get(us) == -1) {
            return moves;
        }

        // filter out illegal moves
        var legalMoves = new ArrayList<InternalMove>();

        for (var move : moves) {
            this.makeMove(move);
            if (!isKingAttacked(us)) {
                legalMoves.add(move);
            }
            this.undoMove();
        }

        return legalMoves;
    }

    private void makeMove(InternalMove move) {
        var us = turn;
        var them = swapColor(us);
        addToHistory(move);

        board[move.getTo()] = board[move.getFrom()];
        board[move.getFrom()] = null;

        // if en passant capture, remove the captured pawn
        if ((move.getFlags() & Bits.EP_CAPTURE) != 0) {
            if (turn == PieceColors.BLACK) {
                board[move.getTo() - 16] = null;
            } else {
                board[move.getFrom() + 16] = null;
            }
        }

        // if pawn promotion, replace with new piece
        if (move.getPromotion() != null) {
            board[move.getTo()] = new Piece(move.getPromotion(), us);
        }

        // if we moved the king
        if (board[move.getTo()].getType() == PieceTypes.KING) {
            kings.put(us, move.getTo());

            // if we castled, move the rook next to the king
            if ((move.getFlags() & Bits.KSIDE_CASTLE) != 0) {
                var castlingTo = move.getTo() - 1;
                var castlingFrom = move.getTo() + 1;
                board[castlingTo] = board[castlingFrom];
                board[castlingFrom] = null;
            }
            else if ((move.getFlags() & Bits.QSIDE_CASTLE) != 0) {
                var castlingTo = move.getTo() + 1;
                var castlingFrom = move.getTo() - 2;
                board[castlingTo] = board[castlingFrom];
                board[castlingFrom] = null;
            }

            // turn off castling
            castling.put(us, 0);
        }

        // turn off castling if we move a rook
        if (castling.get(us) != 0) {
            for (var i = 0; i < Bits.ROOKS.get(us).length; i++) {
                if (
                    move.getFrom() == Bits.ROOKS.get(us)[i].square() &&
                    (castling.get(us) & Bits.ROOKS.get(us)[i].flag()) != 0
                )
                {
                    castling.put(us, castling.get(us) ^ Bits.ROOKS.get(us)[i].flag());
                    break;
                }
            }
        }

        // turn off castling if we capture a rook
        if (castling.get(them) != 0) {
            for (var i = 0; i < Bits.ROOKS.get(them).length; i++) {
                if (
                    move.getTo() == Bits.ROOKS.get(them)[i].square() &&
                    (castling.get(them) & Bits.ROOKS.get(them)[i].flag()) != 0
                )
                {
                    castling.put(them, castling.get(them) ^ Bits.ROOKS.get(them)[i].flag());
                    break;
                }
            }
        }

        // if big pawn move, update the en passant square
        if ((move.getFlags() & Bits.BIG_PAWN) != 0) {
            if (us == PieceColors.BLACK) {
                epSquare = move.getTo() - 16;
            }
            else {
                epSquare = move.getTo() + 16;
            }
        } else {
            epSquare = EMPTY;
        }

        // reset the 50 move counter if a pawn is moved or a piece is captured
        if (move.getPiece() == PieceTypes.PAWN) {
            halfMoves = 0;
        }
        else if ((move.getFlags() & (Bits.CAPTURE | Bits.EP_CAPTURE)) != 0) {
            halfMoves = 0;
        }
        else {
            halfMoves++;
        }

        if (us == PieceColors.BLACK) {
            moveNumber++;
        }

        turn = them;
    }

    /**
     * The move function can be called with in the following parameters:
     * .move('Nxb7')       <- argument is a case-sensitive SAN string
     * .move({ from: 'h7', <- argument is a move object
     *         to :'h8',
     *         promotion: 'q' })
     * An optional strict argument may be supplied to tell chess.js to
     * strictly follow the SAN specification.
     */
    public Move move(MoveOptions options) {
        InternalMove moveObj = null;

        if (options.san.isPresent()) {
            moveObj = moveFromSan(options.san.get(), options.strict);
        }
        else if (options.from.isPresent() && options.to.isPresent()) {
            var moves = this.generateMovesInternal(new MovesOptions());

            // convert the pretty move object to an ugly move object
            for (var m : moves) {
                if (
                    options.from.get().equals(algebraic(m.getFrom())) &&
                    options.to.get().equals(algebraic(m.getTo())) &&
                    (m.getPromotion() == null ||
                    (options.promotion.isPresent() &&
                     options.promotion.get() == m.getPromotion()))
                )
                {
                    moveObj = m;
                    break;
                }
            }
        }

        // failed to find move
        if (moveObj == null) {
            if (options.san.isPresent()) {
                throw new IllegalArgumentException("Invalid move: " + options.san.get());
            }
            else if (options.from.isPresent() && options.to.isPresent()) {
                throw new IllegalArgumentException("Invalid move: " + "from " + options.from.get() + " to " + options.to.get());
            }
        }

        /*
         * need to make a copy of move because we can't generate SAN after the move
         * is made
         */
        var prettyMove = makePretty(moveObj);

        makeMove(moveObj);
        incPositionCount(prettyMove.getAfter());
        return prettyMove;
    }

    /**
     * Undoes the last move.
     * @return The move that was undone, or null if there was no move to undo.
     */
    public Move undo() {
        var move = undoMove();
        if (move != null) {
            var prettyMove = makePretty(move);
            decPositionCount(prettyMove.getAfter());
            return prettyMove;
        }
        return null;
    }

    /**
     * Generates the PGN of the game.
     * @return The PGN string.
     */
    public String pgn() {
        return pgn("\n", 0);
    }

    /**
     * Generates the PGN of the game.
     * @param newline The newline character to use.
     * @param maxWidth The maximum width of the PGN string.
     * @return The PGN string.
     */
    public String pgn(String newline, int maxWidth) {
        var result = new ArrayList<String>();
        var headerExists = false;

        /* add the PGN header information */
        for (var entry : headers.entrySet()) {
            result.add("[" + entry.getKey() + " \"" + entry.getValue() + "\"]" + newline);
            headerExists = true;
        }

        if (headerExists && !history.isEmpty()) {
            result.add(newline);
        }

        // pop all history onto reversed_history
        var reversedHistory = new Stack<InternalMove>();
        while (!history.isEmpty()) {
            reversedHistory.push(undoMove());
        }

        var moves = new ArrayList<String>();
        var moveString = "";

        // special case of a commented starting position with no moves
        if (reversedHistory.isEmpty()) {
            moves.add(appendComment(""));
        }

        // build the list of moves.  a move_string looks like: "3. e3 e6"
        while (!reversedHistory.isEmpty()) {
            moveString = appendComment(moveString);
            var move = reversedHistory.pop();

            // if the position started with black to move, start PGN with #. ...
            if (history.isEmpty() && move.getColor() == PieceColors.BLACK) {
                var prefix = moveNumber + ". ...";

                // is there a comment preceding the first move?
                moveString = moveString.isEmpty() ? prefix : moveString + " " + prefix;
            }
            else if (move.getColor() == PieceColors.WHITE) {
                // store the previous generated move_string if we have one
                if (!moveString.isEmpty()) {
                    moves.add(moveString);
                }
                moveString = moveNumber + ".";
            }

            moveString = moveString + " " + moveToSan(move, generateMovesInternal(new MovesOptions(true)));
            makeMove(move);
        }

        // are there any other leftover moves?
        if (!moveString.isEmpty()) {
            moves.add(appendComment(moveString));
        }

        // is there a result?
        if (headers.containsKey("Result")) {
            moves.add(headers.get("Result"));
        }

        // history should be back to what it was before we started generating PGN,
        // so join together moves
        if (maxWidth == 0) {
            return String.join("", result) + String.join(" ", moves);
        }

        // wrap the PGN output at max_width
        var currentWidth = 0;
        for (var i = 0; i < moves.size(); i++) {
            if (currentWidth + moves.get(i).length() > maxWidth) {
                if (moves.get(i).contains("{")) {
                    currentWidth = wrapComment(currentWidth, moves.get(i), result, newline, maxWidth);
                    continue;
                }
            }

            // if the current move will push past max_width
            if (currentWidth + moves.get(i).length() > maxWidth && i != 0) {
                // don't end the line with whitespace
                if (result.getLast().equals(" ")) {
                    result.removeLast();
                }

                result.add(newline);
                currentWidth = 0;
            }
            else if (i != 0) {
                result.add(" ");
                currentWidth++;
            }

            result.add(moves.get(i));
            currentWidth += moves.get(i).length();
        }

        return String.join("", result);
    }

    /**
     * Loads the game from a PGN string.
     * @param pgn The PGN string.
     * @param strict Whether to parse the PGN strictly.
     * @param newline The newline character to use. If null, the default newline character is used, which is \r?\n.
     */
    public void loadPgn(String pgn, boolean strict, String newline) {
        newline = newline == null ? "\\r?\\n" : newline;
        var maskedNewline = newline.replace("\\", "\\\\");

        // strip whitespace from head/tail of PGN block
        pgn = pgn.trim();

        var headerRegex = "^(\\[((?:" +
                maskedNewline +
                ")|.)*])((?:\\s*" +
                maskedNewline +
                "){2}|(?:\\s*" +
                maskedNewline +
                ")*$)";
        var pattern = Pattern.compile(headerRegex);
        var matcher = pattern.matcher(pgn);

        var headerString = "";
        if (matcher.find()) {
            headerString = matcher.group(1);
        }

        // Put the board in the starting position
        this.reset();

        // parse PGN header
        var headers = parsePgnHeader(headerString, maskedNewline);
        var fen = "";

        for (var key : headers.keySet()) {
            // check to see user is including fen (possibly with wrong tag case)
            if (key.equalsIgnoreCase("fen")) {
                fen = headers.get(key);
            }
            headers.put(key, headers.get(key));
        }

        /*
         * the permissive parser should attempt to load a fen tag, even if it's the
         * wrong case and doesn't include a corresponding [SetUp "1"] tag
         */
        if (!strict) {
            if (!StringUtils.isNullOrEmpty(fen)) {
                this.load(fen, false, true);
            }
        }
        else {
            /*
             * strict parser - load the starting position indicated by [Setup '1']
             * and [FEN position]
             */
            if (headers.get("SetUp") != null && headers.get("SetUp").equals("1")) {
                if (!headers.containsKey("FEN")) {
                    throw new IllegalStateException("Invalid PGN: FEN tag must be supplied with SetUp tag");
                }

                // don't clear the headers when loading
                this.load(headers.get("FEN"), false, true);
            }
        }

        // delete header to get the moves
        var ms = pgn.replace(headerString, "");

        // Encode comments so they don't get deleted
        ms = encodePgnComment(ms, maskedNewline);

        // Replace newline characters with spaces
        ms = ms.replaceAll(maskedNewline, " ");

        // delete recursive annotation variations
        var ravRegex = Pattern.compile("(\\([^()]+\\))+?");
        while (ravRegex.matcher(ms).find()) {
            ms = ms.replaceAll(ravRegex.pattern(), "");
        }

        // delete move numbers
        ms = ms.replaceAll("\\d+\\.\\.\\.", "");

        // delete ... indicating black to move
        ms = ms.replaceAll("\\.\\.\\.", "");

        /* delete numeric annotation glyphs */
        ms = ms.replaceAll("\\$\\d+", "");

        // trim and get array of moves
        var moves = ms.trim().split("\\s+");

        // delete empty entries
        moves = Arrays.stream(moves).filter(move -> !move.isEmpty()).toArray(String[]::new);

        var result = "";

        for (int halfMove = 0; halfMove < moves.length; halfMove++) {
            var comment = EncodingUtils.decodeComment(moves[halfMove]);

            if (!StringUtils.isNullOrEmpty(comment)) {
                this.comments.put(this.fen(), comment);
                continue;
            }

            var move = this.moveFromSan(moves[halfMove], strict);

            // invalid move
            if (move == null) {
                // was the move an end of game marker
                if (ArrayUtils.contains(ChessConstants.TERMINATION_MARKERS, moves[halfMove])) {
                    result = moves[halfMove];
                }
                else {
                    throw new IllegalStateException("Invalid move in PGN: " + moves[halfMove]);
                }
            }
            else {
                // reset the end of game marker if making a valid move
                result = "";
                makeMove(move);
                incPositionCount(fen());
            }
        }

        /*
         * Per section 8.2.6 of the PGN spec, the Result tag pair must match
         * the termination marker. Only do this when headers are present, but the
         * result tag is missing
         */
        if (result != null && !headers.isEmpty() && !headers.containsKey("Result")) {
            headers.put("Result", result);
        }
    }

    private Map<String, String> parsePgnHeader(String headerStr, String newline) {
        var headersMap = new HashMap<String, String>();
        var headersArr = headerStr.split(newline);
        var key = "";
        var value = "";
        var regex = Pattern.compile("^\\s*\\[\\s*([A-Za-z]+)\\s*\"(.*)\"\\s*\\]\\s*$");

        for (var header : headersArr) {
            var matcher = regex.matcher(header);

            if (matcher.find()) {
                key = matcher.group(1);
                value = matcher.group(2);

                if (!key.trim().isEmpty()) {
                    headersMap.put(key, value);
                }
            }
        }

        return headersMap;
    }

    private String encodePgnComment(String comment, String newline) {
        var commentPattern = Pattern.compile("({[^}]*})+?|;([^" + newline + "]*)");
        var commentMatcher = commentPattern.matcher(comment);
        var sb = new StringBuilder();

        while (commentMatcher.find()) {
            if (commentMatcher.group(1) != null) {
                // Bracketed comment
                commentMatcher.appendReplacement(sb, EncodingUtils.encodeComment(commentMatcher.group(1), newline));
            }
            else if (commentMatcher.group(2) != null) {
                // Semicolon comment
                commentMatcher.appendReplacement(sb, " " + EncodingUtils.encodeComment("{" + commentMatcher.group(2).substring(1) + "}", newline));
            }
        }

        commentMatcher.appendTail(sb);
        return sb.toString();
    }

    /*
     * Convert a move from 0x88 coordinates to Standard Algebraic Notation (SAN)
     */
    private String moveToSan(InternalMove move, List<InternalMove> moves) {
        var output = new StringBuilder();

        if ((move.getFlags() & Bits.KSIDE_CASTLE) != 0) {
            output.append("O-O");
        }
        else if ((move.getFlags() & Bits.QSIDE_CASTLE) != 0) {
            output.append("O-O-O");
        }
        else {
            if (move.getPiece() != PieceTypes.PAWN) {
                var disambiguator = getDisambiguator(move, moves);
                output.append(Character.toUpperCase(move.getPiece())).append(disambiguator);
            }

            if ((move.getFlags() & (Bits.CAPTURE | Bits.EP_CAPTURE)) != 0) {
                if (move.getPiece() == PieceTypes.PAWN) {
                    output.append(algebraic(move.getFrom()).charAt(0));
                }
                output.append("x");
            }

            output.append(algebraic(move.getTo()));

            if (move.getPromotion() != null) {
                output.append("=").append(Character.toUpperCase(move.getPromotion()));
            }
        }

        makeMove(move);
        if (this.isCheck()) {
            if (this.isCheckmate()) {
                output.append("#");
            }
            else {
                output.append("+");
            }
        }
        undoMove();

        return output.toString();
    }

    /**
     * Converts a move from Standard Algebraic Notation (SAN) to 0x88 coordinates
     * @param move SAN move
     * @param strict strict parser
     * @return 0x88 move
     */
    private InternalMove moveFromSan(String move, boolean strict) {
        // strip off any move decorations: e.g. Nf3+?! becomes Nf3
        var cleanMove = strippedSan(move);
        var pieceType = inferPieceType(cleanMove);
        var moves = generateMovesInternal(new MovesOptions(true, pieceType));

        // strict parser
        for (var moveItem : moves) {
            if (cleanMove.equals(strippedSan(moveToSan(moveItem, moves)))) {
                return moveItem;
            }
        }

        // the strict parser failed
        if (strict) {
            return null;
        }

        String from = null;
        String to = null;
        Character piece = null;
        Character promotion = null;

        /*
         * The default permissive (non-strict) parser allows the user to parse
         * non-standard chess notations. This parser is only run after the strict
         * Standard Algebraic Notation (SAN) parser has failed.
         *
         * When running the permissive parser, we'll run a regex to grab the piece, the
         * to/from square, and an optional promotion piece. This regex will
         * parse common non-standard notation like: Pe2-e4, Rc1c4, Qf3xf7,
         * f7f8q, b1c3
         *
         * NOTE: Some positions and moves may be ambiguous when using the permissive
         * parser. For example, in this position: 6k1/8/8/B7/8/8/8/BN4K1 w - - 0 1,
         * the move b1c3 may be interpreted as Nc3 or B1c3 (a disambiguated bishop
         * move). In these cases, the permissive parser will default to the most
         * basic interpretation (which is b1c3 parsing to Nc3).
         */
        var overlyDisambiguated = false;

        // Pattern format: {piece} {from} {to} {promotion}
        var matcher = Pattern.compile("([pnbrqkPNBRQK])?([a-h][1-8])x?-?([a-h][1-8])([qrbnQRBN])?").matcher(cleanMove);

        if (matcher.matches()) {
            piece = matcher.group(1).charAt(0);
            from = matcher.group(2);
            to = matcher.group(3);
            promotion = matcher.group(4).charAt(0);

            if (from.length() == 1) {
                overlyDisambiguated = true;
            }
        }
        else {
            /*
             * The [a-h]?[1-8]? portion of the regex below handles moves that may be
             * overly disambiguated (e.g. Nge7 is unnecessary and non-standard when
             * there is one legal knight move to e7). In this case, the value of
             * 'from' variable will be a rank or file, not a square.
             */
            matcher = Pattern.compile("([pnbrqkPNBRQK])?([a-h]?[1-8]?)x?-?([a-h][1-8])([qrbnQRBN])?").matcher(cleanMove);

            if (matcher.matches()) {
                piece = matcher.group(1).charAt(0);
                from = matcher.group(2);
                to = matcher.group(3);
                promotion = matcher.group(4).charAt(0);

                if (from.length() == 1) {
                    overlyDisambiguated = true;
                }
            }
        }

        pieceType = inferPieceType(cleanMove);
        moves = generateMovesInternal(new MovesOptions(false, piece != null ? piece : pieceType));

        if (to == null) {
            return null;
        }

        for (var moveItem : moves) {
            if (from == null) {
                // if there is no from square, it could be just 'x' missing from a capture
                if (cleanMove.equals(strippedSan(moveToSan(moveItem, moves)).replace("x", ""))) {
                    return moveItem;
                }
            }
            else if (
                (piece == null || Character.toLowerCase(piece) == moveItem.getPiece()) &&
                Ox88.get(from) == moveItem.getFrom() &&
                Ox88.get(to) == moveItem.getTo() &&
                (promotion == null || Character.toLowerCase(promotion) == moveItem.getPromotion())
            )
            {
                // hand-compare move properties with the results from our permissive regex
                return moveItem;
            }
            else if (overlyDisambiguated) {
                // SPECIAL CASE: we parsed a move string that may have an unneeded
                // rank/file disambiguator (e.g. Nge7).  The 'from' variable will
                var square = algebraic(moveItem.getFrom());
                if (
                    (piece == null || Character.toLowerCase(piece) == moveItem.getPiece()) &&
                    Ox88.get(to) == moveItem.getTo() &&
                    (from.equals(square.substring(0, 1)) || from.equals(square.substring(1))) &&
                    (promotion == null || Character.toLowerCase(promotion) == moveItem.getPromotion())
                )
                {
                    return moveItem;
                }
            }
        }

        return null;
    }

    /**
     * Generates an ASCII representation of the current position in the board
     * @return The string representation of the board.
     */
    public String ascii() {
        var strBuilder = new StringBuilder("   +------------------------+\n");
        for (int i = Ox88.A8; i <= Ox88.H1; i++) {
            if (file(i) == 0) {
                strBuilder.append(' ')
                        .append("87654321".charAt(rank(i)))
                        .append(" |");
            }

            if (board[i] != null) {
                var piece = this.board[i].getType();
                var color = this.board[i].getColor();
                var symbol = color == PieceColors.WHITE ? Character.toUpperCase(piece) : Character.toLowerCase(piece);

                strBuilder.append(' ')
                        .append(symbol)
                        .append(' ');
            }
            else {
                strBuilder.append(" . ");
            }

            if (((i + 1) & 0x88) != 0) {
                strBuilder.append("|\n");
                i += 8;
            }
        }

        strBuilder.append("   +------------------------+\n");
        strBuilder.append("     a  b  c  d  e  f  g  h");

        return strBuilder.toString();
    }

    public int perft(int depth) {
        var moves = generateMovesInternal(new MovesOptions(true));
        var nodes = 0;
        var color = this.turn;

        for (var move : moves) {
            makeMove(move);

            if (!isKingAttacked(color)) {
                if (depth - 1 > 0) {
                    nodes += perft(depth - 1);
                }
                else {
                    nodes++;
                }
            }

            undoMove();
        }

        return nodes;
    }

    public String[] historyAsStrings() {
        return historyGeneric(move -> moveToSan(move, generateMovesInternal(new MovesOptions())), new String[0]);
    }

    public Move[] history() {
        return historyGeneric(this::makePretty, new Move[0]);
    }

    private <T> T[] historyGeneric(Function<InternalMove, T> processor, T[] arrayType) {
        var reversedHistory = new Stack<InternalMove>();
        var movesHistory = new ArrayList<T>();

        while (!history.isEmpty()) {
            reversedHistory.push(undoMove());
        }

        while (!reversedHistory.isEmpty()) {
            var move = reversedHistory.pop();
            movesHistory.add(processor.apply(move));
            makeMove(move);
        }

        return movesHistory.toArray(arrayType);
    }

    public String getComment() {
        return comments.get(fen());
    }

    public void setComment(String comment) {
        comments.put(fen(), comment.replace('{', '[').replace('}', ']'));
    }

    public String deleteComment() {
        var comment = comments.get(fen());
        comments.remove(fen());
        return comment;
    }

    public FenComment[] getComments() {
        pruneComments();

        return comments.keySet()
                .stream()
                .map(fen -> new FenComment(fen, comments.get(fen)))
                .toArray(FenComment[]::new);
    }

    public FenComment[] deleteComments() {
        var commentsArray = getComments();
        comments.clear();
        return commentsArray;
    }

    private void pruneComments() {
        var reversedHistory = new Stack<InternalMove>();
        var currentComments = new HashMap<String, String>();

        while (!history.isEmpty()) {
            reversedHistory.push(undoMove());
        }

        if (comments.containsKey(fen())) {
            currentComments.put(fen(), comments.get(fen()));
        }

        while (!reversedHistory.isEmpty()) {
            var move = reversedHistory.pop();
            makeMove(move);

            if (comments.containsKey(fen())) {
                currentComments.put(fen(), comments.get(fen()));
            }
        }

        comments = currentComments;
    }

    private String appendComment(String moveString) {
        if (comments.containsKey(moveString)) {
            var delimiter = !moveString.isEmpty() ? " " : "";
            moveString = moveString + delimiter + "{" + comments.get(moveString) + "}";
        }

        return moveString;
    }

    private int wrapComment(int width, String move, List<String> result, String newline, int maxWidth) {
        for (var token : move.split(" ")) {
            if (token.isEmpty()) {
                continue;
            }

            if (width + token.length() > maxWidth) {
                while (!result.isEmpty() && result.getLast().equals(" ")) {
                    result.removeLast();
                    width--;
                }
                result.add(newline);
                width = 0;
            }

            result.add(token);
            width += token.length();
            result.add(" ");
            width++;
        }

        if (!result.isEmpty() && result.getLast().equals(" ")) {
            result.removeLast();
            width--;
        }

        return width;
    }

    public Map<String, String> header(String... args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i] != null && args[i + 1] != null) {
                headers.put(args[i], args[i + 1]);
            }
        }
        return headers;
    }

    private InternalMove undoMove() {
        if (history.isEmpty()) {
            return null;
        }

        var old = history.removeLast();
        var move = old.getMove();

        kings = old.getKings();
        turn = old.getTurn();
        castling = old.getCastling();
        epSquare = old.getEpSquare();
        halfMoves = old.getHalfMoves();
        moveNumber = old.getMoveNumber();

        var us = turn;
        var them = swapColor(us);

        board[move.getFrom()] = board[move.getTo()];
        board[move.getFrom()].setType(move.getPiece()); // to undo any promotions
        board[move.getTo()] = null;

        if (move.getCaptured() != null) {
            if ((move.getFlags() & Bits.EP_CAPTURE) != 0) {
                // en passant capture
                int index;
                if (us == PieceColors.BLACK) {
                    index = move.getTo() - 16;
                } else {
                    index = move.getTo() + 16;
                }
                board[index] = new Piece(PieceTypes.PAWN, them);
            }
            else {
                // regular capture
                board[move.getTo()] = new Piece(move.getCaptured(), them);
            }
        }

        if ((move.getFlags() & (Bits.KSIDE_CASTLE | Bits.QSIDE_CASTLE)) != 0) {
            int castlingTo, castlingFrom;
            if ((move.getFlags() & Bits.KSIDE_CASTLE) != 0) {
                castlingTo = move.getTo() + 1;
                castlingFrom = move.getTo() - 1;
            }
            else {
                castlingTo = move.getTo() - 2;
                castlingFrom = move.getTo() + 1;
            }

            board[castlingTo] = board[castlingFrom];
            board[castlingFrom] = null;
        }

        return move;
    }

    private void addToHistory(InternalMove move) {
        var historyEntry = new History(
            move,
            new HashMap<>(kings),
            turn,
            new HashMap<>(castling),
            epSquare,
            halfMoves,
            moveNumber
        );
        history.add(historyEntry);
    }

    public boolean setCastlingRights(char color, Map<Character, Boolean> rights) {
        var sides = new char[] {PieceTypes.KING, PieceTypes.QUEEN};

        for (var side : sides) {
            if (rights.containsKey(side)) {
                if (rights.get(side)) {
                    castling.put(color, castling.get(color) | Bits.getCastlingSideBit(side));
                }
                else {
                    castling.put(color, castling.get(color) & ~Bits.getCastlingSideBit(side));
                }
            }
        }

        updateCastlingRights();
        var result = getCastlingRights(color);

        return (rights.get(PieceTypes.KING) == null || rights.get(PieceTypes.KING) == result.get(PieceTypes.KING)) &&
                (rights.get(PieceTypes.QUEEN) == null || rights.get(PieceTypes.QUEEN) == result.get(PieceTypes.QUEEN));
    }

    public Map<Character, Boolean> getCastlingRights(char color) {
        var result = new HashMap<Character, Boolean>();
        result.put(PieceTypes.KING, (castling.get(color) & Bits.KSIDE_CASTLE) != 0);
        result.put(PieceTypes.QUEEN, (castling.get(color) & Bits.QSIDE_CASTLE) != 0);
        return result;
    }

    private void setCastlingBit(char color, int bitmask) {
        if (castling.containsKey(color)) {
            castling.put(color, castling.get(color) | bitmask);
        }
    }

    private void updateCastlingRights() {
        var whiteKingInPlace = board[Ox88.E1] != null &&
                board[Ox88.E1].getType() == PieceTypes.KING &&
                board[Ox88.E1].getColor() == PieceColors.WHITE;

        var blackKingInPlace = board[Ox88.E8] != null &&
                board[Ox88.E8].getType() == PieceTypes.KING &&
                board[Ox88.E8].getColor() == PieceColors.BLACK;

        if (
            !whiteKingInPlace ||
            board[Ox88.A1] == null ||
            board[Ox88.A1].getType() != PieceTypes.ROOK ||
            board[Ox88.A1].getColor() != PieceColors.WHITE
        )
        {
            castling.put(PieceColors.WHITE, castling.get(PieceColors.WHITE) & ~Bits.QSIDE_CASTLE);
        }

        if (
            !whiteKingInPlace ||
            board[Ox88.H1] == null ||
            board[Ox88.H1].getType() != PieceTypes.ROOK ||
            board[Ox88.H1].getColor() != PieceColors.WHITE
        )
        {
            castling.put(PieceColors.WHITE, castling.get(PieceColors.WHITE) & ~Bits.KSIDE_CASTLE);
        }

        if (
            !blackKingInPlace ||
            board[Ox88.A8] == null ||
            board[Ox88.A8].getType() != PieceTypes.ROOK ||
            board[Ox88.A8].getColor() != PieceColors.BLACK
        )
        {
            castling.put(PieceColors.BLACK, castling.get(PieceColors.BLACK) & ~Bits.QSIDE_CASTLE);
        }

        if (
            !blackKingInPlace ||
            board[Ox88.H8] == null ||
            board[Ox88.H8].getType() != PieceTypes.ROOK ||
            board[Ox88.H8].getColor() != PieceColors.BLACK
        )
        {
            castling.put(PieceColors.BLACK, castling.get(PieceColors.BLACK) & ~Bits.KSIDE_CASTLE);
        }
    }

    private void updateEnPassantSquare() {
        if (epSquare == EMPTY) {
            return;
        }

        var startSquare = epSquare + (turn == PieceColors.WHITE ? -16 : 16);
        var currentSquare = epSquare + (turn == PieceColors.WHITE ? 16 : -16);
        int[] attackers = {currentSquare + 1, currentSquare - 1};

        if (
            board[startSquare] != null ||
            board[epSquare] != null ||
            board[currentSquare] == null ||
            board[currentSquare].getColor() != swapColor(turn) ||
            board[currentSquare].getType() != PieceTypes.PAWN
        )
        {
            epSquare = EMPTY;
            return;
        }

        IntPredicate canCapture = square -> (square & 0x88) == 0 &&
                board[square] != null &&
                board[square].getColor() == turn &&
                board[square].getType() == PieceTypes.PAWN;

        if (Arrays.stream(attackers).noneMatch(canCapture)) {
            epSquare = EMPTY;
        }
    }

    /*
     * Called when the initial board setup is changed with put() or remove().
     * modifies the SetUp and FEN properties of the header object. If the FEN
     * is equal to the default position, the SetUp and FEN are deleted the setup
     * is only updated if history.length is zero, ie moves haven't been made.
     */
    private void updateSetup(String fen) {
        if (!history.isEmpty()) {
            return;
        }

        if (!fen.equals(DEFAULT_POSITION)) {
            this.headers.put("SetUp", "1");
            this.headers.put("FEN", fen);
        }
        else {
            this.headers.remove("SetUp");
            this.headers.remove("FEN");
        }
    }

    private Move makePretty(InternalMove uglyMove) {
        var color = uglyMove.getColor();
        var piece = uglyMove.getPiece();
        var from = uglyMove.getFrom();
        var to = uglyMove.getTo();
        var flags = uglyMove.getFlags();
        var captured = uglyMove.getCaptured();
        var promotion = uglyMove.getPromotion();
        var prettyFlags = new StringBuilder();

        for (var bit : Bits.getBits()) {
            if ((bit & flags) != 0) {
                prettyFlags.append(Bits.getBitFlag(bit));
            }
        }

        var fromAlgebraic = algebraic(from);
        var toAlgebraic = algebraic(to);

        var move = new Move(color, fromAlgebraic, toAlgebraic, piece, prettyFlags.toString());
        move.setSan(moveToSan(uglyMove, generateMovesInternal(new MovesOptions(true))));
        move.setLan(fromAlgebraic + toAlgebraic);
        move.setBefore(fen());
        move.setAfter("");

        // generate the FEN for the 'after' key
        makeMove(uglyMove);
        move.setAfter(fen());
        undoMove();

        if (captured != null) {
            move.setCaptured(captured);
        }
        if (promotion != null) {
            move.setPromotion(promotion);
            move.setLan(move.getLan() + promotion);
        }

        return move;
    }

    /**
     * Keeps track of position occurrence counts for the purpose of repetition
     * checking. All three methods (`_inc`, `_dec`, and `_get`) trim the
     * irrelevant information from the fen, initialising new positions, and
     * removing old positions from the record if their counts are reduced to 0.
     */
    private int getPositionCount(String fen) {
        var trimmedFen = trimFen(fen);
        return positionCount.getOrDefault(trimmedFen, 0);
    }

    private void incPositionCount(String fen) {
        var trimmedFen = trimFen(fen);

        if (positionCount.containsKey(trimmedFen)) {
            positionCount.put(trimmedFen, positionCount.get(trimmedFen) + 1);
        }
        else {
            positionCount.put(trimmedFen, 0);
        }
    }

    private void decPositionCount(String fen) {
        var trimmedFen = trimFen(fen);
        if (!positionCount.containsKey(trimmedFen)) {
            return;
        }

        if (positionCount.get(trimmedFen) == 1) {
            positionCount.remove(trimmedFen);
        }
        else {
            positionCount.put(trimmedFen, positionCount.get(trimmedFen) - 1);
        }
    }

    /**
     * Swaps the color of a piece.
     * @param color piece color
     * @return swapped color
     */
    private static char swapColor(char color) {
        return color == PieceColors.WHITE ? PieceColors.BLACK : PieceColors.WHITE;
    }

    /**
     * Extracts the zero-based rank of a 0x88 square.
     * @param square 0x88 square
     * @return rank
     */
    private static int rank(int square) {
        return square >> 4;
    }

    /**
     * Extracts the zero-based file of a 0x88 square.
     * @param square 0x88 square
     * @return file
     */
    private static int file(int square) {
        return square & 0xf;
    }

    /**
     * Converts a 0x88 square to algebraic notation.
     * @param square 0x88 square
     * @return algebraic notation
     */
    private static String algebraic(int square) {
        var file = file(square);
        var rank = rank(square);
        return "abcdefgh".substring(file, file + 1) + "87654321".substring(rank, rank + 1);
    }

    /**
     * This function is used to uniquely identify ambiguous moves.
     * @param move move
     * @param moves list of moves
     * @return ambiguous move
     */
    private static String getDisambiguator(InternalMove move, List<InternalMove> moves) {
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
            }
            else if (sameFile > 0) {
                /*
                 * if the moving piece rests on the same file, use the rank symbol as the
                 * disambiguator
                 */
                return String.valueOf(algebraic(from).charAt(1));
            }
            else {
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
     * @param captured captured piece, can be null
     * @param flags move flags
     */
    private static void addMove(
        List<InternalMove> moves,
        char color,
        int from,
        int to,
        char piece,
        Character captured,
        int flags
    )
    {
        var rank = rank(to);

        if (piece == PieceTypes.PAWN && (rank == Ranks.RANK_1 || rank == Ranks.RANK_8)) {
            for (var promotion : ChessConstants.PROMOTIONS) {
                moves.add(new InternalMove(color, from, to, piece, captured, promotion, flags | Bits.PROMOTION));
            }
        }
        else {
            moves.add(new InternalMove(color, from, to, piece, captured, flags));
        }
    }

    private static Character inferPieceType(String san) {
        var pieceType = san.charAt(0);

        if (pieceType >= 'a' && pieceType <= 'h') {
            if (Pattern.matches("[a-h]\\d.*[a-h]\\d", san)) {
                return null;
            }
            return PieceTypes.PAWN;
        }

        pieceType = Character.toLowerCase(pieceType);

        if (pieceType == 'o') {
            return PieceTypes.KING;
        }
        return pieceType;
    }

    /**
     * Parses all the decorators out of a SAN string
     * @param move SAN
     * @return stripped SAN
     */
    private static String strippedSan(String move) {
        return move.replace("=", "").replaceAll("[+#]?[?!]*$", "");
    }

    /**
     * remove last two fields in FEN string as they're not needed when checking
     * for repetition
     * @param fen FEN
     * @return trimmed FEN
     */
    private static String trimFen(String fen) {
        var parts = fen.split(" ");
        return Arrays.stream(parts).limit(4).collect(Collectors.joining(" "));
    }
}
