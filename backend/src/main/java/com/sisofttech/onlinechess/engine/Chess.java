package com.sisofttech.onlinechess.engine;

import com.sisofttech.onlinechess.engine.constants.*;
import com.sisofttech.onlinechess.engine.options.MovesOptions;
import com.sisofttech.onlinechess.engine.utils.ArrayUtils;
import com.sisofttech.onlinechess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class Chess {
    public static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final int EMPTY = -1;
    private final Piece[] board = new Piece[128];
    private final HashMap<String, String> headers = new HashMap<>();
    private final HashMap<Character, Integer> kings = new HashMap<>();
    private final HashMap<Character, Integer> castling = new HashMap<>();
    private final HashMap<String, String> comments = new HashMap<>();

    /**
     * Tracks number of times a position has been seen for repetition checking
     */
    private final HashMap<String, Integer> positionCount = new HashMap<>();
    private final List<History> history = new ArrayList<>();
    private char turn = PieceColors.WHITE;
    private int epSquare = EMPTY;
    private int halfMoves = 0;
    private int moveNumber = 1;

    public Chess(String fen) {
        this.load(fen, false, false);
        kings.put(PieceColors.WHITE, EMPTY);
        kings.put(PieceColors.BLACK, EMPTY);
        castling.put(PieceColors.WHITE, 0);
        castling.put(PieceColors.BLACK, 0);
    }

    public void clear() {
        clear(false);
    }

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

    public void removeHeader(String key) {
        headers.remove(key);
    }

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
                this.put(new Piece(Character.toLowerCase(piece), color), MoveUtils.algebraic(square), false);
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
                    board[square].getType() == PieceSymbols.PAWN
                )
                {
                    // if the pawn makes an ep capture, does it leave it's king in check?
                    var move = new InternalMove(
                        color,
                        square,
                        this.epSquare,
                        PieceSymbols.PAWN,
                        PieceSymbols.PAWN,
                        PieceSymbols.EMPTY,
                        Bits.EP_CAPTURE
                    );

                    makeMove(move);
                    var isLegal = !isKingAttacked(color);
                    undoMove();

                    // if ep is legal, break and set the ep square in the FEN output
                    if (isLegal) {
                        epSquare = MoveUtils.algebraic(this.epSquare);
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

    public void reset() {
        load(DEFAULT_POSITION, false, false);
    }

    public Piece get(String square) {
        var sq = Ox88.get(square);
        return sq == -1 ? null : board[sq];
    }

    public boolean put(Piece piece, String square) {
        return put(piece, square, true);
    }

    private boolean put(Piece piece, String square, boolean updateSetup) {
        // check for piece
        if (PieceSymbols.SYMBOLS.indexOf(Character.toLowerCase(piece.getType())) == -1) {
            return false;
        }

        // check for valid square
        if (Ox88.get(square) == -1) {
            return false;
        }

        var sq = Ox88.get(square);

        // don't let the user place more than one king
        if (
                piece.getType() == PieceSymbols.KING &&
                        !(this.kings.get(piece.getColor()) == EMPTY || this.kings.get(piece.getColor()) == sq)
        )
        {
            return false;
        }

        var currentPieceOnSquare = this.board[sq];

        // if one of the kings will be replaced by the piece from args, set the `kings` respective entry to `EMPTY`
        if (currentPieceOnSquare != null && currentPieceOnSquare.getType() == PieceSymbols.KING) {
            this.kings.put(currentPieceOnSquare.getColor(), EMPTY);
        }

        this.board[sq] = piece;

        if (piece.getType() == PieceSymbols.KING) {
            this.kings.put(piece.getColor(), sq);
        }

        if (updateSetup) {
            updateCastlingRights();
            updateEnPassantSquare();
            updateSetup(fen());
        }

        return true;
    }

    public Piece remove(String square) {
        var piece = get(square);
        board[Ox88.get(square)] = null;

        if (piece != null && piece.getType() == PieceSymbols.KING) {
            kings.put(piece.getColor(), EMPTY);
        }

        updateCastlingRights();
        updateEnPassantSquare();
        updateSetup(fen());
        return piece;
    }

    public boolean isCheck() {
        return isKingAttacked(turn);
    }

    public boolean isCheckmate() {
        return isCheck() && moves().isEmpty();
    }

    public boolean isStalemate() {
        return !isCheck() && moves().isEmpty();
    }

    public boolean isInsufficientMaterial() {
        /*
         * k.b. vs k.b. (of opposite colors) with mate in 1:
         * 8/8/8/8/1b6/8/B1k5/K7 b - - 0 1
         *
         * k.b. vs k.n. with mate in 1:
         * 8/8/8/8/1n6/8/B7/K1k5 b - - 2 1
         */
        var pieces = new HashMap<Character,Integer>();
        pieces.put(PieceSymbols.BISHOP, 0);
        pieces.put(PieceSymbols.KNIGHT, 0);
        pieces.put(PieceSymbols.ROOK, 0);
        pieces.put(PieceSymbols.QUEEN, 0);
        pieces.put(PieceSymbols.KING, 0);
        pieces.put(PieceSymbols.PAWN, 0);

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
                if (piece.getType() == PieceSymbols.BISHOP) {
                    bishops.add(squareColor);
                }
                numPieces++;
            }
        }

        // k vs. k
        if (numPieces == 2) {
            return true;
        } else if (numPieces == 3 && (pieces.get(PieceSymbols.BISHOP) == 1 || pieces.get(PieceSymbols.KNIGHT) == 1)) {
            // k vs. kn .... or .... k vs. kb
            return true;
        } else if (numPieces == pieces.get(PieceSymbols.BISHOP) + 2) {
            // kb vs. kb where any number of bishops are all on the same color
            var sum = 0;
            for (var bishop : bishops) {
                sum += bishop;
            }
            if (sum == 0 || sum == bishops.size()) {
                return true;
            }
        }

        return false;
    }

    public boolean isThreefoldRepetition() {
        return positionCount.get(fen()) >= 3;
    }

    public boolean isDraw() {
        return halfMoves >= 100 || // 50 moves per side = 100 half moves
                isStalemate() ||
                isInsufficientMaterial() ||
                isThreefoldRepetition();
    }

    public boolean isGameOver() {
        return isCheckmate() || isStalemate() || isDraw();
    }

    public List<String> moves() {
        return this.moves(new MovesOptions());
    }

    public List<String> moves(MovesOptions options) {
        List<InternalMove> moves = this.movesInternal(options);

        if (options.verbose) {
            return moves.stream().map(makePretty).collect(Collectors.toList());
        } else {
            return moves.stream().map(move -> this.moveToSan(move, moves)).collect(Collectors.toList());
        }
    }

    private List<InternalMove> movesInternal(MovesOptions options) {
        var forSquare = options.square.map(String::toLowerCase).orElse(null);
        var forPiece = options.piece.orElse(null);

        var moves = new ArrayList<InternalMove>();
        var us = this.turn;
        var them = MoveUtils.swapColor(us);

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
            if (type == PieceSymbols.PAWN) {
                // single square, non-capturing
                to = from + PawnOffsets.get(us)[0];
                if (board[to] == null) {
                    addMove(moves, us, from, to, PieceSymbols.PAWN);

                    // double square
                    to = from + PawnOffsets.get(us)[1];
                    if (Ranks.getSecondRank(us) == MoveUtils.rank(from) && board[to] == null) {
                        addMove(moves, us, from, to, PieceSymbols.PAWN, null, Bits.BIG_PAWN);
                    }
                }

                // pawn captures
                for (int j = 2; j < 4; j++) {
                    to = from + PawnOffsets.get(us)[j];

                    if ((to & 0x88) != 0) {
                        continue;
                    }

                    if (board[to] != null && board[to].getColor() == them) {
                        addMove(moves, us, from, to, PieceSymbols.PAWN, board[to].getType(), Bits.CAPTURE);
                    } else if (to == epSquare) {
                        addMove(moves, us, from, to, PieceSymbols.PAWN, PieceSymbols.PAWN, Bits.EP_CAPTURE);
                    }
                }
            } else {
                for (int j = 0, len = PawnOffsets.get(type).length; j < len; j++) {
                    int offset = PawnOffsets.get(type)[j];
                    to = from;

                    while (true) {
                        to += offset;
                        if ((to & 0x88) != 0) {
                            break;
                        }

                        if (board[to] == null) {
                            addMove(moves, us, from, to, type);
                        } else {
                            // own color, stop loop
                            if (board[to].getColor() == us) {
                                break;
                            }

                            addMove(moves, us, from, to, type, board[to].getType(), Bits.CAPTURE);
                            break;
                        }

                        /* break, if knight or king */
                        if (type == PieceSymbols.KNIGHT || type == PieceSymbols.KING) {
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
        if (forPiece == null || forPiece == PieceSymbols.KING) {
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
                        addMove(moves, us, kings.get(us), castlingTo, PieceSymbols.KING, null, Bits.KSIDE_CASTLE);
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
                        addMove(moves, us, kings.get(us), castlingTo, PieceSymbols.KING, null, Bits.QSIDE_CASTLE);
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

    public boolean isAttacked(char attackedBy, String square) {
        var sq = Ox88.get(square);
        return isAttacked(attackedBy, sq);
    }

    private boolean isAttacked(char color, int square) {
        for (int i = Ox88.A8; i <= Ox88.H1; i++) {
            // did we run off the end of the board
            if ((i & 0x88) != 0) {
                i += 7;
                continue;
            }

            // if empty square or wrong color
            if (board[i] == null || board[i].getColor() != color) {
                continue;
            }

            var piece = board[i];
            var difference = i - square;

            // skip - to/from square are the same
            if (difference == 0) {
                continue;
            }

            int index = difference + 119;

            if ((ChessConstants.ATTACKS[index] & PieceMasks.get(piece.getType())) != 0) {
                if (piece.getType() == PieceSymbols.PAWN) {
                    if (difference > 0 && piece.getColor() == PieceColors.WHITE) {
                        return true;
                    }

                    if (difference < 0 && piece.getColor() == PieceColors.BLACK) {
                        return true;
                    }

                    continue;
                }

                // if the piece is a knight or a king
                if (piece.getType() == PieceSymbols.KNIGHT || piece.getType() == PieceSymbols.KING) {
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

    private boolean isKingAttacked(char color) {
        var square = this.kings.get(color);
        return square != -1 && this.isAttacked(MoveUtils.swapColor(color), square);
    }

    private void setCastlingBit(char color, int bitmask) {
        if (castling.containsKey(color)) {
            castling.put(color, castling.get(color) | bitmask);
        }
    }

    private void updateCastlingRights() {
        var whiteKingInPlace = board[Ox88.E1] != null &&
                board[Ox88.E1].getType() == PieceSymbols.KING &&
                board[Ox88.E1].getColor() == PieceColors.WHITE;

        var blackKingInPlace = board[Ox88.E8] != null &&
                board[Ox88.E8].getType() == PieceSymbols.KING &&
                board[Ox88.E8].getColor() == PieceColors.BLACK;

        if (
            !whiteKingInPlace ||
            board[Ox88.A1] == null ||
            board[Ox88.A1].getType() != PieceSymbols.ROOK ||
            board[Ox88.A1].getColor() != PieceColors.WHITE
        )
        {
            castling.put(PieceColors.WHITE, castling.get(PieceColors.WHITE) & ~Bits.QSIDE_CASTLE);
        }

        if (
            !whiteKingInPlace ||
            board[Ox88.H1] == null ||
            board[Ox88.H1].getType() != PieceSymbols.ROOK ||
            board[Ox88.H1].getColor() != PieceColors.WHITE
        )
        {
            castling.put(PieceColors.WHITE, castling.get(PieceColors.WHITE) & ~Bits.KSIDE_CASTLE);
        }

        if (
            !blackKingInPlace ||
            board[Ox88.A8] == null ||
            board[Ox88.A8].getType() != PieceSymbols.ROOK ||
            board[Ox88.A8].getColor() != PieceColors.BLACK
        )
        {
            castling.put(PieceColors.BLACK, castling.get(PieceColors.BLACK) & ~Bits.QSIDE_CASTLE);
        }

        if (
            !blackKingInPlace ||
            board[Ox88.H8] == null ||
            board[Ox88.H8].getType() != PieceSymbols.ROOK ||
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
            board[currentSquare].getColor() != MoveUtils.swapColor(turn) ||
            board[currentSquare].getType() != PieceSymbols.PAWN
        )
        {
            epSquare = EMPTY;
            return;
        }

        IntPredicate canCapture = square -> (square & 0x88) == 0 &&
                board[square] != null &&
                board[square].getColor() == turn &&
                board[square].getType() == PieceSymbols.PAWN;

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
        } else {
            this.headers.remove("SetUp");
            this.headers.remove("FEN");
        }
    }

    private void incPositionCount(String fen) {
        var trimmedFen = MoveUtils.trimFen(fen);

        if (positionCount.containsKey(trimmedFen)) {
            positionCount.put(trimmedFen, positionCount.get(trimmedFen) + 1);
        } else {
            positionCount.put(trimmedFen, 0);
        }
    }
}
