package de.brettspielwelt.client.boards.games;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import de.Data;
import de.brettspielwelt.client.boards.BaseBoard;
import de.brettspielwelt.develop.HTMLWrapper;


public class PlainInformer implements Serializable{
	public int currentPlayer=0,anzMitSpieler=0,startSpieler=0;
	transient public GameReceiver[] spieler;
	public transient List<BaseBoard> boards=new ArrayList<>();
	
	public PlainInformer(){
	}
	
	public String getSpielClient() {
		return "game";
	}
	public void setBoard(BaseBoard b) {
		boards.add(b);
		anzMitSpieler=boards.size();
		spieler=new GameReceiver[8];
		for(int i=0; i<boards.size(); i++) {
			spieler[i]=new GameReceiver(boards.get(i));
			spieler[i].setName("Player-"+i);
		}
	}
	public void removeBoard(BaseBoard b) {
		boards.remove(b);
		anzMitSpieler=boards.size();
		spieler=new GameReceiver[8];
		for(int i=0; i<boards.size(); i++) {
			spieler[i]=new GameReceiver(boards.get(i));
			spieler[i].setName("Player-"+i);
		}
	}

	boolean runningGame=true;
	
	public boolean isRunningGame() {
		return true;
	}
	public void okayTimer(int curPl) {
	}
	public void shareTimer(int curPl) {
	}
	public void shareTimer() {
	}

	public void spielEnde() {
	}
	public void spielStart() {
	}
	public void reset() {
		
	}
	public void doAnswer(int command,int plNr,Data dat){
	}
	
	public int rnd(int w){
		return(int)(Math.random()*w);
	}
	public void sendAnim(int[] arr){
		for(BaseBoard b: boards) {
			Data dat = makeData(703, "Board");
			dat.v.addElement(arr);
			b.getData(703, dat);
		}
	}
	public void sendAnim(GameReceiver g, int[] arr){
		Data dat = makeData(703, "Board");
		dat.v.addElement(arr);
		g.board.getData(703, dat);
	}
	
	public void sendSound(String pack, int nr){
		//board.playSound(nr);
	}
	public Data makeData(int id, String target){
		Data ret=new Data();
		ret.typ=id;
		return ret;
	}
	public void sendDataObject(Data dat){
		for(BaseBoard b: boards)
			b.getData(dat.typ, dat);
	}
	public GameReceiver[] getReceiverArray(){
		GameReceiver[] spielerA=new GameReceiver[boards.size()];
		for(int i=0; i<boards.size(); i++)
			spielerA[i]=spieler[i];

		return spielerA;
	}
	
	
	public void sendComplete() {
		sendGameStatus();
		sendBoard();
	}

	public void sendComplete(GameReceiver pl) {
		sendGameStatus(pl);
		sendBoard(pl);
	}
	public void sendBoard(){
		for (GameReceiver playerInfo : getReceiverArray()) {
			sendBoard(playerInfo);
		}
	}
	
	public void sendBoard(GameReceiver st){
		sendBoard(st);
	}
	
	public void sendGameStatus(GameReceiver st) {
	}

	public void sendGameStatus() {
		for (GameReceiver playerInfo : getReceiverArray()) {
			sendGameStatus(playerInfo);
		}
	}

	// -------------- Achievements ----------------------
	Hashtable<String, Integer>[] achievements;

	// Simple Management of Achievements ... 
	// gain score for achievements while playing..
	
	protected void gainAchievement(int sid, String id, int steps) {
		if(steps==0) return;
		if(achievements==null) clearAchievements();
		if(achievements[sid]==null)
			achievements[sid]=new Hashtable<String, Integer>();
		Integer t=(Integer) achievements[sid].get(id);
		if(t!=null) t+=steps;
		else t=steps;
		achievements[sid].put(id,t);
	}

	public void setMaxAchievement(int sid, String id, int val){
		if(getAchievementProgress(sid, id)<val) gainAchievement(sid, id, val-getAchievementProgress(sid, id));
	}

	public int getAchievementProgress(int sid, String id) {
		if(achievements==null || achievements[sid]==null) return 0;
		Integer t=(Integer) achievements[sid].get(id);
		if(t!=null) return t;
		return 0;
	}
	
	public void clearAchievements(){
		achievements=new Hashtable[anzMitSpieler];
	}
	
	// Write in the end
	public void writeAchievements(){

		clearAchievements();
	}

}
