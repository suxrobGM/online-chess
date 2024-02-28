package com.sisofttech.onlinechess.engine;

import com.sisofttech.onlinechess.engine.constants.*;
import com.sisofttech.onlinechess.engine.options.MoveOptions;
import com.sisofttech.onlinechess.engine.options.MovesOptions;
import com.sisofttech.onlinechess.engine.utils.ArrayUtils;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sisofttech.onlinechess.engine.constants.CastlingSides.ROOKS;

public class Chess {
    public static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final int EMPTY = -1;
    private final Piece[] board = new Piece[128];
    private final Map<String, String> headers = new HashMap<>();
    private Map<Character, Integer> kings = new HashMap<>();
    private Map<Character, Integer> castling = new HashMap<>();
    private final Map<String, String> comments = new HashMap<>();

    /**
     * Tracks number of times a position has been seen for repetition checking
     */
    private final Map<String, Integer> positionCount = new HashMap<>();
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

    private boolean isKingAttacked(char color) {
        var square = this.kings.get(color);
        return square != -1 && this.isAttacked(swapColor(color), square);
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
        } else if (numPieces == 3 && (pieces.get(PieceTypes.BISHOP) == 1 || pieces.get(PieceTypes.KNIGHT) == 1)) {
            // k vs. kn .... or .... k vs. kb
            return true;
        } else if (numPieces == pieces.get(PieceTypes.BISHOP) + 2) {
            // kb vs. kb where any number of bishops are all on the same color
            var sum = 0;
            for (var bishop : bishops) {
                sum += bishop;
            }
            return sum == 0 || sum == bishops.size();
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
            return moves.stream().map(move -> moveToSan(move, moves)).collect(Collectors.toList());
        }
    }

    private List<InternalMove> movesInternal(MovesOptions options) {
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
                    addMove(moves, us, from, to, PieceTypes.PAWN, null, 0

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
                for (var j = 0; j < PawnOffsets.get(type).length; j++) {
                    var offset = PawnOffsets.get(type)[j];
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
            for (var i = 0; i < ROOKS.get(us).length; i++) {
                if (
                    move.getFrom() == ROOKS.get(us)[i].square() &&
                    (castling.get(us) & ROOKS.get(us)[i].flag()) != 0
                )
                {
                    castling.put(us, castling.get(us) ^ ROOKS.get(us)[i].flag());
                    break;
                }
            }
        }

        // turn off castling if we capture a rook
        if (castling.get(them) != 0) {
            for (var i = 0; i < ROOKS.get(them).length; i++) {
                if (
                    move.getTo() == ROOKS.get(them)[i].square() &&
                    (castling.get(them) & ROOKS.get(them)[i].flag()) != 0
                )
                {
                    castling.put(them, castling.get(them) ^ ROOKS.get(them)[i].flag());
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
            moveObj = moveFromSan(options.san, options.strict);
        }
        else if (options.from.isPresent() && options.to.isPresent()) {
            var moves = this.movesInternal(new MovesOptions());

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

    public Move undo() {
        var move = undoMove();
        if (move != null) {
            var prettyMove = makePretty(move);
            decPositionCount(prettyMove.getAfter());
            return prettyMove;
        }
        return null;
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
                prettyFlags.append(Bits.getBitSymbol(bit));
            }
        }

        var fromAlgebraic = algebraic(from);
        var toAlgebraic = algebraic(to);
        var movesOptions = new MovesOptions();
        movesOptions.legal = true;

        var move = new Move(color, fromAlgebraic, toAlgebraic, piece, prettyFlags.toString());
        move.setSan(moveToSan(uglyMove, moves(movesOptions)));
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

    private void incPositionCount(String fen) {
        var trimmedFen = trimFen(fen);

        if (positionCount.containsKey(trimmedFen)) {
            positionCount.put(trimmedFen, positionCount.get(trimmedFen) + 1);
        }
        else {
            positionCount.put(trimmedFen, 0);
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
        } else {
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
