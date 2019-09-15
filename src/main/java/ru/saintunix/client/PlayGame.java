package ru.saintunix.client;

import ru.saintunix.util.Request;

import static ru.saintunix.util.Requests.readRequest;
import static ru.saintunix.util.Requests.sendRequest;
import static ru.saintunix.util.Function.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PlayGame {
    private Socket socket;
    private final Random rand = new Random();
    private Scanner scanner = new Scanner(System.in);

    private int gameId = -1;
    private int playerId = rand.nextInt(9999);

    public PlayGame() {
        try {
            socket = new Socket("localhost", 2007);
        } catch (IOException e) {
            System.err.println("Failed start socket");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void startNewGame() {
        Request reqCreateNewGame = new Request(NEW_GAME, true, playerId, 0);
        sendRequest(socket, reqCreateNewGame);

        Request reqGame = readRequest(socket);
        gameId = reqGame.getGameId();

        playGame(reqGame);
    }

    private void playGame(Request req) {
        while (true) {
            gameScreen(req);
            req = nextStep(req);
        }
    }

    private void gameScreen(Request req) {
        String[][] field = req.getFieldGame();

        System.out.printf("[%s][%s][%s]\n[%s][%s][%s]\n[%s][%s][%s]\n",
                field[0][0], field[0][1], field[0][2],
                field[1][0], field[1][1], field[1][2],
                field[2][0], field[2][1], field[2][2]);

    }

    private void checkWinner(Request req) {
        int winner = req.getWinner();
        if (winner == -1 || winner > 0) {
            if (winner == playerId) {
                System.out.println("You win!");

            } else if (winner != -1) {
                System.out.println("Win player 2");
            } else {
                System.out.println("Nothing");
            }

            sendRequest(socket, new Request(END_GAME, true));
            System.exit(0);
        }
    }

    private Request nextStep(Request req) {
        int nextPlayerToGo = req.getNextPlayerToGo();

        checkWinner(req);

        // waite you step
        if (nextPlayerToGo != playerId) {
            System.out.println("Expect another player to move.");

            while (true) {
                sendRequest(socket, new Request(STATUS_GAME, true, playerId, gameId));
                Request reqStatus = readRequest(socket);

                if (!reqStatus.getStatus()) {
                    System.err.println("Error: game end");
                    System.exit(1);
                }

                checkWinner(reqStatus);

                nextPlayerToGo = reqStatus.getNextPlayerToGo();

                if (reqStatus.getStatus() && nextPlayerToGo == playerId) {
                    gameScreen(reqStatus);
                    break;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (nextPlayerToGo == playerId) {
            System.out.print("Enter you step [pattern (1 1)]:");
            int[] step = new int[2];
            while (true) {
                String stepStr;
                try {
                    stepStr = scanner.nextLine();
                    step[0] = Integer.parseInt(stepStr.split(" ")[0]);
                    step[1] = Integer.parseInt(stepStr.split(" ")[1]);
                } catch (PatternSyntaxException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Incorrect step!");
                    continue;
                } catch (NumberFormatException e) {
                    continue;
                }

                break;
            }

            Request reqStep = new Request(STEP, true, playerId, gameId);
            reqStep.setStep(step);

            sendRequest(socket, reqStep);
        }

        return readRequest(socket);
    }

    public void listAndConnectToGame() {
        sendRequest(socket, new Request(LIST_ALL_GAME, true));
        Request rListGame = readRequest(socket);

        ArrayList<Integer> freeGame = rListGame.getFreeGame();

        if (freeGame.size() == 0) {
            System.out.println("Not found free game!");
            return;
        }

        int id = 0;
        System.out.println("Free game:");
        for (Integer idGame : freeGame) {
            System.out.printf("%d) %d\n", id++, idGame);
        }

        System.out.print("\nEnter number game to connect (and -1 return to Menu): ");
        int connectGameId;
        while (true) {
            try {
                connectGameId = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Incorrect number!");
                continue;
            }

            if (connectGameId == -1)
                return;

            if (connectGameId > id) {
                System.err.println("Incorrect number!");
                continue;
            }

            break;
        }

        sendRequest(socket, new Request(PLAYER_CONNECT_TO_GAME, true, playerId, freeGame.get(connectGameId)));
        Request reqConnect = readRequest(socket);

        if (!reqConnect.getStatus()) {
            System.err.println("Failed connect to game!");
            return;
        }

        gameId = freeGame.get(connectGameId);

        System.out.println("Connect to game success!\nStart game");
        playGame(reqConnect);
    }

}
