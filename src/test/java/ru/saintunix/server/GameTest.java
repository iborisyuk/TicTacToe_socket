package ru.saintunix.server;

import org.junit.Before;
import org.junit.Test;
import ru.saintunix.exception.BusyFieldExceptions;
import ru.saintunix.exception.GameAlreadyEndedExceptions;
import ru.saintunix.exception.PlayersNotFoundExceptions;
import ru.saintunix.exception.TooManyPlayersExceptions;

import static org.junit.Assert.*;

public class GameTest {
    private Game game = null;
    private final int player1 = 1;
    private final int player2 = 2;

    private final String X = "X";
    private final String O = "O";

    @Before
    public void beforeTest() {
        game = new Game();
    }

    @Test
    public void mustAddPlayerToGame() throws TooManyPlayersExceptions {
        assertEquals(game.countPlayer(), 0);
        game.addPlayer(player1);
        assertEquals(game.countPlayer(), 1);
    }

    @Test
    public void mustRemovePlayer() throws TooManyPlayersExceptions, PlayersNotFoundExceptions {
        assertEquals(game.countPlayer(), 0);
        game.addPlayer(player1);
        assertEquals(game.countPlayer(), 1);

        game.removePlayer(player1);
        assertEquals(game.countPlayer(), 0);
    }

    @Test
    public void mustCheckSuccessAddRmPlayerSeveralTime() throws TooManyPlayersExceptions, PlayersNotFoundExceptions {
        assertEquals(game.countPlayer(), 0);
        game.addPlayer(player1);
        game.addPlayer(player2);

        game.removePlayer(player1);
        assertEquals(game.getPlayerByKey(X), -1);
        game.addPlayer(player1);
        assertEquals(game.getPlayerByKey(X), player1);

        game.removePlayer(player2);
        assertEquals(game.getPlayerByKey(O), -1);
        game.addPlayer(player2);
        assertEquals(game.getPlayerByKey(O), player2);
    }

    @Test
    public void mustPlayerStep() throws TooManyPlayersExceptions, PlayersNotFoundExceptions, GameAlreadyEndedExceptions, BusyFieldExceptions {
        game.addPlayer(player1);
        int[] step = {1, 1};
        game.step(player1, step);
        assertEquals(game.getField()[1][1], X);

        assertEquals(game.nextStepPlayer(), -1);
        game.addPlayer(player2);
        assertEquals(game.nextStepPlayer(), player2);

        int[] step2 = {0, 0};
        game.step(player2, step2);
        assertEquals(game.getField()[0][0], O);

        assertEquals(game.nextStepPlayer(), player1);
    }

    @Test(expected = BusyFieldExceptions.class)
    public void mustFailedBecauseFieldBusy() throws TooManyPlayersExceptions, PlayersNotFoundExceptions, GameAlreadyEndedExceptions, BusyFieldExceptions {
        game.addPlayer(player1);
        game.addPlayer(player2);

        game.step(player1, new int[]{0, 0});
        game.step(player2, new int[]{0, 0});
    }

    @Test(expected = PlayersNotFoundExceptions.class)
    public void mustFailedPlayerIncorrect() throws TooManyPlayersExceptions, BusyFieldExceptions, GameAlreadyEndedExceptions, PlayersNotFoundExceptions {
        game.addPlayer(player1);
        game.step(player2, new int[]{1, 1});
    }

    @Test
    public void mustWinGame() throws TooManyPlayersExceptions, BusyFieldExceptions, GameAlreadyEndedExceptions, PlayersNotFoundExceptions {
        game.addPlayer(player1);
        game.addPlayer(player2);

        game.step(player1, new int[]{0, 0});
        game.step(player2, new int[]{1, 0});
        game.step(player1, new int[]{0, 1});
        game.step(player2, new int[]{1, 1});
        game.step(player1, new int[]{0, 2});

        assertTrue(game.checkWinner());
        assertEquals(game.getWinner(), player1);
    }

}
