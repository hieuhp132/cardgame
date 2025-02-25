package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Karte implements Serializable {

	private static final long serialVersionUID = 1L;
	int value;
	private String suite;

	public Karte(int value, String suite) {
		this.value = value;
		this.suite = suite;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setSuite(String suite) {
		this.suite = suite;
	}

	public int getValue() {
		return this.value;
	}
	
	public String getSuite() {
		return this.suite;
	}
	
	public static ArrayList<Karte> init() {
		String[] esuite = { "pik", "karo", "herz", "kreuz" };
		ArrayList<Karte> kartenList = new ArrayList<>();
		Karte tmp;
		for (int i = 2; i < 10; i++) {
			for (int j = 0; j < 4; j++) {
				tmp = new Karte(i, esuite[j]);
				kartenList.add(tmp);
			}
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				tmp = new Karte(10, esuite[j]);
				kartenList.add(tmp);
			}
		}

		for (int j = 0; j < 4; j++) {
			tmp = new Karte(11, esuite[j]);
			kartenList.add(tmp);
		}

		System.out.println("Number of cards: " + kartenList.size());
		return kartenList;

	}

	public String str() {
		return "value: " + this.value + ", suite: " + this.suite + ".\n";
	}

	public static void main(String[] args) {

		ArrayList<Karte> karten = Karte.init();

		for (Karte k : karten) {
			System.out.print(k.str());
		}
	}
}
