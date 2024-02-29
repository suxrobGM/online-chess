package com.sisofttech.onlinechess.engine.options;

import java.util.Optional;

public class MovesOptions {
    public boolean verbose;
    public boolean legal;
    public Optional<String> square;
    public Optional<Character> piece;

    public MovesOptions() {
        verbose = false;
        legal = false;
        square = Optional.empty();
        piece = Optional.empty();
    }

    public MovesOptions(boolean verbose, boolean legal) {
        this.verbose = verbose;
        this.legal = legal;
        this.square = Optional.empty();
        this.piece = Optional.empty();
    }

    public MovesOptions(boolean legal) {
        this.verbose = false;
        this.legal = legal;
        this.square = Optional.empty();
        this.piece = Optional.empty();
    }

    public MovesOptions(boolean legal, Character piece) {
        this.verbose = false;
        this.legal = legal;
        this.square = Optional.empty();
        this.piece = Optional.ofNullable(piece);
    }

    public MovesOptions(boolean verbose, boolean legal, Character piece) {
        this.verbose = verbose;
        this.legal = legal;
        this.square = Optional.empty();
        this.piece = Optional.ofNullable(piece);
    }

    public MovesOptions(boolean verbose, boolean legal, String square, Character piece) {
        this.verbose = verbose;
        this.legal = legal;
        this.square = Optional.ofNullable(square);
        this.piece = Optional.ofNullable(piece);
    }
}
