package ru.saintunix.server;

import org.junit.Before;
import org.junit.Test;
import ru.saintunix.util.Function;
import ru.saintunix.util.Request;

import java.util.Arrays;

import static org.junit.Assert.*;

public class GameServerTest {
    private GameServer gameServer;

    private String[][] startField = {{"", "", ""}, {"", "", ""}, {"", "", ""}};

    private final int player1 = 10;
    private final int player2 = 20;
    private final int player3 = 30;

    private final String X = "X";
    private final String O = "O";

    @Before
    public void beforeMethod() {
        gameServer = new GameServer();
    }

    @Test
    public void mustCreateNewGame() {
        Request resp = gameServer.startNewGame(player1);
        assertEquals(resp.getFunction(), Function.RESPONSE);
        assertTrue(resp.getStatus());
        assertTrue(resp.getGameId() > 0);
        assertEquals(resp.getNextPlayerToGo(), player1);
        assertEquals(resp.getCountPlayers(), 1);
        assertTrue(Arrays.deepEquals(resp.getFieldGame(), startField));
    }

    @Test
    public void mustConnectNewPlayerToActiveGame() {
        Request rNewGame = gameServer.startNewGame(player1);
        assertEquals(rNewGame.getCountPlayers(), 1);
        Request resp = gameServer.playerConnectToGame(player2, rNewGame.getGameId(), true);
        assertEquals(resp.getFunction(), Function.RESPONSE);
        assertTrue(resp.getStatus());
        assertTrue(resp.getGameId() > 0);
        assertEquals(resp.getNextPlayerToGo(), player1);
        assertEquals(resp.getCountPlayers(), 2);
        assertTrue(Arrays.deepEquals(resp.getFieldGame(), startField));
    }

    @Test
    public void mustLeaveOnePlayerFromGame() {
        Request resp = gameServer.startNewGame(player1);
        int gameId = resp.getGameId();

        Request r = gameServer.playerLeaveTheGame(player1, gameId);
        assertTrue(r.getStatus());

        Request rStatusGameGame = gameServer.getStatusGame(gameId, player1);
        assertFalse(rStatusGameGame.getStatus());
    }

    @Test
    public void mustLeaveTwoPlayersFromGame() {
        Request resp = gameServer.startNewGame(player1);
        int gameId = resp.getGameId();

        Request r = gameServer.playerConnectToGame(player2, gameId, true);

        assertEquals(r.getCountPlayers(), 2);

        gameServer.playerLeaveTheGame(player2, gameId);
        gameServer.playerLeaveTheGame(player1, gameId);

        Request rStatusGameGame2 = gameServer.getStatusGame(gameId, player1);
        assertFalse(rStatusGameGame2.getStatus());

        assertEquals(gameServer.listAllGame().getFreeGame().size(), 0);
    }

    @Test
    public void listAllGame() {
        assertTrue(gameServer.startNewGame(player1).getStatus());
        assertTrue(gameServer.startNewGame(player2).getStatus());

        Request rList = gameServer.listAllGame();
        assertEquals(rList.getFreeGame().size(), 2);

        gameServer.playerConnectToGame(player3, rList.getFreeGame().get(0), true);
        Request r = gameServer.listAllGame();
        assertEquals(r.getFreeGame().size(), 1);
    }

    @Test
    public void mustStepPlayers() {
        Request startGame = gameServer.startNewGame(player1);
        int gameId = startGame.getGameId();

        gameServer.playerConnectToGame(player2, gameId, false);

        assertEquals(startGame.getNextPlayerToGo(), player1);

        Request sP1 = gameServer.step(gameId, player1, new int[]{0, 0});
        assertEquals(sP1.getFieldGame()[0][0], "X");

        Request sP2 = gameServer.step(gameId, player2, new int[]{1, 1});
        assertEquals(sP2.getFieldGame()[1][1], "O");

        // Incorrect player step
        Request failStep = gameServer.step(gameId, player2, new int[]{1, 1});
        assertFalse(failStep.getStatus());

        // Busy field step
        Request failStep2 = gameServer.step(gameId, player1, new int[]{1, 1});
        assertFalse(failStep2.getStatus());

    }

    @Test
    public void mustGetWinner() {
        Request r = gameServer.startNewGame(player1);
        int gameId = r.getGameId();

        gameServer.playerConnectToGame(player2, gameId, false);

        gameServer.step(gameId, player1, new int[]{0, 0});
        gameServer.step(gameId, player2, new int[]{1, 0});
        gameServer.step(gameId, player1, new int[]{0, 1});
        gameServer.step(gameId, player2, new int[]{1, 1});
        Request rWin = gameServer.step(gameId, player1, new int[]{0, 2});

        assertEquals(rWin.getWinner(), player1);
    }

}
