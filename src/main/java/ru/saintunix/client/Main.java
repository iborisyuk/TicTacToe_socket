package ru.saintunix.client;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static PlayGame game = new PlayGame();

    public static void main(String[] args) {

        while (true) {
            printMenu();
            System.out.println("Make you choice: ");
            int func = scanner.nextInt();
            switch (func) {
                case 1: {
                    game.startNewGame();
                    break;
                }
                case 2: {
                    game.listAndConnectToGame();
                    break;
                }
                case 3: {
                    return;
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("Menu:");
        System.out.println("\t1) New game");
        System.out.println("\t2) List all active game");
        System.out.println("\t3) Exit");
    }
}
