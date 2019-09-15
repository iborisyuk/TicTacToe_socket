package ru.saintunix.util;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

import static java.lang.System.exit;

public class Requests {
    private static final Gson gson = new Gson();

    public static Request readRequest(Socket socket) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }

        String request = null;
        while (true) {
            try {
                if (request != null && !reader.ready())
                    break;

                request = reader.readLine();
            } catch (IOException e) {
                System.err.println("Failed read from input stream.");
                e.printStackTrace();
            }
        }

//        System.out.println("DEBUG: " + request);
        return gson.fromJson(request, Request.class);
    }

    public static void sendRequest(Socket socket, Request request) {
//        System.out.println("DEBUG: " + request);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }

        String jsonRequest = gson.toJson(request);

        try {
            writer.write(jsonRequest);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed send json request:\n\t" + jsonRequest);
            e.printStackTrace();
        }
    }
}
