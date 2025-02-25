package Beta;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static Car[] storedCars = new Car[3]; // Local storage for cars

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            // Automatically create cars and store them
            storedCars[0] = new Car("Tesla", 2025, true);
            storedCars[1] = new Car("BMW", 2020, false);
            storedCars[2] = new Car("Audi", 2019, true);

            // Connect to the server
            System.out.println("Attempting to connect to server...");
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Client connected to server.");

            // Create input and output streams
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            Car[] fromServer = (Car[]) input.readObject();
            
            displayCars(fromServer);
            
            fromServer[0].setYear(00000);
            fromServer[0].setName("It was modified");
            
            displayCars(fromServer);

            output.writeObject(fromServer);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void displayCars(Car[] list) {
    	for(Car c: list) {
    		System.out.println(c.toString());
    	}
    }
    
}
