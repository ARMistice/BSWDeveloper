package de.brettspielwelt.client.util;

import java.awt.image.RGBImageFilter;

public class ImageColorFilter extends RGBImageFilter {
	int rcol, gcol, bcol;
	int level=256;
	
	boolean reverse=false, transparent=false;

	public ImageColorFilter(int col) {
		rcol=(col>>16)&0xff;
		gcol=(col>>8)&0xff;
		bcol=(col)&0xff;
		canFilterIndexColorModel=true;
	}

	public ImageColorFilter(int col, boolean rev) {
		rcol=(col>>16)&0xff;
		gcol=(col>>8)&0xff;
		bcol=(col)&0xff;
		canFilterIndexColorModel=true;
		reverse=rev;
	}

	public void setLevel(int level){
		this.level=level;
	}
	public void setReverse(boolean rev) {
		reverse=rev;
	}

	public void setTransparent(boolean trans) {
		transparent=trans;
	}

	public int filterRGB(int x, int y, int rgb) {
		if(transparent) return 0;
		int r, g, b, a, m;

		a=(rgb&0xff000000)>>24;
		r=(rgb&0x00ff0000)>>16;
		g=(rgb&0x0000ff00)>>8;
		b=(rgb&0x000000ff);

		if(a==0) return 0;
		m=(r+g+b)/3;
		if(m>=level) return (a<<24)|((r)<<16)|((g)<<8)|(b);
		if(reverse) m=255-m;
		return (a<<24)|((m*rcol>>8)<<16)|((m*gcol>>8)<<8)|(m*bcol>>8);
	}
}
