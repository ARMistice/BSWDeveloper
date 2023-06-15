package de.brettspielwelt.develop;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

import de.brettspielwelt.client.boards.BaseBoard;
import de.brettspielwelt.client.util.Dim;
import de.brettspielwelt.client.util.ImageBrightFilter;
import de.brettspielwelt.client.util.ImageColorFilter;
import de.brettspielwelt.client.util.MaskImage;

public abstract class HTMLWrapper extends BaseBoard implements ClientPanel, Runnable{

	public HTMLWrapper() {
		// der getter, der bisher überschrieben wurde, wude von gar niemandem benutzt
		usesRunThread = false;
	}

	MediaTracker media;
	public Image brightenImage(Image image, int i) {
		return createImage(new FilteredImageSource(image.getSource(),new ImageBrightFilter(i) ) );
	}

	protected Image dimImage(Image image, int i) {
		return createImage(new FilteredImageSource(image.getSource(),new Dim(i) ) );
	}

	public Image colorateImage(Image image, int col){
		return createImage(new FilteredImageSource(image.getSource(),new ImageColorFilter(col) ) );
	}

	public Image colorateImage(Image image, int col, int level){
		ImageColorFilter filter = new ImageColorFilter(col);
		filter.setLevel(level);
		return createImage(new FilteredImageSource(image.getSource(),filter ) );
	}

	public Image cropImage(Image image, int x, int y, int w, int h){
		ImageFilter cropFilter0 = new CropImageFilter(x,y,w,h);
		return createImage(new FilteredImageSource(image.getSource(),cropFilter0 ) );
	}

	public Image maskImage(Image image, Image mask) {
		return MaskImage.maskImage(image, mask);
	}

	public Image getImageLocal(String name){
		if(media==null) media=new MediaTracker(this);
		Image a=super.getImageLocal(name);
		media.addImage(a, 1);
		try {
			media.waitForID(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		media.removeImage(a);

		return a;
	}

	public Image createImage(ImageProducer producer) {
		if(media==null) media=new MediaTracker(this);
		Image a=super.createImage(producer);
		media.addImage(a, 1);
		try {
			media.waitForID(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		media.removeImage(a);
		return a;
	}


		List<Composite> savComp=new ArrayList<Composite>();
	List<Shape> savClip=new ArrayList<Shape>();
	List<AffineTransform> savTrans=new ArrayList<AffineTransform>();
	
	public void save(Graphics gi){
		Graphics2D g=(Graphics2D) gi;
		savComp.add(g.getComposite());
		savClip.add(g.getClip());
		savTrans.add(g.getTransform());
	}
	
	public void restore(Graphics gi){
		Graphics2D g=(Graphics2D) gi;
		if(savComp.size()>0) {
			g.setComposite(savComp.remove(savComp.size()-1));
			g.setTransform(savTrans.remove(savTrans.size()-1));
			g.setClip(savClip.remove(savClip.size()-1));
		}
	}

	Thread ticki=null;
	
	public void start(){
		super.start();
		startT(this);
	}
	public void stop(){
		super.stop();
		ticki=null;
		quit=true;
	}
	
	public void init(){
		init(0,null);
		init(1,null);
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}
	
	public abstract void init(int lev,Runnable run);
	public abstract void run();
	public abstract void paintp(Graphics g);

	protected boolean localInit(){
		init();
		return true;
	}
	
	public void startT(final Runnable that){
		if(ticki==null && getTickMillis() != 0) {
			ticki=new Thread(new Runnable() {
				public void run() {
					long lastTick;
					while(!quit){
						lastTick = System.nanoTime();
						that.run();
						long delay = getTickMillis() - (System.nanoTime() - lastTick) / 1000000;
						try {
							if (delay > 0) {
								Thread.sleep(delay);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			ticki.start();
		}
	}

	public int getTickMillis() {
		return 30;
	}


	private Graphics resizeG;
	public BufferedImage resizeImg;
	public Graphics getOffScreenGraphics(){
		return resizeG;
	}


	int cw=1220, ch=784;
	public void setCanvasSize(int w, int h) {
		cw=w; ch=h;
		resizeG=null;
	}
	
	public static boolean portraitMode=false;                                   ;
	
	public int getHeight() {
		return getWidth()*784/1220;
	}
	public void paint(Graphics g) {
//		Client.med.getParameter("GameFormat");
		if(resizeG==null){
			resizeImg=new BufferedImage(cw,ch,1);
			resizeG=resizeImg.getGraphics();
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		paintp(resizeG);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if(!portraitMode) {
			double s = (double)getWidth() / cw;
			g2.scale(s, s);
			g2.drawImage(resizeImg, 0,0, null);
			//master.drawRest(g2);
			g2.scale(-s, -s);

		}else {
//		// für Portrait coffee
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(resizeImg, 0,0, cw*getHeight()/1300,getHeight(), null);
			g.setColor(Color.white);
			g.drawRect(getWidth()-200,10,190,50);
			g.drawString("   Notch", getWidth()-200, 40);
			g.drawRect(getWidth()-200,110,190,50);
			g.drawString("   16:9", getWidth()-200, 140);
			g.drawRect(getWidth()-200,210,190,50);
			g.drawString("   Pad", getWidth()-200, 240);
		}
	}

	public void mousePressed(MouseEvent mouseEvent) {
		if(portraitMode) {
			int x = mouseEvent.getX();
			int y = mouseEvent.getY();
			if(x>getWidth()-200) {
				int yp=y/100;
				int[] sizeW = { 600, 729, 975 };

				if (yp < sizeW.length)
					setCanvasSize(sizeW[yp], 1300);
				if(yp>=sizeW.length) {
					portraitMode=false;
				}
				mouseEvent.consume();
				repaint();
				return;
			}
		}
		super.mousePressed(mouseEvent);
	}
	
	public void clearRect(Graphics gt, int w, int h) {
		((Graphics2D)gt).setComposite(AlphaComposite.Clear);
		gt.setColor(new Color(255,0,0,0));
		gt.fillRect(0, 0, w,h);
		((Graphics2D)gt).setComposite(AlphaComposite.SrcOver);
	}

	public Rectangle getTutorialPlace(String s) {
		return null;
	}

	public boolean hasMode(int mo){
		return mo==MODE_610||mo==MODE_915||mo==MODE_1220;
	}
	
}
