package de.brettspielwelt.client.boards;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.Data;
import de.brettspielwelt.client.Client;
import de.brettspielwelt.develop.ClientPanel;
import de.brettspielwelt.develop.Main;
import de.brettspielwelt.shared.tools.JavaVersionChecker;


public class BaseBoard extends JPanel implements ClientPanel,
	MouseListener, MouseMotionListener, KeyListener, FocusListener
{
	private static final long serialVersionUID = -1141657774074854531L;

	public static final int MODE_AUTO = 0;
	public static final int MODE_610  = 1;
	public static final int MODE_915  = 2;
	public static final int MODE_1220 = 3;

	public int spielerNr=0;
	public Client master;
	protected int paintMode = MODE_610;

	protected String panelName;

	protected boolean usesRunThread = false;
	protected boolean quit = false;
	protected Thread runThread = null;

	protected BoardTimer timer = null;	// Hält die Zeitmessungsdaten
	protected BoardClock clock = null;	// Ticker für Zeitoperationen (z.B. Countdown, Anzeige)

	protected boolean nioState = false;	// Messages über NIO-Loop verarbeiten
	protected long nioTime;
	protected long nioSleepTime = 0L;	// Wartezustand in der NIO-Loop

	Vector<Data> nioData = new Vector<Data>();	// Warteschlage der NIO-Loop

	String prefix="game";
	public Properties props=new Properties();
	
//	public int getWidth() {
//		return 1220;
//	}
//	public int getHeight() {
//		return 784;
//	}
	public Image getImageLocal(String name){
		BufferedImage bi=null;
		try {
			bi = ImageIO.read(new File("assets/"+name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi;
	}
	public Image getImage(String name){
		return getImageLocal(name);
	}
	
	public Data makeData(int i){
		Data ret=new Data();
		ret.typ=i;
		return ret;
	}
	public void sendDataObject(Data d){
		Main.info.doAnswer(d.typ,spielerNr,d);
	}
	
	public void getBaseSoundPack(){
//		String[] sound=new String[] {"ok.wav","error.wav","chimes.wav","drumroll.wav","fight.wav","what.wav","yipee.wav","ah-ah.wav","dice.wav","swipe.wav","scream.wav","kling.wav","bazbuy.wav","pop.wav","baer.wav","jaeger.wav","ente.wav","open.wav","blabla.wav","neuling.wav"};
//		Prepare.consoleLog("Getting ase sound file: base");
//		String packName="base";
//		if(baseClips==null){
//			baseClips=new Sounds(packName,sound);
//			baseClips.loadS(packName, null);
//		}
	}

	public boolean getSoundPack(String o,String[] sound){
//		Prepare.consoleLog("Getting sound file: "+prefix);
//		String packName=prefix;
//		if(o!=null) packName=o;
//		if(clips==null){
//			//if(packName!=null && packName.equals("base")) clips=Prepare.baseClips;
//			//else{
//				clips=new Sounds(packName,sound);
//				clips.loadS(packName, null);
//			//}
//			//Prepare.audioStore.put(packName, clips);
//		}
		return true;
	}
	
	public void playSound(int id){
//		if(pauseMode==-1)
//			clips.playSound(id);
	}
	
	
	public Font getFontLocal(String name, double scale){
		// http://stackoverflow.com/questions/2756575/drawing-text-to-canvas-with-font-face-does-not-work-at-the-first-time
//		return new Font("Helvetica",0,20);
		String def=(String)props.get(name);
		String[] di=def.split(",");
		String lName=di[0].replace(' ', '_');
		return new Font(di[0],Integer.parseInt(di[1]),(int)(Integer.parseInt(di[2])*scale)>>0);
	}
	public Font getFontLocal(String name){
//		return new Font("Helvetica",0,20);
		String def=(String)props.get(name);
		String[] di=def.split(",");
		String lName=di[0].replace(' ', '_');
		return new Font(di[0],Integer.parseInt(di[1]),(int)(Integer.parseInt(di[2]))>>0);
	}
	public String[] getStringsLocal(String name, int anz){
		String[] ret = new String[anz];
		for(int i=0; i<ret.length; i++){
			ret[i]=(String)props.get(name+""+i);
			if(ret[i]==null) ret[i]="";
		}
		return ret;
	}
	public String[] getStringsLocal(String name){
		int i=0;
		while(props.get(name+""+(i++))!=null);
		return getStringsLocal(name, i);
	}
	public String getStringLocal(String name){
		return (String)props.get(name);
	}
	public String getStringLocal(String name, String def){
		if(props.get(name)==null) return def;
		return (String)props.get(name);
	}
	public int getIntLocal(String name){
		return Integer.parseInt((String)props.get(name));
	}
	public String[] getBubbleArray(String s)
	{
		if (s == null)
			return null;
		return s.split("\\|");
	}

	protected String[][] getBubbleArray(String[] stringsLocal) {
		String[][] ret = new String[stringsLocal.length][];
		for(int i=0; i<ret.length; i++){
			ret[i]=stringsLocal[i].split("\\|");
		}
		return ret;
	}

	public void registerFont(String fontName) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("assets/"+fontName+"")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	public BaseBoard()
	{
		panelName = getPanelNameByClassName(getClass().getName());
		usesRunThread = (this instanceof Runnable);
		setDoubleBuffered(false);
	}

	protected String getPanelNameByClassName(String className)
	{
		String name = className;
		int lastIndex = name.length() - 1;

		if ((name.charAt(lastIndex) == '1') || (name.charAt(lastIndex) == '2'))
		{
			// HQ-Boards mit Suffix 1 und 2...
			name = name.substring(0, lastIndex);
		}
		if (name.endsWith("Board"))
		{
			// Standard- und Spielboards...
			name = name.substring(name.lastIndexOf('.') + 1, name.length() - 5);
		}
		else if (name.endsWith("RoomDisplay"))
		{
			// Raumansichten...
			name = name.substring(name.lastIndexOf('.') + 1, name.length() - 11);
		}
		else if (name.indexOf("client.boards.single") != -1)
		{
			// SU-Spiele haben kein Suffix...
			name = name.substring(name.lastIndexOf('.') + 1, name.length());
		}

		return name;
	}

	public String getPanelName()
	{
		return panelName;
	}

	public void init()
	{
//		if (usesRunThread)
//		{
//			start();
//		}

		repaint();
	}

	public void init(String params)
	{
//		if (usesRunThread)
//		{
//			start();
//		}

		repaint();
	}

	public void construct(Client master)
	{
		this.master = master;
	//	callBuildGUI();
	}

	public boolean usesRunThread() {
		return usesRunThread;
	}

	public boolean isRunning() {
		return usesRunThread && !quit && runThread != null;
	}

	public void start()
	{
		quit = false;
		if (usesRunThread)
		{

			if (runThread == null)
			{
				runThread = new Thread((Runnable)this);
				runThread.setName(panelName);
				runThread.start();

				System.out.println("Started RunThread " + runThread.getName());
			}
		}
	}

	public void stop()
	{
		if (clock != null)
		{
			clock.destruct();
			clock = null;
		}
		timer = null;

		if (usesRunThread)
		{
			if (runThread != null)
			{
				System.out.println("Stopping RunThread " + runThread.getName() + "...");

				runThread = null;
				quit = true;
			}
		}
	}

	// Direkte Verarbeitung von Board-Messages...
	public void getData(int typ, Data dat)
	{
		// Achtung: Nur per super.getData() aufrufen, wenn ansonsten keine Verarbeitung erfolgt!
		nioData.addElement(dat);
		handleNioData(0L);
	}

	// Verarbeitung von Board-Messages über NIO-Loop...
	public void getNioData(int typ, Data dat)
	{
	}

	// Verarbeitung von Timer-Messages...
	public void getTimerData(int typ, Data dat)
	{
		if (nioState)
		{
			// NIO-Behandlung aktiv!
			nioData.addElement(dat);
			return;
		}

		switch (typ)
		{
		case 4800:
			setClassNames(dat);
			break;

		case 4999:
			System.err.println("Warning: Used sendSleep() without NIO loop!");
			try
			{
				Thread.sleep(((Integer)dat.v.elementAt(0)).intValue());
			}
			catch (Exception ex) {}
			break;

		default:
			if (timer != null)
			{
				timer.getData(typ, dat);
			}
			break;
		}
	}

	public synchronized Data getFirstNioData(){
		if(nioData.isEmpty()) return null;
		return nioData.remove(0);
	}

	public void handleNioData(long msTakt) {
		try {
			nioState = true;
			/*if(nioData.isEmpty())*/
			Thread.sleep(msTakt);
		} catch (Exception ex) {
		}
		handleNioData();
	}


	public void handleNioData() {
		nioState = true;
		long nioElapse;
		if (nioTime == 0) {
			nioElapse = 0;
			nioTime = System.nanoTime();
		} else {
			long now = System.nanoTime();
			nioElapse = (now - nioTime) / 1000000;
			if (nioElapse < 0) {
				nioElapse = 0;
			}
			nioTime = now;
		}
		if (nioSleepTime > 0) {
			nioSleepTime -= nioElapse;
		}
		if(nioSleepTime<=0L){
			nioSleepTime=0L;

			Data dat=null;
			while((dat=getFirstNioData())!=null){ // !nioData.isEmpty()){
				//Data dat=(Data)nioData.elementAt(0);
				//nioData.removeElementAt(0);

				if((dat.typ==4999)&&(dat.v.size()==1)){
					nioSleepTime+=((Integer)dat.v.elementAt(0)).intValue();
					break;
				}else if(dat.typ==4800){
					setClassNames(dat);
				}else if(dat.typ>4800){
					if(timer!=null){
						timer.getData(dat.typ,dat);
					}
				}else{
					getNioData(dat.typ,dat);
					//callgetNioDataThreadSave(dat.typ,dat);
				}
			}
		}
	}

	/**
	 * Bestimmen der Timer- und Clock-Klasse im Board (default: BaseTimer, BaseClock)...
	 * @param dat
	 */
	protected void setClassNames(Data dat)
	{
		String timerClass = "de.brettspielwelt.client.boards." + (String)dat.v.elementAt(0);
		String clockClass = "de.brettspielwelt.client.boards." + (String)dat.v.elementAt(1);
		Long param = (Long)dat.v.elementAt(2);

		if ((timer != null) && ! timerClass.equals(timer.getClass().getName()))
		{
			timer = null;
		}
		if (timer == null)
		{
			try
			{
				timer = (BoardTimer)Class.forName(timerClass).newInstance();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		if (timer != null)
		{
			timer.construct(this);
			timer.getData(4800, dat);
		}
		if ((clock != null) && ! clockClass.equals(clock.getClass().getName()))
		{
			clock.destruct();
			clock = null;
		}
		if (clock == null)
		{
			try
			{
				clock = (BoardClock)Class.forName(clockClass).newInstance();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		if (clock != null)
		{
			clock.setParam(param.longValue());
			clock.construct(this);
		}
	}

	public boolean releaseFocus(int x, int y)
	{
		return true;  // Normalerweise brauch ich keinen Focus!
	}

	// FIXME Messe im orginal weg
//	public void repaint(){
//		if(master!=null && master instanceof MultiClient){
//			System.err.println("Repaint() "+master);
//			((MultiClient)master).bb.repaint();
//		}else super.repaint();
//	}

	@Override
	@Deprecated
	// Das nutzen wir nicht mehr!!!! Wir nutzen KeyEvent
	public final boolean keyDown(Event e, int k) {
		return false;
	}

	@Override
	@Deprecated
	// Das nutzen wir nicht mehr!!!! Wir nutzen KeyEvent
	public final boolean keyUp(Event evt, int key) {
		return false;
	}

	public int rnd(int x)
	{
		return((int)(Math.random()*x));
	}

	public String getHelp()
	{
		return panelName.toLowerCase();
	}

	public int mode(){ return paintMode; }

	@Deprecated
	public int getPaintMode(){ return paintMode; }

	public void setPaintMode(int p){ paintMode=p; }

	// changeMode wird aufgerufen, wenn sich die Groesse des Fensters aendert.
	public int changeMode()
	{
//		System.out.println("Auto mode == on / myWidth="+myWidth());
		return MODE_1220;
	}

	// Moechte ich aus der alten Klasse etwas mitnehmen?
	// Beste Moeglichkeit ist, diese Funktion zu ueberschreiben...
	public void changeBoard(BaseBoard old) {}

	public boolean hasMode(int mo)
	{
		if(mo==MODE_610) return true;
		return false;
	}

	/* Dimension stuff.	*/
//	public Dimension getMaximumSize()
//	{
//		return new Dimension(master.topWidth,master.topHeight);
//	}
//
//	public Dimension getMinimumSize()
//	{
//		return new Dimension(master.topWidth,master.topHeight);
//	}
//
	@Override
	public Dimension getPreferredSize()
	{
		if(master!=null) {
		master.topWidth=1220;
		master.topHeight=784;
		}
		return new Dimension(1220,784);
	}

	// FIXME messe
//	public int getWidth(){
//		return 1080; }
//	public int getHeight() { return 694; }
//	public int myWidth(){
//		return 1080; }
//	public int myHeight() { return 694; }

	public int myWidth(){ return master.topWidth; }
	public int myHeight(){ return master.topHeight; }


//
//	public Dimension minimumSize()
//	{
//		return new Dimension(master.topWidth,master.topHeight);
//	}
//
//	public Dimension preferredSize()
//	{
//		return new Dimension(master.topWidth,master.topHeight);
//	}

	/**
	 * @deprecated Ersetzt durch mouseDragged(MouseEvent e)
	 */
	@Deprecated
	@Override
	// Das nutzen wir nicht mehr!!!! Wir nutzen KeyEvent
	public final boolean mouseDrag(Event ev, int x, int y) {
		return false;
	}

	/**
	 * @deprecated Ersetzt durch mouseMoved(MouseEvent e)
	 */
	@Deprecated
	@Override
	// Das nutzen wir nicht mehr!!!! Wir nutzen KeyEvent
	public final boolean mouseMove(Event ev, int x, int y) {
		return false;
	}

	/**
	 * @deprecated Ersetzt durch mousePressed(MouseEvent e)
	 */
	@Deprecated
	@Override
	// Das nutzen wir nicht mehr!!!! Wir nutzen MouseEvent
	public final boolean mouseDown(Event ev, int x, int y) {
		return false;
	}

	@Override
	@Deprecated
	// Das nutzen wir nicht mehr!!!! Wir nutzen MouseEvent
	public final boolean mouseEnter(Event evt, int x, int y) {
		return false;
	}

	@Override
	@Deprecated
	// Das nutzen wir nicht mehr!!!! Wir nutzen MouseEvent
	public final boolean mouseExit(Event evt, int x, int y) {
		return false;
	}

	@Override
	@Deprecated
	// Das nutzen wir nicht mehr!!!! Wir nutzen FocusEvent
	public final boolean gotFocus(Event evt, Object what) {
		return false;
	}

	@Override
	@Deprecated
	// Das nutzen wir nicht mehr!!!! Wir nutzen FocusEvent
	public final boolean lostFocus(Event evt, Object what) {
		return false;
	}

	/**
	 * @deprecated Ersetzt durch mouseReleased(MouseEvent e)
	 */
	@Deprecated
	@Override
	// Das nutzen wir nicht mehr!!!! Wir nutzen KeyEvent
	public final boolean mouseUp(Event ev, int x, int y) {
		return false;
	}

	public boolean isRightClick(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			return true;
		}
		if (e.isMetaDown()) {           // TODO - das ist ne Altlast. Bei Gelegenheit mal unter verschiedenen Java Versionen testen und dann entfernen
										// TODO - dafür vielleiht lieber eine Maustasten Emulation für Mac einbauen
			return true;
		}
		return false;
	}

	/**
	 * Entferne generierte Key-Modifier.
	 *
	 * Abhängig von der Java Version werden zusätzliche Key-Modifier generiert. Zum Beispiel
	 * bei Jave <=8 für eine rechte Maustaste die Meta Key. Filtere diese Modifier heraus.
	 * Dadurch können zwar Modifier "verloren" gehen, weil diese nicht unterscheidbar sind,
	 * aber besser als zu viele.
	 *
	 * @param event Ein MouseEvent
	 * @return Ein int mit der echten Modifier Mask
	 */
	public int getRealModifierMask(MouseEvent event) {
		JavaVersionChecker.JavaVersion version = JavaVersionChecker.jvmVersion();
		
		int modifiers = event.getModifiersEx();

		// Windows macht aus AltGr ein AltGr + Alt + Ctrl
		if ((modifiers & MouseEvent.ALT_GRAPH_DOWN_MASK) != 0) {
			modifiers &= ~MouseEvent.ALT_DOWN_MASK;
			modifiers &= ~MouseEvent.CTRL_DOWN_MASK;
		}

		// Java Versionen unter 9 machen aus rechter und mittlerer Maustaste
		// ein Meta und ein Alt
		if (version.getMajor() < 9) {
			if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
				modifiers &= ~MouseEvent.META_DOWN_MASK;
			}
			if (((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0)) {
				modifiers &= ~MouseEvent.ALT_DOWN_MASK;
			}
		}
		return modifiers;
	}

	public void drawBorderdString(Graphics backG, Color col1, Color col2, String str, int x, int y)
	{
		backG.setColor(col2);
		backG.drawString(str,x+1,y+1);
		backG.drawString(str,x-1,y-1);
		backG.drawString(str,x-1,y+1);
		backG.drawString(str,x+1,y-1);
		backG.drawString(str,x+1,y);
		backG.drawString(str,x-1,y);
		backG.drawString(str,x,y+1);
		backG.drawString(str,x,y-1);
		backG.setColor(col1);
		backG.drawString(str,x,y);
	}

	// die x und y Koordinaten im Folgenden bezeichnen den Beginn der
	// Baseline, auf die der String plaziert wird
	public void drawClippedString(Graphics g, String s, int x, int y, int width, int height)
	{
		Shape orig_s = g.getClip();
		g.setClip(x, y-g.getFontMetrics().getAscent(), width, g.getFontMetrics().getAscent()+g.getFontMetrics().getDescent());
		g.drawString(s,x,y);
		g.setClip(orig_s);
	}

	// die Rechteck r im Folgenden beschreibt den Clip-Bereich, der
	// noch um 2 Pixel nach unten verschoben wird.
	// Die Baseline des Strings wird in der linken unteren Ecke des
	// ursprünglich angegebenen Rechtecks plaziert
	public void drawClippedString(Graphics g, String s, Rectangle r)
	{
		drawClippedString(g, s, r.x, r.y+r.height, r.width, r.height);
	}

	public void drawClippedString(Graphics g, String s, int x, int y, int width, int height, Color c)
	{
		Color orig_c = g.getColor();
		g.setColor(c);
		drawClippedString(g, s, x, y, width, height);
		g.setColor(orig_c);
	}

	public void drawClippedString(Graphics g, String s, Rectangle r, Color c)
	{
		drawClippedString(g, s, r.x, r.y+r.height, r.width, r.height, c);
	}

	public void drawSizedString(Graphics g, String text, int x, int y, int w, boolean center)
	{
		while(g.getFontMetrics().stringWidth(text)>w)
			text=text.substring(0,text.length()-4)+"..";
		g.drawString(text,x-(center?g.getFontMetrics().stringWidth(text)/2:0),y);
	}

	public void drawSizedString(Graphics g, String text, int x, int y, int w)
	{
		drawSizedString(g,text,x,y,w,false);
	}

	public void drawSizedBorderdString(Graphics backG, Color col1, Color col2, String str, int x, int y, int w, boolean center)
	{
		while(backG.getFontMetrics().stringWidth(str)>w && str.length() >= 4)
			str=str.substring(0,str.length()-4)+"..";
		x=x-(center?backG.getFontMetrics().stringWidth(str)/2:0);
		backG.setColor(col2);
		backG.drawString(str,x+1,y+1);
		backG.drawString(str,x-1,y-1);
		backG.drawString(str,x-1,y+1);
		backG.drawString(str,x+1,y-1);
		backG.drawString(str,x+1,y);
		backG.drawString(str,x-1,y);
		backG.drawString(str,x,y+1);
		backG.drawString(str,x,y-1);
		backG.setColor(col1);
		backG.drawString(str,x,y);

	}
	// Neu damit man auch zentriert das machen kann....
	public void drawSizedBorderdString(Graphics backG, Color col1, Color col2, String str, int x, int y, int w)
	{
		drawSizedBorderdString(backG,col1,col2,str,x,y,w, false);
	}


	public void drawRoundedString(Graphics2D g, String str, int x, int y, int r) {
		FontMetrics fm=g.getFontMetrics();
		int w=fm.stringWidth(str);
		int l=0;
		double theR=(r);//winkel;
		double u=theR*2*Math.PI;
	//	g.drawLine(x-w/2, y, x+w/2, y);
		
		for(int i=0; i<str.length(); i++) {
			AffineTransform save=g.getTransform();
			int cw=fm.stringWidth(str.substring(i,i+1));
			g.translate(x, y+r);
			g.rotate((l+cw/2-w/2)*Math.PI*(w/u)*2/w);
			g.translate(0, -theR);
			g.drawString(""+str.substring(i,i+1), -cw/2,0);
			l+=cw;
			g.setTransform(save);
		}
	}

	Color shadow = null;
	public Color getShadowColor()
	{
		if(shadow==null){
			try { shadow=new Color(0,0,0,64); }catch(Throwable t) { shadow=new Color(0,0,0); }
		}
		return shadow;
	}

	public void drawBubbleString(Graphics g, String bubbleText, int x, int y)
	{
		if(bubbleText!=null && bubbleText.length()>0){
			int bw=g.getFontMetrics().stringWidth(bubbleText)+4;
			int bh=g.getFontMetrics().getHeight()+4;
			int ba=g.getFontMetrics().getAscent()+3;
			if (x-2+bw > getWidth()) {
				x = getWidth() - bw;
			}
			g.setColor(Color.white);
			g.fillRect(x-2,y-ba,bw,bh);
			g.setColor(Color.black);
			g.drawString(bubbleText,x,y);
			g.drawRect(x-2,y-ba,bw,bh);
		}
	}

	public void drawShadow(Graphics g, int x, int y, int w, int h)
	{
		if (shadow == null) {
			try { shadow = new Color(0,0,0,64); } catch (Throwable t) { shadow = new Color(0,0,0); }
		}
		if (master.standAlone){
			g.setColor(shadow);
			for(int i=0; i<3; i++) {
				g.fillRoundRect(x-i, y-i, w+i*2, h+i*2, 2, 2);
			}
		} else {
			g.setColor(shadow);
			g.fillRect(x, y, w, h);
		}
	}

	@Override
	public void paint(Graphics g)
	{
//		if (paintOffScreen(getOffScreenGraphics(g))) {
//			master.drawRest(g);
//		}
		super.paint(g);
	}

	/**
	 * Unterstützt Zeichnen auf offScreenGraphics. Diese wird auch schon übergeben,
	 * muß also nicht mit getOffScreenGraphics() gehohlt werden.
	 * <p>Boards die MODE_AUTO haben sollten paintComponent(Graphics g) überschreiben.</p>
	 */
	protected boolean paintOffScreen(Graphics osg) {
		return false;
	}

	Graphics offScreen=null;
	public BufferedImage oimg;
	
	public Graphics getOffScreenGraphics() {
		if(offScreen==null) {
			oimg=new BufferedImage(1220,784,BufferedImage.TYPE_INT_ARGB);
			offScreen=oimg.getGraphics();
		}
		return offScreen;
	}

	protected Graphics getOffScreenGraphics(Graphics g) {
		Rectangle clip = g.getClipBounds();
		Graphics offG = getOffScreenGraphics();
//		if (clip != null) {
//			offG.setClip(new Rectangle(
//					master.convert(clip.x),
//					master.convert(clip.y),
//					master.convert(clip.width) + 2,
//					master.convert(clip.height) + 2
//			));
//		}
		return offG;
	}

//	@Override
//	protected void paintComponent(Graphics g) {
////		super.paintComponent(g);
//	}

	// Für den Aufruf durch EventDistributor
	public void focusGained(FocusEvent e) {}
	public void focusLost(FocusEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	/**
	 * Wird aufgerufen, wenn Java Version >= 1.4 ist.
	 *
	 * @param e Ist eigentlich ein MouseWheelEvent wurde aber
	 * wegen Kompatibilität mit Java Version < 1.4 in ein MouseEvent
	 * gecastet.
	 *
	 * @param units ist positiv wenn das Scrollrad nach unten
	 * bewegt wurde und negativ wenn es nach oben bewegt
	 * wurde. Die Anzahl der Units ist die im Betriebssystem
	 * eingestellte.
	 */
	public void mouseWheelMoved(MouseEvent e, int units) {}

	/**
	 * Skaliert das übergebene Bild um den angegebenen Skalierungsfaktor
	 * @param img Das zu skalierende Bild
	 * @param faktor Der Faktor um den das Bild skaliert werden soll
	 * @return Das skalierte Bild
	 */
	public Image getScaledInstance(Image img, double faktor) {
		return getScaledInstance(img, faktor, faktor);
	}

	/**
	 * Skaliert das übergebene Bild um die angegebenen Skalierungsfaktoren
	 * @param img Das zu skalierende Bild
	 * @param faktorX Der Faktor um den das Bild in der Breite skaliert werden soll
	 * @param faktorY Der Faktor um den das Bild in der Höhe skaliert werden soll
	 * @return Das skalierte Bild
	 */
	public Image getScaledInstance(Image img, double faktorX, double faktorY) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		int targetWidth =(int)(w*faktorX);
		int targetHeight =(int)(h*faktorY);
		return getScaledInstance(img, w, h, targetWidth, targetHeight);
	}

	/**
	 * Skaliert das übergebene Bild auf die angegebene Größe
	 * @param img Das zu skalierende Bild
	 * @param targetWidth Die Breite des skalierten Bildes
	 * @param targetHeight Die Höhe des skalierten Bildes
	 * @return Das skalierte Bild
	 */
	public Image getScaledInstance(Image img, int targetWidth, int targetHeight) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		return getScaledInstance(img, w, h, targetWidth, targetHeight);
	}

	/**
	 * Skaliert das übergebene Bild auf die angegebene Größe, wobei davon ausgegangen wird,
	 * daß das übergebene Bild die angegebene Größe hat.
	 * @param img Das zu skalierende Bild
	 * @param origWidth Die Breite des original Bildes
	 * @param origHeight Die Höhe des original Bildes
	 * @param targetWidth Die Breite des skalierten Bildes
	 * @param targetHeight Die Höhe des skalierten Bildes
	 * @return Das skalierte Bild
	 */
	public Image getScaledInstance(Image img, int origWidth, int origHeight, int targetWidth, int targetHeight) {
		return scaleImage(img,origWidth,origHeight,targetWidth,targetHeight);
	}

	/**
	 * Die echte Image skalierungs methode
	 * @param img
	 * @param origWidth
	 * @param origHeight
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static Image scaleImage(Image img, int origWidth, int origHeight, int targetWidth, int targetHeight) {
		Image ret = img;
		int w = origWidth < targetWidth ? targetWidth : origWidth;
		int h = origHeight < targetHeight ? targetHeight : origHeight;
		do {
			if (w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
			if (h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	public long getMemoryUsage()
	{
		return 10L*1024L*1024L;	// 10 MB
	}

	private boolean isSuspended = false;
	private Thread suspendAnimator = null;
	private final Insets suspendInsets = new Insets(0, 0, 0, 0);
	public void drawSuspendWindow(Graphics g, boolean isSuspended) {
		drawSuspendWindow(g, isSuspended, null, null);
	}
	public void drawSuspendWindow(Graphics g, boolean isSuspended, Color col) {
		drawSuspendWindow(g, isSuspended, col, null);
	}
	public void drawSuspendWindow(Graphics g, boolean isSuspended, String font) {
		drawSuspendWindow(g, isSuspended, null, font);
	}
	public void drawSuspendWindow(Graphics g, boolean isSuspended, Color col, String font) {
		this.isSuspended = isSuspended;
		String suspended = null;
		int w = 0;
		if (isSuspended) {
			if (font == null) {
				font = "Helvetica";
			}
			g.setFont(new Font(font, 0, (int)(suspendCo(32))));
			suspended = "Suspend"; //master.getLocalString("Game suspended!");
			w = g.getFontMetrics().stringWidth(suspended);

			suspendInsets.left = (int)(suspendCo(610) - w / 2) / 2;
			suspendInsets.right = (int)suspendCo(1220) - suspendInsets.left;
			suspendInsets.top = (int)(suspendCo(384) - suspendCo(100));
			suspendInsets.bottom = (int)(suspendCo(384) + suspendCo(100));
		}
		if (suspendAnimator == null && isSuspended) {
			suspendAnimator = new Thread(new Runnable() {
				public void run() {
					System.err.println("Start " + getPanelName() + " Suspend-Animator Thread");
					while ((!usesRunThread || !quit) && BaseBoard.this.isSuspended) {
						int sW = (int)suspendCo(10);
						repaint(suspendInsets.left - sW, suspendInsets.top - sW, suspendInsets.right - suspendInsets.left + 2 * sW, suspendInsets.bottom - suspendInsets.top + 2 * sW);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					}
					suspendAnimator = null;
					System.err.println("Stop " + getPanelName() + " Suspend-Animator Thread");
				}
			}, getPanelName() + " Suspend-Animator");
			suspendAnimator.start();
		}
		if (isSuspended) {
			Graphics2D g2 = (Graphics2D)g;
			int e = (int)suspendCo(20);
			int l = suspendInsets.left;
			int r = suspendInsets.right;
			int t = suspendInsets.top;
			int b = suspendInsets.bottom;
			int[] pathX = new int[] {l+e, r-e, r, r, r-e, l+e, l, l, l+e};
			int[] pathY = new int[] {t, t, t+e, b-e, b, b, b-e, t+e, t};
			g2.setStroke(new BasicStroke(suspendCo(10)));
			g2.setColor(Color.black);
			g2.fillPolygon(pathX, pathY, pathX.length);
			float f = 1f / 4000 * (int)(System.currentTimeMillis() % 4000);
			float w2 = suspendCo(80);
			g2.setPaint(new GradientPaint(w2 * f * 4, 0, Color.white, 1.4f * w2 + w2 * f * 4, w2, Color.gray, true));
			g2.drawPolygon(pathX, pathY, pathX.length);
			Color c = col == null ? Color.RED : col;
			if (System.currentTimeMillis() % 2000 > 1000) {
				c = c.darker();
			}
			drawBorderdString(g2, c, c.darker(), suspended, (int)(suspendCo(610) - w / 2), (int)(suspendCo(384) + g2.getFontMetrics().getAscent() / 2 - g2.getFontMetrics().getDescent()));
		}
	}
	protected float suspendCo(float co) {
		return co * getWidth() / 1220;
	}

	// Folgende Funktionen werden nur aufgerufen, wenn das Board SubCompPanel implementiert.
	public void buildGUI() {}
	public void updateGUI() {}
	public void getDataThreadSave(int typ, Data dat) {}
	public void getNioDataThreadSave(int typ, Data dat) {}

	private final int[] callState = new int[4];

//	public void callBuildGUI() {
//		if (this instanceof SubCompPanel && (callState[0] == 0 || callState[0] == 1))
//			callState[0] = invokeAndWait("buildGUI", new Class[0], new Object[0]);
//	}
//
//	public void callupdateGUI() {
//		if (this instanceof SubCompPanel && (callState[1] == 0 || callState[1] == 1))
//			callState[1] = invokeAndWait("updateGUI", new Class[0], new Object[0]);
//	}
//
//	public void callgetDataThreadSave(int typ, Data dat) {
//		if (this instanceof SubCompPanel && (callState[2] == 0 || callState[2] == 1))
//			callState[2] = invokeAndWait("getDataThreadSave", new Class[] {int.class, Data.class}, new Object[] {new Integer(typ), dat});
//	}
//
//	public void callgetNioDataThreadSave(int typ, Data dat) {
//		if (this instanceof SubCompPanel && (callState[3] == 0 || callState[3] == 1))
//			callState[3] = invokeAndWait("getNioDataThreadSave", new Class[] {int.class, Data.class}, new Object[] {new Integer(typ), dat});
//	}
//
	public int invokeAndWait(String method, Class<?>[] paramClass, final Object[] paramObject) {
		int state = 0;
		try {
			final Method m = getClass().getMethod(method, paramClass);

			state = m.getDeclaringClass() == getClass() ? 1 : 2;

			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							m.invoke(BaseBoard.this, paramObject);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				state = -2;
				e.printStackTrace();
			}
		} catch (Exception e) {
			state = -1;
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public void repaint() {
			super.repaint();
	}

	private void repaintMultiClient() {
			super.repaint();
	}

	protected String getParameter(String key) {
		return key; //master.med.getParameter(key);
	}
}
