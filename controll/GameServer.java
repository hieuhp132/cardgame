package controll;

import java.io.IOException;
import java.util.*;
import model.Spiel;
import model.Spieler;

/**
 * Main function of this class is to processing client request after connected to server and using game logik
 * */

public class GameServer {
    private static final int MAX_PLAYERS = 4;
    private static List<ClientHandler> clientHandlers = new ArrayList<>();
    private int readyPlayers = 0;
    
    public GameServer() {}

    public boolean addPlayer(ClientHandler clientHandler) {
        if (clientHandlers.size() < MAX_PLAYERS) {
            clientHandlers.add(clientHandler);
            return true;
        }
        return false;  // Game is full
    }

    public void processPlayerAction(Spieler player, String action) {
        switch (action.toLowerCase()) {
            case "ready":
                if (player.isReady()) {
                    readyPlayers++;
                    if (readyPlayers == MAX_PLAYERS) {
                        startGame();
                    }
                }
                break;

            case "submit":
                int score = player.getScore();
                System.out.println("Received score: " + score);
                break;

            default:
                System.out.println("Invalid action from " + player.getName());
        }
    }

    private void startGame() {
        System.out.println("All players are ready. Starting the game....");
        // Game start logic, like shuffling cards, etc.
        // For example, we can deal cards here
        Spiel s = new Spiel(clientHandlers);
        
    }

    public boolean isFull() {
        return clientHandlers.size() >= MAX_PLAYERS;
    }

    public static List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void broadcast(String message) {
        for (ClientHandler client : clientHandlers) {
            try {
                client.sendObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastWinner(Spieler winner) {
        for (ClientHandler client : clientHandlers) {
            try {
                if (client.getSpieler().equals(winner)) {
                    client.sendObject("You are the winner!!!");
                } else {
                    client.sendObject("The winner is " + winner.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
