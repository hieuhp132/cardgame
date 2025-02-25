package controll;

import java.io.*;
import java.net.*;


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
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");

                ClientHandler clientHandler = new ClientHandler(socket, gameServer);

                if (gameServer.addPlayer(clientHandler)) {
                    new Thread(clientHandler).start();
                    gameServer.broadcast("A new player has joined. Total players: " + gameServer.getClientHandlers().size());
                } else {
                    clientHandler.sendObject("Game is full!");
                }
            }
        } catch (IOException e) {
            System.err.println("Error accepting client connection: " + e.getMessage());
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
