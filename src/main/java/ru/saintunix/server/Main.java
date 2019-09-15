package ru.saintunix.server;


import java.io.IOException;
import java.net.ServerSocket;

import static java.lang.System.exit;

public class Main {
    private static ServerSocket serverSocket;
    private static GameServer gameServer;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(2007);
        } catch (IOException e) {
            System.err.println("Failed start server socket:");
            e.printStackTrace();
            exit(1);
        }

        gameServer = new GameServer();
        System.out.println("Start game server;");

        while (true) {
            try {
                new RouteRequest(serverSocket.accept(), gameServer);
            } catch (IOException e) {
                System.err.println("Failed accept");
                e.printStackTrace();
                exit(1);
            }
        }
    }
}
