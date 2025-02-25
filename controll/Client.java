package controll;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import model.*;

/*
 * This is a client class that should working together with Spieler object and processing request from Server.
 * */

public class Client {

    static Socket socket;
    static public Spieler s;
    static Scanner sc = new Scanner(System.in);
    static ObjectOutputStream out;
    static ObjectInputStream in;

    /*
     * Constructor. Creating a new Client with a socket and spielers name.
     * Also create a new instance of in, out where object should be seen from it.
     * */

    public Client(Socket socket, String name) {
        try {
            this.s = new Spieler(name);
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    /*
     * Sending object to server.
     * */

    public void sendObject(Object o) throws IOException {
        if(o != null) {
        	out.writeObject(o);
            out.flush();
        } else { System.err.println("null object"); } 
    }

    /*
     * Sending a message to server.
     * */

    public void sendMessage() throws IOException {
        while (socket.isConnected()) {
            String msgToSend = sc.nextLine();
            s.setAction(msgToSend);
            sendObject(s);
        }
        closeEverything(socket, in, out);
    }

    /*
     * Send request to server (as an Object)
     */

    public void sendRequest(Object request) throws IOException {
        try {
        	if (request instanceof String) {
                handleStringRequest((String) request);
            } else if (request instanceof Integer) {
                handleScoreRequest((Integer) request);
            } else if (request instanceof ArrayList<?>) {
                handleCardSelectionRequest((ArrayList<Karte>) request);
            }
        } catch(IOException e) {
        	System.err.println("Error handling request: " + request.getClass());
        }
    }

    /*
     * Processing request from server as an String
     * */

    private void handleStringRequest(String action) throws IOException {
        s.setAction(action);
        switch (action.toLowerCase()) {
            case "ready":
                s.setStatus(true);
                sendObject(s);
                waitForServerResponse();
                break;
            case "leave":
                sendObject(s);
                System.out.println("You have left the game.");
                closeEverything(socket, in, out);
                System.exit(0);
                break;
            default:
                System.out.println("Invalid action.");
        }
    }

    /*
     * Processing request from Server as an Integer
     * */

    private void handleScoreRequest(Integer score) throws IOException {
        s.setAction("submit"); s.setScore(score);
        System.out.println("Sending player: " + s.getName() + ", action: " + s.getAction() + ", score: " + s.getScore());
        sendObject(s);
        waitForServerResponse();
    }

    /*
     * Processing request from server as an ArrayList<Karte> (Kartenliste as ArrayList)
     * */

    private void handleCardSelectionRequest(ArrayList<Karte> cards) throws IOException {
        s.setAction("deal");
        System.out.println("Sending player: " + s.getName() + ", action: " + s.getAction() + ", score: " + s.getScore());
        sendObject(s);
        waitForServerResponse();
    }

    /*
     * After sending request to server, wait for it response. (Multithreading.!)
     * */

    private void waitForServerResponse() {
        synchronized (this) {
            try {
                wait(); // Wait for server response
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * listen for server message after socket is created. Initialized state after running client.
     * */

    public void listenForMessage() {
        new Thread(() -> {
            Object msgFromServer;
            try {
                while (socket.isConnected()) {
                    if (socket.isClosed()) {
                        break;
                    }

                    msgFromServer = in.readObject();

                    if (msgFromServer instanceof String) {
                        handleStringMessage((String) msgFromServer);
                    } else if (msgFromServer instanceof ArrayList<?>) {
                        handleCardListMessage((ArrayList<?>) msgFromServer);
                    } else {
                        System.out.println("Received unsupported message type: " + msgFromServer.getClass().getName());
                    }
                }
            } catch (EOFException e) {
                System.err.println("End of file reached. Server might have closed the connection.");
                e.printStackTrace();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error reading message: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeEverything(socket, in, out); // Close resources on error
            }
        }).start();
    }

    /*
     * processing Message from Server as an String
     * */

    private void handleStringMessage(String message) {
        System.out.println(message); // Print server's message
        if (message.contains("game has started")) {
            synchronized (this) {
                notify(); // Notify the client that the game has started
            }
        }

        // Check if the message contains "cards"
        if (message.contains("cards")) {
            try {
                ArrayList<Karte> receivedCards = (ArrayList<Karte>) in.readObject();
                System.out.println("You have received the following cards:");
                for (Karte card : receivedCards) {
                    System.out.println(card.str()); // Display each card's string representation
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * processing Kartenliste von Server als ArrayList
     * */

    private void handleCardListMessage(ArrayList<?> listFromServer) throws IOException {
        if (!listFromServer.isEmpty() && listFromServer.get(0) instanceof Karte) {
            System.out.println("You have received the following cards. Write SUBMIT to send score, PICK to pick cards, or else.");
            ArrayList<Karte> receivedCards = (ArrayList<Karte>) listFromServer;
            s.setHands(receivedCards);

            int index = 0;
            System.out.println("On hand: ");
            for (Karte card : s.getHands()) {
                System.out.println(index + ": " + card.str());
                index++;
            }

            boolean picked = false;
            while (!picked) {
                String input = sc.nextLine().trim();
                switch (input.toLowerCase()) {
                    case "submit":
                        
                        sendRequest(s.getScoreSumme());
                        System.out.println("Score sent!");
                        picked = true;
                        break;
                    case "pick":
                        handleCardPick(receivedCards);
                        picked = true;
                        break;
                    default:
                        System.out.println("Invalid action. Try again!");
                }
            }
        }
    }

    /*
     * Pick a card on hand a send to server.
     * */

    private void handleCardPick(ArrayList<Karte> receivedCards) {
        ArrayList<String> listOfChoice = new ArrayList<>();
        ArrayList<Karte> chosenKarten = new ArrayList<>();
        System.out.println("Enter card indices (or type 'STOP' to end selection):");

        while (true) {
            String input = sc.nextLine().trim();
            if ("STOP".equalsIgnoreCase(input)) break;

            try {
                Integer index = Integer.parseInt(input);
                if (index >= 0 && index < receivedCards.size()) {
                    listOfChoice.add(input);
                } else {
                    System.out.println("Invalid card index. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Add chosen cards to the chosenKarten list
        for (String str : listOfChoice) {
            Integer i = Integer.parseInt(str);
            chosenKarten.add(receivedCards.get(i));
            s.getHands().remove(i); // Remove picked cards from the hand
        }

        try {
            sendRequest(chosenKarten);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Closing socket. (For disconnecting, ragequit,....)
     * */

    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name for group chat: ");
        String username = sc.nextLine();

        Socket socket = new Socket("localhost", 1239);
        Client cl = new Client(socket, username);

        cl.listenForMessage();

        System.out.println("Type 'Ready' when you're ready to play the game. Or 'Leave' to leave.");
        while (true) {
            String message = sc.nextLine();
            cl.sendRequest(message);
        }
    }
}
