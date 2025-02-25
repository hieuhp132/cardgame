import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import controll.Client;

public class GreetClient {

    
    public static void main(String[] args) throws IOException {
    	
    	Socket socket = new Socket("localhost", 12345);
    	Client cl1 = new Client(socket, "asd");
    	Client cl2 = new Client(socket, "abc");
    	Client cl3 = new Client(socket, "aaa");
    	Client cl4 = new Client(socket, "bbb");
    	cl1.listenForMessage(); cl2.listenForMessage(); cl3.listenForMessage(); cl4.listenForMessage();
    	cl1.sendReady(); cl2.sendReady(); cl3.sendReady(); cl4.sendReady();
    	
    }
}
