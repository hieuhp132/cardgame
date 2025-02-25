package Beta;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    private static Car[] storedCars = new Car[3]; // Local storage for cars

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            // Automatically create cars and store them
            storedCars[0] = new Car("Tesla", 2024, true);
            storedCars[1] = new Car("BMW", 2020, false);
            storedCars[2] = new Car("Audi", 2019, true);

            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server is waiting for a connection...");

            // Accept a client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");

            // Create input and output streams for object communication
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            output.writeObject(storedCars);

            Car[] fromClient = (Car[]) input.readObject();
            
            displayCars(storedCars);
            
            storedCars = fromClient;
            
            displayCars(storedCars);
            
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public static void displayCars(Car[] list) {
    	for(Car c: list) {
    		System.out.println(c.toString());
    	}
    }
    

}
