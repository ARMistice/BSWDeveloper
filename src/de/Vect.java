package de;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import de.brettspielwelt.tools.IntVector;

/**
 * @author ARMistice
 *
 * Datentypen in byte[] and vice versa. Vect (bedingt kompatibel mit Vector)
 * unterstuetzte Datentypen:
 *  int, boolean, String, Vector, int[], String[], byte[], IntVector, String[][], boolean[], int[][], boolean[][],
 *  long, long[], int[][][], byte[][], Hashtable
 * Es werden keine "null" values akzeptiert. (IntVector ist eine Vector implementation fuer "int"; String ist UTF-8 kodiert.)
 * 
 */

public class Vect
{	// Groesse in Bytes (0: gefolgt von 2 byte size, -1: gefolgt von 3 byte size)
	static int[] size={0, 4,1,0,0,0, 0,-1,0,-1,0, 0,0,8,0,0, 0,-1,0,0,0};
	public int bc=5;
	public byte[] b=new byte[512];
	int sizeE=0;

	public int size(){
		return sizeE;
	}

	public Object getObject(int typ, int si, int wo){
		switch(typ){
		case 1:	// int
			int i=((b[wo++]&255)<<24)|((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
			return new Integer(i);
		case 2:	// boolean
		    if(b[wo++]==0) return Boolean.FALSE;
			else return Boolean.TRUE;
		case 3:	// String
	        int utflen = si;
	        StringBuffer str = new StringBuffer(utflen);
	        int c, char2, char3;
			int count = wo;
		
			while (count < utflen+wo) {
		     	    c = (int) b[count] & 0xff;
			    switch (c >> 4) {
		        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				    count++;
                    str.append((char)c);
			    	break;
		        case 12: case 13:
				    count += 2;
				    char2 = (int) b[count-1];
		            str.append((char)(((c & 0x1F) << 6) | (char2 & 0x3F)));
				    break;
		        case 14:
				    count += 3;
				    char2 = (int) b[count-2];
				    char3 = (int) b[count-1];
	                str.append((char)(((c     & 0x0F) << 12) |
                    	              ((char2 & 0x3F) << 6)  |
                    	              ((char3 & 0x3F) << 0)));
				    break;
				}
			}
	       	//System.out.println("String: "+new String(str));
	        return new String(str);
		case 4:	// Vector
			Vector v=new Vector();
			int anz=((int)(b[wo++]&0xff)<<8)|((int)b[wo++]&0xff);
			for(int j=0; j<anz; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
				if(siv==-1) siv=((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
				v.addElement(getObject(w,siv,wo));
				wo+=siv;
			}
			return v;
		case 5:	// int[]
			int anzi=si/4;
			int[] tb=new int[anzi];
			for(int j=0; j<anzi; j++){
				int in=((b[wo++]&255)<<24)|((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
				tb[j]=in;
			}
			return tb;
		case 6: // String[]
//			int anzs=((int)b[wo++]&0xff);
			int anzs=((int)(b[wo++]&0xff)<<8)|((int)b[wo++]&0xff);
			String[] ts=new String[anzs];
			for(int j=0; j<anzs; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
//				System.out.println("Typ: "+w+" Size:"+siv);
				ts[j]=((String)getObject(w,siv,wo));
				wo+=siv;
			}
			return ts;
		case 7:	// byte[]
			byte[] ta=new byte[si];
			for(int j=0; j<si; j++){
				ta[j]=b[wo++];
			}
			return ta;
		case 8: // IntVector
			IntVector vi=new IntVector();
			for(int j=0; j<si/4; j++){
				int in=((b[wo++]&255)<<24)|((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
				vi.addElement(in);
			}
			return vi;
		case 9: // String[][]
//			int anzss=((int)b[wo++]&0xff);
			int anzss=((int)(b[wo++]&0xff)<<8)|((int)b[wo++]&0xff);
			String[][] tss=new String[anzss][];
			for(int j=0; j<anzss; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
//				System.out.println("Typ: "+w+" Size:"+siv);
				tss[j]=((String[])getObject(w,siv,wo));
				wo+=siv;
			}
			return tss;
		case 10: // boolean[]
			boolean[] tbo=new boolean[si];
			for(int j=0; j<si; j++){
				tbo[j]=b[wo++]>0?true:false;
			}
			return tbo;
		case 11: // int[][]
			int anzii=((int)b[wo++]&0xff);
			int[][] tii=new int[anzii][];
			for(int j=0; j<anzii; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
//				System.out.println("Typ: "+w+" Size:"+siv);
				tii[j]=((int[])getObject(w,siv,wo));
				wo+=siv;
			}
			return tii;
		case 12: // boolean[][]
			int anziib=((int)b[wo++]&0xff);
			boolean[][] tiib=new boolean[anziib][];
			for(int j=0; j<anziib; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
//				System.out.println("Typ: "+w+" Size:"+siv);
				tiib[j]=((boolean[])getObject(w,siv,wo));
				wo+=siv;
			}
			return tiib;
		case 13: // long
			long il=(((long)b[wo++]&255)<<56)|
					(((long)b[wo++]&255)<<48)|
					(((long)b[wo++]&255)<<40)|
					(((long)b[wo++]&255)<<32)|
					(((long)b[wo++]&255)<<24)|
					(((long)b[wo++]&255)<<16)|
					(((long)b[wo++]&255)<< 8)|
					( (long)b[wo++]&255);
			return new Long(il);
		case 14: // long[]
			int anzid=si/8;
			long[] tbd=new long[anzid];
			for(int j=0; j<anzid; j++){
				long in=(((long)b[wo++]&255)<<56)|
						(((long)b[wo++]&255)<<48)|
						(((long)b[wo++]&255)<<40)|
						(((long)b[wo++]&255)<<32)|
						(((long)b[wo++]&255)<<24)|
						(((long)b[wo++]&255)<<16)|
						(((long)b[wo++]&255)<< 8)|
						( (long)b[wo++]&255);
				tbd[j]=in;
			}
			return tbd;
		case 15: // long[][]
			int anziil=((int)b[wo++]&0xff);
			long[][] tiil=new long[anziil][];
			for(int j=0; j<anziil; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
				tiil[j]=((long[])getObject(w,siv,wo));
				wo+=siv;
			}
			return tiil;
		case 16: // int[][][]
//			System.out.println("3 array int");
			int anziir=((int)b[wo++]&0xff);
			int[][][] tiir=new int[anziir][][];
			for(int j=0; j<anziir; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
//				System.out.println("3 array int"+getObject(w,siv,wo));
				tiir[j]=((int[][])getObject(w,siv,wo));
				wo+=siv;
			}
			return tiir;
		case 17: // byte[][]
			int anzbb=((int)b[wo++]&0xff);
			byte[][] tbb=new byte[anzbb][];
			for(int j=0; j<anzbb; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
				if(siv==-1) siv=((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);

								//System.out.println("Typ: "+w+" Size:"+siv);
				tbb[j]=((byte[])getObject(w,siv,wo));
				wo+=siv;
			}
			return tbb;
		case 18: // Hashtable
			Hashtable hash=new Hashtable();
			int anzht=((int)(b[wo++]&0xff)<<8)|((int)b[wo++]&0xff);
			for(int j=0; j<anzht; j++){
				// key
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
				if(siv==-1) siv=((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
				Object key=getObject(w,siv,wo);
				wo+=siv;

				// value
				w=b[wo++];
				siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
				if(siv==-1) siv=((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
				Object value=getObject(w,siv,wo);
				wo+=siv;

				hash.put(key,value);
			}
			return hash;
		case 19: // byte[][][]
//			System.out.println("3 array int");
			int anzbbr=((int)b[wo++]&0xff);
			byte[][][] tbbr=new byte[anzbbr][][];
			for(int j=0; j<anzbbr; j++){
				byte w=b[wo++];
				int siv=((int)size[w]);
				if(siv==0) siv=((b[wo++]&255)<<8)|(b[wo++]&255);
				if(siv==-1) siv=((b[wo++]&255)<<16)|((b[wo++]&255)<<8)|(b[wo++]&255);
//				System.out.println("3 array int"+getObject(w,siv,wo));
				tbbr[j]=((byte[][])getObject(w,siv,wo));
				wo+=siv;
			}
			return tbbr;
		case 20: // IntVector[]
			int anziv = (b[wo++] & 0xff) << 8 | b[wo++] & 0xff;
			IntVector tiv[] = new IntVector[anziv];
			for(int j = 0; j < anziv; j++)
			{
			    byte w = b[wo++];
			    int siv = size[w];
			    if(siv == 0)
			        siv = (b[wo++] & 0xff) << 8 | b[wo++] & 0xff;
			    tiv[j] = (IntVector)getObject(w, siv, wo);
			    wo += siv;
			}
			
			return tiv;
		}
		return null;
	}

	public byte[] decompress() {
		int len=((b[0]&255)<<16)|((b[1]&255)<<8)|(b[2]&255);
		Inflater decompressor = new Inflater();
	    System.err.println("LEN Zip:"+len);
		decompressor.setInput(b,3,len-3);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(len-3);
	    byte[] buf = new byte[1024];
	    while (!decompressor.finished()) {
	        try {
	            int count = decompressor.inflate(buf);
	            bos.write(buf, 0, count);
	        } catch (DataFormatException e) {
	        	e.printStackTrace();
	        }
	    }
	    try {  bos.close(); } catch (IOException e) {}
	    return bos.toByteArray();		
	}

	public byte[] compress() {
		byte[] input = b;
	    Deflater compressor = new Deflater();
	    compressor.setLevel(Deflater.BEST_SPEED);
	    compressor.setInput(input,0,bc);
	    compressor.finish();
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
	    bos.write(0); bos.write(0); bos.write(0); bos.write(0);
	    byte[] buf = new byte[1024];
	    while (!compressor.finished()) {
	        int count = compressor.deflate(buf);
	        bos.write(buf, 0, count);
	    }
	    try { bos.close(); } catch (IOException e) {}
	    byte[] ret=bos.toByteArray();
	    ret[0]=(byte)255;
	    ret[1]=(byte)(((ret.length-1)>>16)&255);
	    ret[2]=(byte)(((ret.length-1)>>8)&255);
	    ret[3]=(byte)(((ret.length-1))&255);
	    System.err.println("Zipping: "+bc+" -> "+ret.length);
	    return ret;
	}

	public int toVector(){
		sizeE=0;
		int c=5;
		int theTyp=((b[3]&255)<<8)|(b[4]&255);
//		System.out.println("BC: "+bc+" typ: "+theTyp);
		while(c<bc){
			byte w=b[c++];
			int si=(int)size[w];
			if(si==0) si=((b[c++]&255)<<8)|(b[c++]&255);
			if(si==-1) si=((b[c++]&255)<<16)|((b[c++]&255)<<8)|(b[c++]&255);
//				System.out.println("Typ: "+w+" Size:"+si);
			sizeE++;
			c+=si;
		}
		return theTyp;
	}

	public void removeAllElements() {
		sizeE=0;
		bc=5;
	}
	
	public void removeElementAt(int i){
//		if (i == sizeE - 1) {
//			sizeE--;
//			return;
//		}
		int c=5,t=0;
//			System.out.println("Removeing element at: "+i+" BC: "+bc);
		while(c<bc){
			int a=c;
			byte w=b[c++];
			int si=((int)size[w]);
			if(si==0) si=((b[c++]&255)<<8)|(b[c++]&255);
			if(si==-1) si=((b[c++]&255)<<16)|((b[c++]&255)<<8)|(b[c++]&255);
//				System.out.println("Typ: "+w+" Size:"+si);
			if(i==t++){
				System.arraycopy(b,c+si,b,a,b.length-si-c);
				bc-=(si+c)-a;
				sizeE--;
				return;
			}
			c+=si;
		}
	}

	public Object elementAt(int i){
		int c=5,t=0;
//			System.out.println("BC: "+bc);
		while(c<bc){
			byte w=b[c++];
			int si=((int)size[w]);
			if(si==0) si=((b[c++]&255)<<8)|(b[c++]&255);
			if(si==-1) si=((b[c++]&255)<<16)|((b[c++]&255)<<8)|(b[c++]&255);
//				System.out.println("Typ: "+w+" Size:"+si);
			if(i==t++){
//				System.out.println("Type: "+getObject(w,si,c));
				return getObject(w,si,c);
			}
			c+=si;
		}
		return null;
	}

	public void addElementAt(Object o, int i){
		sizeE++;
		int sbc=bc;
		int len=putElement(o);
		byte[] tmp=new byte[len];
		System.arraycopy(b, sbc, tmp, 0, len);
		int c=5,t=0;
		while(c<bc){
			int a=c;
			byte w=b[c++];
			int si=((int)size[w]);
			if(si==0) si=((b[c++]&255)<<8)|(b[c++]&255);
			if(si==-1) si=((b[c++]&255)<<16)|((b[c++]&255)<<8)|(b[c++]&255);
	//			System.out.println("Typ: "+w+" Size:"+si);
			if(i==t++){
				System.arraycopy(b,a,b,a+len,bc-len-a);
				System.arraycopy(tmp,0,b,a,len);
				return;
			}
			c+=si;
		}
	}
	public void addElement(Object o){
		sizeE++;
		putElement(o);
	}
	
	public int putElement(Object o){
		if(o instanceof Integer){
			return putInt(((Integer)o).intValue());
		}
		if(o instanceof Boolean){
			return putBoolean(((Boolean)o).booleanValue());
		}
		if(o instanceof String){
			return putString((String)o);
		}
		if(o instanceof Vector){
			return putVector((Vector)o);
		}
		if(o instanceof Hashtable){
			return putHashtable((Hashtable)o);
		}
		if(o instanceof int[]){
			return putIntArr((int[])o);
		}
		if(o instanceof String[]){
			return putStringArr((String[])o);
		}
		if(o instanceof byte[]){
			return putByteArr((byte[])o);
		}
		if(o instanceof IntVector){
			return putIntVector((IntVector)o);
		}
		if(o instanceof String[][]){
			return putString2Arr((String[][])o);
		}
		if(o instanceof boolean[]){
			return putBoolArr((boolean[])o);
		}
		if(o instanceof int[][]){
			return putInt2Arr((int[][])o);
		}
		if(o instanceof boolean[][]){
			return putBool2Arr((boolean[][])o);
		}
		if(o instanceof Long){
			return putLong(((Long)o).longValue());
		}
		if(o instanceof long[]){
			return putLongArr((long[])o);
		}
		if(o instanceof long[][]){
			return putLong2Arr((long[][])o);
		}
		if(o instanceof int[][][]){
			return putInt3Arr((int[][][])o);
		}
		if(o instanceof byte[][]){
			return putByte2Arr((byte[][])o);
		}
		if(o instanceof byte[][][]){
			return putByte3Arr((byte[][][])o);
		}
        if(o instanceof IntVector[])
            return putIntVectorArr((IntVector[])o);

		return 0;
	}

	public void ensureCapacity(int si){
		if(bc+si>=b.length){
			// Länge verdoppeln, um Anzahl der allocations zu minimieren...
			// der Sender verwendet bc, um zu entscheiden wieviel er wirklich
			// sendet, daher ist das ungefährlich
			int newlen=b.length*2;
			while(bc+si>=newlen)newlen*=2;
			
			byte[] bold=b;
			b=new byte[newlen];
//			System.out.println("Resize: "+newlen);
			System.arraycopy(bold,0,b,0,bc);
		}
	}
	
	public int putInt(int i){
		ensureCapacity(5);
		b[bc++]=1;
		b[bc++]=(byte)((i>>24)&255);
		b[bc++]=(byte)((i>>16)&255);
		b[bc++]=(byte)((i>>8)&255);
		b[bc++]=(byte)(i&255);
		return 5;	
	}

	public int putBoolean(boolean i){
		ensureCapacity(2);
		b[bc++]=2;
		if(i) b[bc++]=1;
		else b[bc++]=0;
		return 2;
	}	

	public int putString(String str){
//		if(str==null) str="null";
		int strlen = str.length();
		int utflen = 0;
	 	char[] charr = new char[strlen];
		int c;

		str.getChars(0, strlen, charr, 0);

		for (int i = 0; i < strlen; i++) {
		    c = charr[i];
		    if ((c >= 0x0001) && (c <= 0x007F)) {
			utflen++;
		    } else if (c > 0x07FF) {
			utflen += 3;
		    } else {
			utflen += 2;
		    }
		}

		if (utflen > 65535)
		    return 0;  // Errorhandling
		ensureCapacity(utflen+3);
		b[bc++]=3;
		b[bc++] = (byte) ((utflen >>> 8) & 0xFF);
		b[bc++] = (byte) ((utflen >>> 0) & 0xFF);
		for (int i = 0; i < strlen; i++) {
		    c = charr[i];
		    if ((c >= 0x0001) && (c <= 0x007F)) {
			b[bc++] = (byte) c;
		    } else if (c > 0x07FF) {
			b[bc++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
			b[bc++] = (byte) (0x80 | ((c >>  6) & 0x3F));
			b[bc++] = (byte) (0x80 | ((c >>  0) & 0x3F));
		    } else {
			b[bc++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
			b[bc++] = (byte) (0x80 | ((c >>  0) & 0x3F));
		    }
		}
		return utflen+3;
	}	

	public int putVector(Vector v){
		ensureCapacity(5);
		b[bc++]=4;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)((v.size()>>8)&255); //Anzahl Elemente
		b[bc++]=(byte)((v.size())&255); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<v.size(); e++)
			siv+=putElement(v.elementAt(e));
		siv+=2;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
		return siv;
	}

	public int putHashtable(Hashtable hash){
		ensureCapacity(5);
		b[bc++]=18;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)((hash.size()>>8)&255); //Anzahl Elemente
		b[bc++]=(byte)((hash.size())&255); //Anzahl Elemente
		int siv=0;
		for(Enumeration e=hash.keys(); e.hasMoreElements(); ){
			Object key=e.nextElement();
			siv+=putElement(key);
			siv+=putElement(hash.get(key));
		}
		siv+=2;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
		return siv;
	}

	public int putBoolArr(boolean[] tmp){
		ensureCapacity(tmp.length+4);
		b[bc++]=10;
		b[bc++]=(byte)((tmp.length>>8)&255);
		b[bc++]=(byte)((tmp.length)&255);
		for(int e=0; e<tmp.length; e++){
			boolean i=tmp[e];
			if(i) b[bc++]=0x01; else b[bc++]=0x00;
		}
		return tmp.length+3;
	}

	public int putIntArr(int[] tmp){
		ensureCapacity(tmp.length*5+4);
		b[bc++]=5;
		b[bc++]=(byte)((tmp.length*4>>8)&255);
		b[bc++]=(byte)((tmp.length*4)&255);
		for(int e=0; e<tmp.length; e++){
			int i=tmp[e];
			b[bc++]=(byte)((i>>24)&255);
			b[bc++]=(byte)((i>>16)&255);
			b[bc++]=(byte)((i>>8)&255);
			b[bc++]=(byte)(i&255);
		}
		return tmp.length*4+3;
	}

	public int putIntVector(IntVector v){
		ensureCapacity(v.size()*4+3);
		b[bc++]=8;
		b[bc++]=(byte)((v.size()*4>>8)&255);
		b[bc++]=(byte)((v.size()*4)&255);
		for(int e=0; e<v.size(); e++){
			int i=v.elementAt(e);
			b[bc++]=(byte)((i>>24)&255);
			b[bc++]=(byte)((i>>16)&255);
			b[bc++]=(byte)((i>>8)&255);
			b[bc++]=(byte)(i&255);
		}
		return v.size()*4+3;
	}

	public int putStringArr(String[] tmp){
		ensureCapacity(5);
		b[bc++]=6;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
//		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		b[bc++]=(byte)((tmp.length>>8)&255); //Anzahl Elemente
		b[bc++]=(byte)((tmp.length)&255); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
//			System.out.println("e= "+e+" str="+tmp[e]);
			siv+=putString(tmp[e]);
		}
		siv+=2;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
//			System.out.println("Siv: "+siv);
		return siv;
	}

	public int putString2Arr(String[][] tmp){
		ensureCapacity(6);
		b[bc++]=9;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);

		b[bc++]=(byte)((tmp.length>>8)&255); //Anzahl Elemente
		b[bc++]=(byte)((tmp.length)&255); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putStringArr(tmp[e]);
		}
		siv+=2;

		b[sip]=(byte)((siv>>16)&255);
		b[sip+1]=(byte)((siv>>8)&255);
		b[sip+2]=(byte)((siv)&255);
		siv+=4;
//			System.out.println("Siv: "+siv);
		return siv;
	}

	public int putByteArr(byte[] tmp){
		ensureCapacity(tmp.length+4);
		b[bc++]=7;
		b[bc++]=(byte)((tmp.length>>16)&255);
		b[bc++]=(byte)((tmp.length>>8)&255);
		b[bc++]=(byte)((tmp.length)&255);
		for(int e=0; e<tmp.length; e++){
			b[bc++]=tmp[e];
		}
		return tmp.length+4;
	}

	public int putByte2Arr(byte[][] tmp){
		ensureCapacity(5);
		b[bc++]=17;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putByteArr(tmp[e]);
		}
		siv+=1;
		b[sip]=(byte)((siv>>16)&255);
		b[sip+1]=(byte)((siv>>8)&255);
		b[sip+2]=(byte)((siv)&255);
		siv+=4;
//			System.out.println("Siv: "+siv);
		return siv;
	}

	public int putInt2Arr(int[][] tmp){
		ensureCapacity(4);
		b[bc++]=11;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putIntArr(tmp[e]);
		}
		siv+=1;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
//			System.out.println("Siv: "+siv);
		return siv;
	}

	public int putBool2Arr(boolean[][] tmp){
		ensureCapacity(4);
		b[bc++]=12;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putBoolArr(tmp[e]);
		}
		siv+=1;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
//			System.out.println("Siv: "+siv);
		return siv;
	}

	public int putLong(long i){
		ensureCapacity(9);
		b[bc++]=13;
		b[bc++]=(byte)((i>>56)&255);
		b[bc++]=(byte)((i>>48)&255);
		b[bc++]=(byte)((i>>40)&255);
		b[bc++]=(byte)((i>>32)&255);
		b[bc++]=(byte)((i>>24)&255);
		b[bc++]=(byte)((i>>16)&255);
		b[bc++]=(byte)((i>>8)&255);
		b[bc++]=(byte)(i&255);
		return 9;	
	}

	public int putLongArr(long[] tmp){
		ensureCapacity(tmp.length*8+4);
		b[bc++]=14;
		b[bc++]=(byte)((tmp.length*8>>8)&255);
		b[bc++]=(byte)((tmp.length*8)&255);
		for(int e=0; e<tmp.length; e++){
			long i=tmp[e];
			b[bc++]=(byte)((i>>56)&255);
			b[bc++]=(byte)((i>>48)&255);
			b[bc++]=(byte)((i>>40)&255);
			b[bc++]=(byte)((i>>32)&255);
			b[bc++]=(byte)((i>>24)&255);
			b[bc++]=(byte)((i>>16)&255);
			b[bc++]=(byte)((i>>8)&255);
			b[bc++]=(byte)(i&255);
		}
		return tmp.length*8+3;
	}

	public int putLong2Arr(long[][] tmp){
		ensureCapacity(4);
		b[bc++]=15;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putLongArr(tmp[e]);
		}
		siv+=1;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
		return siv;
	}

	public int putInt3Arr(int[][][] tmp){
		ensureCapacity(4);
		b[bc++]=16;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putInt2Arr(tmp[e]);
		}
		siv+=1;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
		return siv;
	}
	
	public int putByte3Arr(byte[][][] tmp){
		ensureCapacity(4);
		b[bc++]=19;
		int sip=bc;
		b[bc++]=0; //(byte)((c.length*2>>8)&255);
		b[bc++]=0; //(byte)((c.length*2)&255);
		b[bc++]=(byte)(tmp.length); //Anzahl Elemente
		int siv=0;
		for(int e=0; e<tmp.length; e++){
			siv+=putByte2Arr(tmp[e]);
		}
		siv+=1;
		b[sip]=(byte)((siv>>8)&255);
		b[sip+1]=(byte)((siv)&255);
		siv+=3;
		return siv;
	}
    public int putIntVectorArr(IntVector tmp[])
    {
        ensureCapacity(5);
        b[bc++] = 20;
        int sip = bc;
        b[bc++] = 0;
        b[bc++] = 0;
        b[bc++] = (byte)(tmp.length >> 8 & 0xff);
        b[bc++] = (byte)(tmp.length & 0xff);
        int siv = 0;
        for(int e = 0; e < tmp.length; e++)
            siv += putIntVector(tmp[e]);

        siv += 2;
        b[sip] = (byte)(siv >> 8 & 0xff);
        b[sip + 1] = (byte)(siv & 0xff);
        return siv += 3;
    }

	public void makeData(int ttyp){
		b[3]=(byte)((ttyp>>8)&255);
		b[4]=(byte)((ttyp)&255);
		b[0]=(byte)((bc>>16)&255);
		b[1]=(byte)((bc>>8)&255);
		b[2]=(byte)((bc)&255);
	} 	
}
