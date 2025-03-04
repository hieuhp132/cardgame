package controll;

import java.io.*;
import java.net.*;
/**
 * This class is to creating a new Server that should able to listen for new connections of clients (max. 4). 
 * If a new client was connected, it will create new class named ClientHandler that should holding new client and processes their request.
 * */

public class Server {

    static private ServerSocket serverSocket;
    static private GameServer gameServer;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
       	this.gameServer = new GameServer();
    }

    public void start() throws IOException {
        try {
            while (!this.serverSocket.isClosed()) {
                System.out.println("[Server]: Waiting for new connections...");
                Socket socket = this.serverSocket.accept();
                System.out.println("[Server]: A new client has connected");

                ClientHandler clientHandler = new ClientHandler(socket, this.gameServer);

                if (this.gameServer.addPlayer(clientHandler)) {
                    new Thread(clientHandler).start();
                    this.gameServer.broadcast("[Server]: A new player has joined. Total players: " + gameServer.getClientHandlers().size());
                } else {
                    clientHandler.sendObject("[Server]: Game is full!");
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("[Server]: Error accepting client connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 1239;
        try {
            serverSocket = new ServerSocket(port);
            Server server = new Server(serverSocket);
            server.start();
        } catch(IOException e) {
            System.err.println("[Server]: Error starting server: " + e.getMessage());
        }
    }

}
