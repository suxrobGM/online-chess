package com.silyosbekov.chessmate.core;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Result implements IResult {
    @JsonInclude
    private final boolean isSuccess;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final String error;

    public Result() {
        this.isSuccess = true;
        this.error = null;
    }

    public Result(String error) {
        this.isSuccess = error == null || error.isEmpty();
        this.error = error;
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    @Override
    public String getError() {
        return error;
    }

    public static Result success() {
        return new Result();
    }

    public static Result fail(String error) {
        return new Result(error);
    }
}
