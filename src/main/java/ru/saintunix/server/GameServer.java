package ru.saintunix.server;

import ru.saintunix.exception.*;
import ru.saintunix.util.Function;
import ru.saintunix.util.Request;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameServer {
    private final Random rand = new Random();

    private HashMap<Integer, Game> games = new HashMap<>();

    public Request startNewGame(int playerId) {
        int gameId = rand.nextInt(999999);
        games.put(gameId, new Game());
        playerConnectToGame(playerId, gameId, false);
        return getStatusGame(gameId, playerId);
    }

    public Request playerConnectToGame(int playerId, int gameId, boolean sendMsg) {
        Game game = games.get(gameId);
        try {
            game.addPlayer(playerId);
        } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
            tooManyPlayersExceptions.printStackTrace();
            return sendFailedStatus();
        }

        if (sendMsg) {
            return getStatusGame(gameId, playerId);
        }

        return null;
    }

    public Request playerLeaveTheGame(int playerId, int gameId) {
        try {
            games.get(gameId).removePlayer(playerId);

            if (games.get(gameId).countPlayer() == 0)
                endGame(gameId);

        } catch (PlayersNotFoundExceptions playersNotFoundExceptions) {
            playersNotFoundExceptions.printStackTrace();
            return sendFailedStatus();
        }

        return new Request(Function.RESPONSE, true, gameId);
    }

    public Request listAllGame() {
        ArrayList<Integer> freeGame = new ArrayList<>();
        for (int key : games.keySet()) {
            if (games.get(key).countPlayer() == 1) {
                freeGame.add(key);
            }
        }

        Request request = new Request(Function.RESPONSE, true);
        request.setFreeGame(freeGame);

        return request;
    }

    public Request step(int gameId, int playerId, int[] step) {
        Game game = games.get(gameId);
        try {
            game.step(playerId, step);
        } catch (PlayersNotFoundExceptions | BusyFieldExceptions e) {
            e.printStackTrace();
            return sendFailedStatus();

        } catch (GameAlreadyEndedExceptions gameAlreadyEndedExceptions) {
            return endGame(gameId);

        }

        return getStatusGame(gameId, playerId);
    }

    public Request getStatusGame(int gameId, int playerId) {
        if (!games.containsKey(gameId))
            return sendFailedStatus();

        Game game = games.get(gameId);

        if (!game.getPlayersId().contains(playerId))
            return sendFailedStatus();

        Request request = new Request(Function.RESPONSE, true, gameId);
        request.setFieldGame(game.getField());
        request.setCountPlayers(game.countPlayer());

        try {
            request.setNextPlayerToGo(game.nextStepPlayer());
        } catch (PlayersNotFoundExceptions playersNotFoundExceptions) {
            playersNotFoundExceptions.printStackTrace();
            return sendFailedStatus();
        }

        return request;
    }

    private Request endGame(int gameId) {
        Game game = games.get(gameId);
        Request request = new Request(Function.RESPONSE, true, gameId);
        request.setFieldGame(game.getField());

        if (game.checkWinner())
            request.setWinner(game.getWinner());
        else
            request.setWinner(-1);

        games.remove(gameId);
        return request;
    }

    private Request sendFailedStatus() {
        return new Request(Function.RESPONSE, false);
    }
}
