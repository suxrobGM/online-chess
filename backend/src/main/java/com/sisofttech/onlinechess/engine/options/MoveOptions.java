package com.sisofttech.onlinechess.engine.options;

import java.util.Optional;

public class MoveOptions
{
    public Optional<String> from;
    public Optional<String> to;
    public Optional<Character> san;
    public Optional<Character> promotion;
    public boolean strict;
}
