package com.silyosbekov.onlinechess.dto;

public class CreateGameDto {
    private final String whitePlayerId;
    private final String blackPlayerId;

    public CreateGameDto(String whitePlayerId, String blackPlayerId) {
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
    }

    public String getWhitePlayerId() {
        return whitePlayerId;
    }

    public String getBlackPlayerId() {
        return blackPlayerId;
    }
}
