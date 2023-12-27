package de.brettspielwelt.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Vector;

import de.Data;
import de.Vect;
import de.brettspielwelt.develop.HTMLWrapper;
import de.brettspielwelt.tools.IntVector;


public class Board extends HTMLWrapper
{
	private Font fontDefault,threeFont,fourFont,fontLarge;
	private String[] localStrings;
	
	private Vector history=new Vector();
	boolean rep=false;
	int animation=0;
	
	int mouseMoveX=0, mouseMoveY=0;
	int mouseDownX=0, mouseDownY=0;

	int[] score=new int[0];
	int[] platz=new int[0];
	
	private String[] spielerName={"","","",""};
	boolean iAmPlaying = false;
	int anzSpieler = 0;
	int iAmId = -1;
	
	int currentPlayer=-1,startSpieler=-1;
	int phase=0,round=0;
	
	Image[] baseImg;
	
	// ------------------------- Init Stuff - Loading Strings for localization / Fonts / Images ---------------------
	@Override
	public void init(int lev, Runnable run) {
		if(lev==0){
			localStrings = getStringsLocal("t", 1);
			
			registerFont("Calligraphic.ttf");
			
			fourFont = getFontLocal("fourFont", 1);
			threeFont = getFontLocal("threeFont", 1);
			fontDefault = getFontLocal("defaultFont", 1);
			fontLarge = getFontLocal("largeFont", 1);
			
			initPics();
		}
		if(lev==1){
			getSoundPack(null, new String[] { "star.wav"});
		}
	}
	
	public void initPics(){
		String[] imageNames = {
				"bg.png","grid.png", "blue.png","red.png"
		};
		
		baseImg = new Image[imageNames.length];
		for(int i=0; i<imageNames.length; i++){
			baseImg[i] = getImageLocal(imageNames[i]);
		}
	}	
	
	// ---------------- Get the Data from the Informer into local variables ---------------
	public void getBoard(Vect v){
		if(v.size()>3) {
			int c=0;
			
			anzSpieler=((Integer)v.elementAt(c++)).intValue();
			iAmId=((Integer)v.elementAt(c++)).intValue();
			phase=((Integer)v.elementAt(c++)).intValue();
			currentPlayer=((Integer)v.elementAt(c++)).intValue();
			startSpieler=((Integer)v.elementAt(c++)).intValue();
		
			int[] anim=((int[])v.elementAt(c++));
			handleAnim(anim);
			
			iAmPlaying=(currentPlayer==iAmId);
			repaint();
		}else {
			if(v.size()==1){
				int[] anim=((int[])v.elementAt(0));
				handleAnim(anim);
			}
		}
	}
	

	private void handleAnim(int[] anim) {
		if(anim==null || anim.length==0) return;
	}
	
	// --------------- Update Loop Triggers every 40ms to have 25 frames/sec ----------------
	public void run() {

		if(history!=null && history.size()>0) { 	// Check if we got new Data from the Informer
			getBoard((Vect)history.elementAt(0));	// And work with it.
			history.removeElementAt(0);
			rep=true;
		}
		
		if(rep || animation>0) {
			rep=false;
			repaint();
		}
		
	}
	
	// -------------- Receiving and Sending Data to and from the Server -----------
	
	public void getNioData(int typ, Data dat){
		int c=0;
		
		switch(typ){	// Put received data from the Server in a queue (history)
		case 700:
			history.addElement(dat.v);
			break;
		case 701:
			history.addElement(dat.v);
			break;
			
		case 702:
			for(int i=0; i<4; i++)
				spielerName[i]=(String)dat.v.elementAt(c++);
			repaint();
			break;
			
		case 703:
			history.addElement(dat.v);
			break;
			
		}
	}
	
	public synchronized void sendAction(int power, int act){
		Data dat=makeData(700);
		dat.v.addElement(new Integer((power<<28)|act));
		sendDataObject(dat);
	}
	public synchronized void sendAction(int power, int act, IntVector marks){
		Data dat=makeData(700);
		dat.v.addElement(new Integer((power<<27)|act));
		dat.v.addElement(marks);
		sendDataObject(dat);
	}
	
	// --------------- Mouse / Touch Actions ------------------
	
	int rco(int v) {   // Dummy Helper for BSWDeveloper to get real coordinates of mouse actions.
		return v*1220/getWidth();
	}
	@Override
	public void mouseMoved(MouseEvent ev) {
		int x = rco(ev.getX());
		int y = rco(ev.getY());
		mouseMoveX=x; mouseMoveY=y;
		
		ev.consume();
	}
	
	public void mouseReleased(MouseEvent ev) {
		int x = rco(ev.getX());
		int y = rco(ev.getY());
	}
	
	public void mousePressed(MouseEvent ev) {
		int x = rco(ev.getX());
		int y = rco(ev.getY());
		mouseDownX=x; mouseDownY=y;
		
		System.err.println(x+"+"+y);
		repaint();
		ev.consume();
	}
	
	@Override
	public void mouseDragged(MouseEvent ev){
		int x = rco(ev.getX());
		int y = rco(ev.getY());
		mouseMoveX=x; mouseMoveY=y;

		repaint();
	}
	
	public boolean immediateDrag(){
		return true;
	}

	// ----------------- Drawing the Board ------------------
	
	public void paintp(Graphics g) {
		
		int iId=iAmId;
		if(iId==-1) iId=0;
		
		try {
			Graphics2D backG=(Graphics2D)getOffScreenGraphics();
			backG.setColor(new Color(0x010039));			
			backG.fillRect(0, 0, 1220, 784);
			backG.setColor(Color.white);
			backG.setFont(fontDefault);

			backG.drawImage(baseImg[0], 0,0, null);
			backG.drawImage(baseImg[1], 200,0, null);
			
			if(phase==0)
				for(int i=0; i<anzSpieler; i++)
					backG.drawString(spielerName[i], 10, 40+i*40);
			
			if(phase==1) {
				backG.drawString("Running game!", 10, 40);
				backG.drawString("CurrentPlayer:"+currentPlayer, 10, 80);
				backG.drawString("My Id:"+iAmId+" - so I am playing: "+iAmPlaying, 10, 120);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	// -------------  Some Helper Stuff  --------------------------
	
	public void ghost(Graphics2D g, int level){
		save(g);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,level/255.0f));
	}
	public void noGhost(Graphics2D g){
		restore(g);
	}

	public int drawImage(Graphics2D g, Image img, int x, int y, int w) {
		int hi=w*img.getHeight(null)/img.getWidth(null);
		double sc=(double)w/(double)img.getWidth(null);
		save(g);
		g.translate(x, y);
		g.scale(sc,sc);
		g.drawImage(img, 0, 0,null);
		restore(g);
		return hi;
	}

	//Standard-Version
	public double ease(double t, double b, double c, double d) {
		c-=b;
		double ts = (t /= d) * t;
		double tc = ts * t;
		return b + c * (tc + -3 * ts + 3 * t);
	}
	
	public double ease2(double t, double b, double c, double d) {
		c-=b;
		double ts = (t /= d) * t;
		double tc = ts * t;
		return b + c * (4 * tc + -9 * ts + 6 * t);
	}
	
	public double ease3(double t, double b, double c, double d) {
		c-=b;
		double ts = (t /= d) * t;
		double tc = ts * t;
		return b + c * (tc * ts);
	}
	
	
}
