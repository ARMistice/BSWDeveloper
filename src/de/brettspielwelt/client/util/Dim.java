package de.brettspielwelt.client.util;

import java.awt.image.RGBImageFilter;

public class Dim extends RGBImageFilter {
   int dunkler;
                             
   public Dim(int col) {
  		dunkler=col<<8;
      canFilterIndexColorModel = true;//bei gif auf false stellen... jpg on
   }
   
	public int filterRGB(int x, int y, int orginal) {
		int wert1=orginal&0x000000ff;
		if((wert1-=(dunkler>>8))<0) wert1=0;
		int wert2=(orginal&0x0000ff00)>>8;
		if((wert2-=(dunkler>>8))<0) wert2=0;
		int wert3=(orginal&0x00ff0000)>>16;
		if((wert3-=(dunkler>>8))<0) wert3=0;
		int wert4 = orginal&0xff000000;
      return (wert1)|(wert2<<8)|(wert3<<16)|wert4;      
  }
}
