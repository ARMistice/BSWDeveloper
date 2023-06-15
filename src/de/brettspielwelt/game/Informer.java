package de.brettspielwelt.game;

import java.io.Serializable;

import de.Data;
import de.brettspielwelt.client.boards.games.GameReceiver;
import de.brettspielwelt.client.boards.games.PlainInformer;

public class Informer extends PlainInformer implements Serializable{

	int phase=0; 

	int[] score=new int[0];
	int[] platz=new int[0];
	int[] punkte=new int[6];
	

	public Informer(){
		baseInit();
	}
	
	// ----------------------- Init and Starting of Game: reset() / spielStart()  
	public void spielStart() {
		baseInit();
		
		phase=1;
		currentPlayer=(currentPlayer+anzMitSpieler-1)%anzMitSpieler;
		sendBoard();

		super.spielStart();
	}
	
	@Override
	public void reset() {
		baseInit();
		super.reset();
	}

	public void baseInit(){
		punkte=new int[4];
		platz=new int[0];
		score=new int[0];
		phase=0;
	}

	// ------------- Game End ---------------------------
	
	
	@Override
	public void spielEnde() {
		phase=15;

		calcScorePlatz();

		sendGameStatus();
		sendBoard();

		super.spielEnde();
	}

	private void calcScorePlatz() {
		int[] tie=new int[anzMitSpieler];
		score=new int[anzMitSpieler];
		platz=new int[anzMitSpieler];

		for (int u=0; u<anzMitSpieler; u++) {
			platz[u]=1;
			score[u]=1;
			tie[u]=1;  // Tiebreaker 
		}
		
		for (int u=0; u<anzMitSpieler-1; u++) {
			for (int v=u+1; v<anzMitSpieler; v++) {
				if (score[u] < score[v]) {
					platz[u]++;
				} else if (score[u] < score[v]) {
					platz[v]++;
				}
			}
		}

		for(int u=0; u<anzMitSpieler-1; u++) {
			for(int v=u+1; v<anzMitSpieler; v++) {
				if(score[u]==score[v]) {
					if(tie[v]<tie[u]){
						platz[v]++;
					}else{ if(tie[u]<tie[v])
						platz[u]++;
					}
				}
			}
		}
	}
	
	// --------------- Input received from the Boards -----------------

	@Override
	public void doAnswer(int command,int pl,Data dat){
		switch(command){
		case 700:
			execAction(pl,((Integer)dat.v.elementAt(0)).intValue());
			break;
		}
	}

	private void execAction(int curPl, int action) {
		int act=action>>28&7;
		if(!isRunningGame()) return;

		if(currentPlayer==curPl) {
			if(phase==0) {
			}
			sendBoard();
		}
	}


	// --------------  Sending Stuff  --------------------- 
	public void sendAnim(int sp, int[] anim) {
		if(spieler[sp]!=null)
			sendAnim(spieler[sp],anim);
	}
	
	public int[] appendAnim(int[] arr, int wh, int fr1, int fr2, int to1, int to2) {
		int[] ret;
		int le=arr==null?0:arr.length;
		if(arr!=null) {
			ret=new int[arr.length+5];
			System.arraycopy(arr,0,ret,0,arr.length);
		} else ret=new int[5];
		ret[le]=wh;
		ret[le+1]=fr1;
		ret[le+2]=fr2;
		ret[le+3]=to1;
		ret[le+4]=to2;
		return ret;
	}

	public void sendBoard(){
		for (GameReceiver playerInfo : getReceiverArray()) {
			sendBoard(playerInfo);
		}
	}
	public void sendBoard(int[] anim){
		for (GameReceiver playerInfo : getReceiverArray()) {
			sendBoard(playerInfo,anim);
		}
	}
	
	public void sendBoard(GameReceiver st){
		sendBoard(st,null);
	}
	
	// ------------------ The informations for all the Boards connected ---------------
	
	public void sendBoard(GameReceiver st, int[] anim){
		int id=st.getPlaying();
		
		Data dat=st.makeData(700,getSpielClient());
		dat.v.addElement(new Integer(anzMitSpieler));
		dat.v.addElement(new Integer(st.getPlaying()));
		dat.v.addElement(new Integer(phase));
		dat.v.addElement(new Integer(currentPlayer));
		dat.v.addElement(new Integer(startSpieler));
		
		if(anim!=null)
			dat.v.addElement(anim);
		else
			dat.v.addElement(new int[0]);
		st.sendDataObject(dat);
		sendGameStatus(st);
	}

	public void sendGameStatus(GameReceiver st) {
		Data dat=st.makeData(702,getSpielClient());

		for(int i=0; i<4; i++) {
			if(spieler[i]!=null) {
				dat.v.addElement(spieler[i].getPName());
			} else {
				dat.v.addElement("");
			}
		}
		st.sendDataObject(dat);
	}

}
