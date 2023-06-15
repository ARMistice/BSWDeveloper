package de.brettspielwelt.shared.tools;

import java.io.Serializable;

import de.Data;

public class Spielende implements Serializable {
	private static final long serialVersionUID = 380763940730995038L;

	public String name;
	public int duration,spielId;
	public String[] players;
	public int[] points;
	public int[] place;
	public int[] sort;
	public String[] ratings;
	public int [] spielerIds;
	//public String asyncId;
	
	public Spielende(int spielId, String name, int duration, String[] players, int[] points, int[] place, String[] ratings, int[] spielerIds) {
		super();
		this.spielId=spielId;
		this.name = name;
		this.duration = duration;
		this.players = players;
		this.points = points;
		this.place = place;
		this.ratings = ratings;
		this.spielerIds = spielerIds;
		sort = new int[place.length];
		for (int i = 0; i < sort.length; i++) {
			sort[i] = i;
		}
	}

	
	public void sort() {
		int len = place.length;
		int[] platz = new int[len];
		for (int u = 0; u < len - 1; u++) {
			for (int v = u + 1; v < len; v++){
				if (place[u] > place[v]) {
					platz[u]++;
				}
				else if (place[u] <= place[v]) {
					platz[v]++;
				}
			}
		}
		String[] players = new String[len];
		int[] points = new int[len];
		int[] place = new int[len];
		String[] ratings = new String[len];
		int[] spielerIds = new int[len];
		for (int i = 0; i < len; i++) {
			players[platz[i]] = this.players[i];
			points[platz[i]] = this.points[i];
			place[platz[i]] = this.place[i];
			ratings[platz[i]] = this.ratings[i];
			spielerIds[platz[i]] = this.spielerIds[i];
		}
		this.players = players;
		this.points = points;
		this.place = place;
		this.sort = platz;
		this.ratings = ratings;
		this.spielerIds = spielerIds;
	}
}
