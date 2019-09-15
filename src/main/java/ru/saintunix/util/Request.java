package ru.saintunix.util;

import java.util.ArrayList;
import java.util.Arrays;

public class Request {
    private Function function;
    private boolean status;
    private int playerId;
    private int gameId;

    private String[][] fieldGame;
    private int nextPlayerToGo;
    private int[] step;
    private ArrayList<Integer> freeGame;
    private int winner;
    private int countPlayers;

    public Request(Function fun, boolean status) {
        this.function = fun;
        this.status = status;
    }

    public Request(Function fun, boolean status, int gameId) {
        this.function = fun;
        this.status = status;
        this.gameId = gameId;
    }

    public Request(Function function, boolean status, int playerId, int gameId) {
        this.function = function;
        this.status = status;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public ArrayList<Integer> getFreeGame() {
        return freeGame;
    }

    public void setFreeGame(ArrayList<Integer> freeGame) {
        this.freeGame = freeGame;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String[][] getFieldGame() {
        return fieldGame;
    }

    public void setFieldGame(String[][] fieldGame) {
        this.fieldGame = fieldGame;
    }

    public int getNextPlayerToGo() {
        return nextPlayerToGo;
    }

    public void setNextPlayerToGo(int nextPlayerToGo) {
        this.nextPlayerToGo = nextPlayerToGo;
    }

    public int[] getStep() {
        return step;
    }

    public void setStep(int[] step) {
        this.step = step;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public int getCountPlayers() {
        return countPlayers;
    }

    public void setCountPlayers(int countPlayers) {
        this.countPlayers = countPlayers;
    }

    @Override
    public String toString() {
        return "Request{" +
                "function=" + function +
                ", status=" + status +
                ", playerId=" + playerId +
                ", gameId=" + gameId +
                ", fieldGame=" + Arrays.toString(fieldGame) +
                ", nextPlayerToGo=" + nextPlayerToGo +
                ", step=" + Arrays.toString(step) +
                ", freeGame=" + freeGame +
                ", winner=" + winner +
                ", countPlayers=" + countPlayers +
                '}';
    }
}
