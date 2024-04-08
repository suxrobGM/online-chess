package com.silyosbekov.chessmate.core;

public record PagedQuery(String orderBy, int page, int pageSize) {
}
