package controll;

import java.io.*;
import java.net.*;
/**
 * This class is to creating a new Server that should able to listen for new connections of clients (max. 4). 
 * If a new client was connected, it will create new class named ClientHandler that should holding new client and processes their request.
 * */

public class Server {

    private static ServerSocket serverSocket;
    private static GameServer gameServer;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        gameServer = new GameServer();
    }

    public void start() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("[Server]: Waiting for new connections !");
                Socket socket = serverSocket.accept();
                System.out.println("[Server]: A new client has connected");

                ClientHandler clientHandler = new ClientHandler(socket, gameServer);

                if (gameServer.addPlayer(clientHandler)) {
                    new Thread(clientHandler).start();
                    gameServer.broadcast("[Server]: A new player has joined. Total players: " + gameServer.getClientHandlers().size());
                } else {
                    clientHandler.sendObject("[Server]: Game is full!");
                }
            }
        } catch (IOException e) {
            System.err.println("[Server]: Error accepting client connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 1239;
        serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.start();
    }
}
