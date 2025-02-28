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
                
		System.out.println("[Game Server]: " + player.getName() + " is ready? " + player.isReady());

		if (player.isReady()) {
                    readyPlayers++;
                    if (readyPlayers == MAX_PLAYERS) {
                        startGame();
                    }
                }
                break;

            case "submit":
                updatePlayer(player); // Stelle sicher, dass der Spieler aktualisiert wird.
                System.out.println("[Game Server]: Received score: " + player.getScore());
                break;

            default:
                System.out.println("[Game Server]: Invalid action from " + player.getName());
        }
    }

    private void startGame() {
        System.out.println("[Game Server]: All players are ready. Starting the game....");
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

    public void updatePlayer(Spieler updatedPlayer) {
    	for(ClientHandler client : clientHandlers) {
		if(client.getSpieler().getName().equals(updatedPlayer.getName())) {
			client.setSpieler(updatedPlayer);
		}
	}
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
                    client.sendObject("[Game Server]: You are the winner!!!");
                } else {
                    client.sendObject("[Game Server]: The winner is " + winner.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
