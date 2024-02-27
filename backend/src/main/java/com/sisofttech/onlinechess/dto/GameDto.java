package com.sisofttech.onlinechess.dto;

public class GameDto {
    private String id;
    private String whitePlayerId;
    private String blackPlayerId;
    private String winnerPlayerId;
    private String status;
    private String turnPlayerId;
//    private boolean isCheck;
//    private boolean isCheckmate;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWhitePlayerId() {
        return whitePlayerId;
    }

    public void setWhitePlayerId(String whitePlayerId) {
        this.whitePlayerId = whitePlayerId;
    }

    public String getBlackPlayerId() {
        return blackPlayerId;
    }

    public void setBlackPlayerId(String blackPlayerId) {
        this.blackPlayerId = blackPlayerId;
    }

    public String getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public void setWinnerPlayerId(String winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public String getTurnPlayerId() {
        return turnPlayerId;
    }

    public void setTurnPlayerId(String turnPlayerId) {
        this.turnPlayerId = turnPlayerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

//    public boolean isCheck() {
//        return isCheck;
//    }
//
//    public void setCheck(boolean check) {
//        isCheck = check;
//    }
//
//    public boolean isCheckmate() {
//        return isCheckmate;
//    }
//
//    public void setCheckmate(boolean checkmate) {
//        isCheckmate = checkmate;
//    }
}
