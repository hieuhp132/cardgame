package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.List;

import controll.Server;
import controll.ClientHandler;
import controll.GameServer;

public class Spiel {
	public Kartengeber kartengeber;
	public ArrayList<Spieler> teilnehmende;
	public ArrayList<Karte> kartenList;
	public boolean end;
	public int readycount;

	// Constructor to initialize the game
	public Spiel(List<ClientHandler> clientHandlers) {
		if(clientHandlers != null) {
			kartenList = Karte.init();
			Collections.shuffle(kartenList);

			kartengeber = new Kartengeber("kartengeber"); // Initialize the kartengeber
			kartengeber.hands = kartenList; // Assign the card list to the kartengeber
			//displayCards(kartengeber.hands);
			
			System.out.println("#ClientHandler: " + clientHandlers.size());
			System.out.println("First player: " + clientHandlers.get(0).getSpieler().getName());
			
			teilnehmende = new ArrayList<>();
			for (int i = 0; i < clientHandlers.size(); i++) {
				Spieler tmp = new Spieler(clientHandlers.get(i).getSpieler().getName());
				teilnehmende.add(tmp);
			}

			System.out.println("#Teilnehmende: " + teilnehmende.size());
			dealingCards(); 
			end = false;
		} else { System.out.println("Received players list from server ist null.!"); } 
		
		
	}

	public Spiel() {
		kartenList = Karte.init();
		Collections.shuffle(kartenList);

		kartengeber = new Kartengeber("kartengeber"); // Initialize the kartengeber
		kartengeber.hands = kartenList; // Assign the card list to the kartengeber

		teilnehmende = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Spieler tmp = new Spieler("a" + i);
			teilnehmende.add(tmp);
		}

		System.out.println("#Teilnehme: " + teilnehmende.size());

		end = false;
	}

	public void dealingCards() {

		int i = 0;
		while (kartengeber.hands1.size() != 3 && kartengeber.hands2.size() != 3
				&& teilnehmende.get(0).getHands().size() != 3 && teilnehmende.get(1).getHands().size() != 3
				&& teilnehmende.get(2).getHands().size() != 3 && teilnehmende.get(3).getHands().size() != 3) {

			if (kartengeber.hands1.size() == 3 && kartengeber.hands2.size() == 3)
				continue;

			kartengeber.hands1.add(kartengeber.hands.get(i));
			kartengeber.hands.remove(i);
			kartengeber.hands2.add(kartengeber.hands.get(i));
			kartengeber.hands.remove(i);

			for (int j = 0; j < teilnehmende.size(); j++) {
				if (teilnehmende.get(j).getHands().size() == 3)
					continue;
				teilnehmende.get(j).getHands().add(kartengeber.hands.get(i));
				kartengeber.hands.remove(i);
			}
			i++;
		}
		
		sendCardstoClients();
		
		
	}

	private void sendCardstoClients() {
		if(teilnehmende.size() == 0  || teilnehmende == null ) {
			System.err.println("Es gibt keine Teilnehmende, die die Karten erhalten sollen.");
			return;
		}
		
		for(int i = 0; i < teilnehmende.size(); i++) {
			Spieler player = teilnehmende.get(i);
			ClientHandler clientHandler = GameServer.getClientHandlers().get(i);
			
			try {
				clientHandler.sendObject(player.getHands());
			} catch(IOException e) {
				System.err.println("Error sending cards to player " + player.getName());
				e.printStackTrace();
			}
		}
		
	}

	public ArrayList<Spieler> getTeilnehmede() {
		if(teilnehmende == null) {
			System.out.println("Es gibt keine Teilnehmer.");
			return null;
		}
		return teilnehmende;
	}

	public Kartengeber getKartengeber() {
		if(kartengeber == null) {
			System.out.println("No Cards dealer");
			return null;
		}
		return kartengeber;
	}

	public Spieler getSpieler(ArrayList<Spieler> list, int index) {
		if(list == null) {
			System.out.println("Null list!");
			return null;
		}
		return list.get(index);
	}
	
	public void displayCards(ArrayList<Karte> list) {
		if(list == null || list.size() == 0) {
			System.out.println("Leere list");
			return;
		}
		
		int i = 0;
		for (Karte k : list) {
			System.out.println(i + ": " + k.str());
			i++;
		}

	}

	public boolean validCheck(Integer[] list) {
		if(list == null) {
			System.err.println("list leer!");
			return false;
		}
		
		for (int i = 0; i < list.length; i++) {
			for (int j = i + 1; j < list.length; j++) {
				if (list[i] == list[j])
					return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Spiel s = new Spiel();

		s.dealingCards();

		ArrayList<Spieler> sList = s.getTeilnehmede();
		for (int i = 0; i < sList.size(); i++) {
			System.out.print(i + ".te own #cards: " + sList.get(i).getHands().size() + "\n");
		}

		System.out.print("#CardsHand1 KB: " + s.kartengeber.getHands1().size() + "\n");
		System.out.print("#CardsHand2 KB: " + s.kartengeber.getHands2().size() + "\n");
		System.out.print("#CardsHand KB: " + s.kartengeber.getHands().size() + "\n");

	}
}
