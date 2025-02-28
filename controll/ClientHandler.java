package controll;

import java.io.*;
import java.net.*;
import model.Spieler;

/*
 * ClientHandler taking request from clients, process it and tell gamelogik with functions should be use to controll the logik after a player from client sending their request.
 * */

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Spieler spieler;
    private GameServer gameServer;

	
    /*
     * Constructor. Creating new ClientHandler mit clientSocket, gameServer, in and out for object exchange.
     * */

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
                Object receivedObject = this.in.readObject();
                if (receivedObject instanceof Spieler) {
                    Spieler spieler = (Spieler) receivedObject;
                    System.out.println("[Client Handler]: Received player: " + spieler.getName() + ", action: " + spieler.getAction() + ", score: " + spieler.getScore());
                    this.processClientRequestAsSpieler(spieler);
                } else {
                    System.out.println("[Client Handler]: Unknown message type received: " + receivedObject.getClass());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            this.disconnectClient();
        }
    }

    private void processClientRequestAsSpieler(Spieler spieler) {
        if (spieler != null) {
            
            this.spieler = spieler; // Speicher die aktuelle Spieler-Instanz
            String playerAction = this.spieler.getAction();
            System.out.println("[Client Handler]: Received Spieler from client: " + this.spieler.getName() + ". Action: " + playerAction  + ". Score: " + this.spieler.getScore() + " . Status: " + this.spieler.isReady());
            this.gameServer.processPlayerAction(this.spieler, playerAction);
        }
    }

    public void sendObject(Object o) throws IOException {
        this.out.writeObject(o);
        this.out.flush();
    }

    public Spieler getSpieler() {
        return this.spieler;
    }

    public void setSpieler(Spieler spieler) {
    	this.spieler = spieler;
    }

    public void disconnectClient() {
        this.gameServer.broadcast("[ClientHandler]: " + spieler.getName() + " has left the game.");
        this.closeEverything();
    }

    private void closeEverything() {
        try {
            if (this.in != null) this.in.close();
            if (this.out != null) this.out.close();
            if (this.clientSocket != null) this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
