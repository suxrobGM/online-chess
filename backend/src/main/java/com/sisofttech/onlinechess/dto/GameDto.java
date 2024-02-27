package com.sisofttech.onlinechess.dto;

public class GameDto {
    private String id;
    private String whitePlayerId;
    private String blackPlayerId;
    private String winnerPlayerId;
    private String status;
    private String turnPlayerId;
    private String check;
    private String checkmate;
    private String stalemate;
    private String draw;
    private String resign;
    private String moveCount;
    private String createdAt;
    private String updatedAt;

    public GameDto() {
    }

    public GameDto(String id, String whitePlayerId, String blackPlayerId, String winnerPlayerId, String status, String turnPlayerId, String check, String checkmate, String stalemate, String draw, String resign, String moveCount, String createdAt, String updatedAt) {
        this.id = id;
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.winnerPlayerId = winnerPlayerId;
        this.status = status;
        this.turnPlayerId = turnPlayerId;
        this.check = check;
        this.checkmate = checkmate;
        this.stalemate = stalemate;
        this.draw = draw;
        this.resign = resign;
        this.moveCount = moveCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getWhitePlayerId() {
        return whitePlayerId;
    }

    public String getBlackPlayerId() {
        return blackPlayerId;
    }

    public String getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public String getStatus() {
        return status;
    }

    public String getTurnPlayerId() {
        return turnPlayerId;
    }

    public String getCheck() {
        return check;
    }

    public String getCheckmate() {
        return checkmate;
    }

    public String getStalemate() {
        return stalemate;
    }

    public String getDraw() {
        return draw;
    }

    public String getResign() {
        return resign;
    }

    public String getMoveCount() {
        return moveCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
