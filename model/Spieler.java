package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Spieler implements Serializable {

	private static final long serialVersionUID = 1L;
	String name;
	ArrayList<Karte> hands;
	boolean npc;
	boolean ready;
	String action;
	Integer score;

	public Spieler(String name) {
		this.name = name;
		hands = new ArrayList<>();
		this.npc = false;
		this.ready = false;
		this.score = 0;
	}

	public Spieler() {
		
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public void setStatus(boolean ready) {
		this.ready = ready;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public String getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public boolean getRolle() {
		return npc;
	}

	public boolean isReady() {
		return ready;
	}

	public void setHands(ArrayList<Karte> list) {
		this.hands = list;
	}
	
	public ArrayList<Karte> getHands() {
		return hands;
	}

	public int getScoreSumme() {
		int summe = 0;
		for (Karte k : this.hands) {
			summe += k.getValue();
		} return summe;
	}
	
	public int getScore() {

		return this.score;
	}
	
	public String str() {
		return "Players name: " + this.getName();
	}

}

class Kartengeber extends Spieler {

	ArrayList<Karte> hands1;
	ArrayList<Karte> hands2;

	public Kartengeber(String name) {
		super(name);
		this.npc = true;
		this.ready = true;
		hands1 = new ArrayList<>();
		hands2 = new ArrayList<>();
	}

	public ArrayList<Karte> getHands1() {
		return hands1;
	}

	public ArrayList<Karte> getHands2() {
		return hands2;
	}

}
