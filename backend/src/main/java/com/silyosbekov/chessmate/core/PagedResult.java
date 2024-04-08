package com.silyosbekov.chessmate.core;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class PagedResult<T> implements IResult {
    @JsonInclude
    private final boolean isSuccess;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final String error;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final List<T> data;

    @JsonInclude
    private final int totalItems;

    @JsonInclude
    private final int totalPages;

    public PagedResult() {
        this(null, 0, 0, null);
    }

    public PagedResult(List<T> data, int totalItems, int pageSize, String error) {
        this.isSuccess = error == null || error.isEmpty();
        this.error = error;
        this.data = data;
        this.totalItems = totalItems;
        this.totalPages = (int)Math.ceil((double) totalItems / pageSize);
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    @Override
    public String getError() {
        return error;
    }

    public List<T> getData() {
        return data;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public static <T> PagedResult<T> success(List<T> items, int totalItems, int totalPages) {
        return new PagedResult<>(items, totalItems, totalPages, null);
    }

    public static <T> PagedResult<T> fail(String error) {
        return new PagedResult<>(null, 0, 0, error);
    }
}
