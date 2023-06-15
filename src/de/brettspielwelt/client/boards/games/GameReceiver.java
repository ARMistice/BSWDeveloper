package de.brettspielwelt.client.boards.games;

import de.Data;
import de.brettspielwelt.client.boards.BaseBoard;
import de.brettspielwelt.develop.Main;

public class GameReceiver {
	BaseBoard board;
	String name="Dummy";
	
	public GameReceiver(BaseBoard b){
		board=b;
	}
	public void sendDataObject(Data dat){
		board.getData(dat.typ, dat);
	}
	public Data makeData(int id, String target){
		Data ret=new Data();
		ret.typ=id;
		return ret;
	}
	public int getPlaying(){
		return board.spielerNr;
	}
	public String getPName(){
		return name;
	}
	public void setName(String n) {
		name=n;
	}
	public void sendSound(String pack, int s){
		//sendDataObject(makeData());
	}
}
