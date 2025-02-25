import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import controll.Client;
import controll.Server;
import model.Spiel;
import model.Spieler;

public class Programtest {

public static void main(String[] args) throws IOException, InterruptedException {
	
	
	   Scanner s = new Scanner(System.in);
	   boolean stop = false;
	   int i=0;
	   while(!stop) {
		   String input = s.nextLine();
		   System.out.println("i: " + i +input); i++;
	   }
	
}	
	
}
