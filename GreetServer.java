import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import controll.Server;

public class GreetServer {

    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	int port = 12345;
    	ServerSocket serverSocket = new ServerSocket(port);
    	Server sv = new Server(serverSocket);
    	sv.start();
    	
    }
}