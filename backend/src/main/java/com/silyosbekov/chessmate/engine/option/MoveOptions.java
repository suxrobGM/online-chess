package com.silyosbekov.chessmate.engine.option;

import java.util.Optional;

public class MoveOptions
{
    public Optional<String> from;
    public Optional<String> to;
    public Optional<String> san;
    public Optional<Character> promotion;
    public boolean strict;

    public MoveOptions()
    {
        from = Optional.empty();
        to = Optional.empty();
        san = Optional.empty();
        promotion = Optional.empty();
        strict = false;
    }

    public MoveOptions(String from, String to, String san, Character promotion, boolean strict)
    {
        this.from = Optional.ofNullable(from);
        this.to = Optional.ofNullable(to);
        this.san = Optional.ofNullable(san);
        this.promotion = Optional.ofNullable(promotion);
        this.strict = strict;
    }
}
