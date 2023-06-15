package de.brettspielwelt.client.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

public class MaskImage {

    public static Image maskImage(Image image, Image mask) {
        int picSizex = image.getWidth(null);
        int picSizey = image.getHeight(null);
        int[] pixelskSchatten= buildArray(image,picSizex,picSizey);
        int[] pixelsNext= buildArray(mask,picSizex,picSizey);// macht aus dem Image ein array und gibt es zurueck
        int [] pixelsDest = new int[picSizex*picSizey];
        BufferedImage destImage = new BufferedImage(picSizex, picSizey, BufferedImage.TYPE_4BYTE_ABGR);

        for(int y=0;y<picSizey;y++){//ok
            for(int x=0;x<picSizex;x++){

                int get=pixelskSchatten[(y*picSizex)+x];
                int test=pixelsNext[(y*picSizex)+x];
                test = (test&0xff000000)>>24;
                if(test!=0) {
                    pixelsDest[(y*picSizex)+x]=get;
                } else {
                    pixelsDest[(y * picSizex) + x] = 0;
                }
            }
        }
        destImage.setRGB(0,0, picSizex, picSizey, pixelsDest, 0, picSizex);
        return destImage;
    }

    private static int[] buildArray(Image pic,int w,int h){
        int [] pixels = new int[w * h];
        int xx=0;
        int yy=0;
        PixelGrabber pg = new PixelGrabber(pic, xx, yy, w, h, pixels, 0, w);//0=startspalte ,w=endspalte;
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return pixels;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
            return pixels;
        }
        return pixels;
    }


}
