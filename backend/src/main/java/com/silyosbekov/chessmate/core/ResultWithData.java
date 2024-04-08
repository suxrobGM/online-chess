package com.silyosbekov.chessmate.core;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResultWithData<T> implements IResult {
    @JsonInclude
    private final boolean isSuccess;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final String error;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final T data;

    public ResultWithData() {
        this.isSuccess = true;
        this.error = null;
        this.data = null;
    }

    public ResultWithData(T data, String error) {
        this.isSuccess = error == null || error.isEmpty();
        this.data = data;
        this.error = error;
    }

    public ResultWithData(T data) {
        this.isSuccess = true;
        this.error = null;
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    @Override
    public String getError() {
        return this.error;
    }

    public T getData() {
        return data;
    }

    public static <T> ResultWithData<T> success(T data) {
        return new ResultWithData<>(data);
    }

    public static <T> ResultWithData<T> fail(String error) {
        return new ResultWithData<T>(null, error);
    }
}
