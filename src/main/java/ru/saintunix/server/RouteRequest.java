package ru.saintunix.server;

import ru.saintunix.util.Function;
import ru.saintunix.util.Request;

import java.io.IOException;
import java.net.Socket;

import static ru.saintunix.util.Requests.readRequest;
import static ru.saintunix.util.Requests.sendRequest;

public class RouteRequest extends Thread {
    private Socket socket;
    private GameServer gameServer;

    public RouteRequest(Socket socket, GameServer gameServer) {
        this.socket = socket;
        this.gameServer = gameServer;

        start();
    }

    public void run() {
        game:
        while (true) {
            Request request = readRequest(socket);

            System.out.println("Request: " + request);

            Request sendReq;

            switch (request.getFunction()) {
                case NEW_GAME: {
                    sendReq = gameServer.startNewGame(request.getPlayerId());
                    break;
                }

                case LIST_ALL_GAME: {
                    sendReq = gameServer.listAllGame();
                    break;
                }
                case STATUS_GAME: {
                    sendReq = gameServer.getStatusGame(request.getGameId(), request.getPlayerId());
                    break;
                }

                case STEP: {
                    sendReq = gameServer.step(request.getGameId(), request.getPlayerId(), request.getStep());
                    break;
                }

                case PLAYER_CONNECT_TO_GAME: {
                    sendReq = gameServer.playerConnectToGame(request.getPlayerId(), request.getGameId(), true);
                    break;
                }
                case PLAYER_LEAVE_THE_GAME: {
                    sendReq = gameServer.playerLeaveTheGame(request.getPlayerId(), request.getGameId());
                    break;
                }
                case END_GAME: {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break game;
                }
                default: {
                    sendReq = new Request(Function.RESPONSE, false);
                }
            }

            sendRequest(socket, sendReq);
        }
    }
}
