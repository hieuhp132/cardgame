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
    private boolean isRunning = true; // flag to manage thread execution
	
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
            while (isRunning) {
                Object receivedObject = this.in.readObject();
                if (receivedObject instanceof Spieler) {
                    Spieler updatedSpieler = (Spieler) receivedObject;
                    
                    // Ensure we track the same player instance
                    if(this.spieler == null) {
                        this.spieler = updatedSpieler;
                    } else {
                        this.spieler.setAction(updatedSpieler.getAction());
                        this.spieler.setScore(updatedSpieler.getScore());
                    }

                    System.out.println("[Client Handler]: Player: " + this.spieler.getName() + ", performed action: " + this.spieler.getAction() + ", score: " + this.spieler.getScore());
                    //this.processClientRequestAsSpieler(spieler);
                    gameServer.processPlayerAction(this.spieler, this.spieler.getAction());
                } else {
                    System.out.println("[Client Handler]: Unknown message type received: " + receivedObject.getClass());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            this.disconnectClient();
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
        try {
            isRunning = false;
            this.gameServer.broadcast("[ClientHandler]: " + (spieler != null ? spieler.getName() : "Unknown player") + " has left the game.");
            //this.closeEverything();
        } catch(IOException e) {
            System.err.println("[ClientHandler]: Error during disconnect - " + e.getMessage());
        } 
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
