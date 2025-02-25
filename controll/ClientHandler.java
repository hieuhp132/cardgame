package controll;

import java.io.*;
import java.net.*;
import model.Spieler;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Spieler spieler;
    private GameServer gameServer;

    public ClientHandler(Socket clientSocket, GameServer gameServer) throws IOException {
        this.clientSocket = clientSocket;
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object receivedObject = in.readObject();
                if (receivedObject instanceof Spieler) {
                    Spieler spieler = (Spieler) receivedObject;
                    System.out.println("Received player: " + spieler.getName() + ", action: " + spieler.getAction() + ", score: " + spieler.getScore());
                    processClientRequestAsSpieler(spieler);
                } else {
                    System.out.println("Unknown message type received: " + receivedObject.getClass());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            disconnectClient();
        }
    }

    private void processClientRequestAsSpieler(Spieler spieler) {
        if (spieler != null) {
            
            this.spieler = spieler;
            String playerAction = this.spieler.getAction();
            System.out.println("Received Spieler from client: " + this.spieler.getName() + ". Action: " + playerAction  + ". Score: " + this.spieler.getScore() + " . Status: " + this.spieler.isReady());
            gameServer.processPlayerAction(spieler, playerAction);
        }
    }

    public void sendObject(Object o) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    public Spieler getSpieler() {
        return spieler;
    }

    public void disconnectClient() {
        gameServer.broadcast(spieler.getName() + " has left the game.");
        closeEverything();
    }

    private void closeEverything() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
