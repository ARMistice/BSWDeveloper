/*
 * Created on 14.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.brettspielwelt.client.util;
import java.awt.image.*;
/**
 * @author ARMistice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImageBrightFilter extends RGBImageFilter {
	int bright=0;
	int modus=0; //0 additiv, 1 multiplikativ
	
	public ImageBrightFilter(int br) {
	   	bright=br;
	}	 
	
	public ImageBrightFilter(int br, int m) {
	   	bright=br;
	   	modus=m;
	}	
	
	public void setModus(int m){
	    modus=m;
	}
	
	public int getModus(){
	    return modus;
	}
	
	public int filterRGB(int x, int y, int rgb) {
	    int r,g,b,a,m;
	             
		a=(rgb&0xff000000)>>24;
		r=(rgb&0x00ff0000)>>16;
	    g=(rgb&0x0000ff00)>>8;
		b=(rgb&0x000000ff);
			
			
		if(modus==0){
		    r=r+bright>255?255:r+bright;
			g=g+bright>255?255:g+bright;
			b=b+bright>255?255:b+bright;
		}
		if(modus==1){
		    r=r*bright>255?255:r*bright;
			g=g*bright>255?255:g*bright;
			b=b*bright>255?255:b*bright;		    
		}    
	    return ((a)<<24) | ((r)<<16) | ((g)<<8) | (b) ;
	}
}
