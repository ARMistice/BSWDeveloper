package de.brettspielwelt.tools;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

public class IntVector implements Serializable, Cloneable
{
	static final long serialVersionUID = 9208114285434399546L;

	static Random randGen = new Random();

	private int[] arr;
	private int	size = 0;
	private int inc = 5;

	public IntVector() {
		arr = new int[5];
		size = 0;
	}

	public IntVector(int ig) {
		arr = new int[ig];
		size = 0;
	}

	public IntVector(int ig, int in) {
		arr = new int[ig];
		inc = in;
		size = 0;
	}

	public IntVector(int[] a, int in) {
		arr = new int[a.length];
		System.arraycopy(a, 0, arr, 0, a.length);
		inc = in;
		size = a.length;
	}

	@Override
	public Object clone() {
		IntVector ret = new IntVector(getArray(), inc);
		return ret;
	}

	public static void setRandGen(Random rndG) {
		randGen = rndG;
	}

	public int lastElement() {
		return 	arr[size-1];
	}

	public void addFirst(int o) {
		insertElementAt(o, 0);
	}

	public void addLast(int o) {
		addElement(o);
	}

	public void insertElementAt(int o, int at) {
		if (size + 1 > arr.length) {
			arr = ensureCapacity();
		}
		System.arraycopy(arr, at, arr, at + 1, arr.length - at - 1);
		arr[at] = o;
		size++;
	}

	public void append(IntVector v) {
		int[] arr2 = new int[size + v.size()];
		System.arraycopy(arr, 0, arr2, 0, size);
		int[] app = v.getArray();
		System.arraycopy(app, 0, arr2, size, app.length);
		size += app.length;
		arr = arr2;
	}

	public void addElement(int o) {
		if (size + 1 > arr.length) {
			arr = ensureCapacity();
		}
		arr[size++] = o;
	}

	public int removeFirst() {
		return removeElementAt(0);
	}

	public int removeLast() {
		return removeElementAt(size - 1);
	}

	public int removeElementAt(int s) throws NoSuchElementException{
		if (s >= size) {
			throw new NoSuchElementException("No element: " + s);
		}
		int ret = arr[s];
		System.arraycopy(arr, s + 1, arr, s, arr.length - s - 1);
		size--;
		return ret;
	}

	public boolean contains(int o) {
		for (int i=0; i<size; i++) {
			if (arr[i] == o) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(int[] o) {
		IntVector tmp=(IntVector)clone();
		for (int i=0; i<o.length; i++) {
			if (! tmp.removeElement(o[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(IntVector o) {
		return contains(o.getArray());
	}

	public void setValueAt(int o, int w) {
		arr[o] = w;
	}

	public void setElementAt(int i, int a) {
		arr[a] = i;
	}

	public int countElement(int o) {
		int c = 0;
		for (int i=0; i<size; i++) {
			if (arr[i] == o) {
				c++;
			}
		}
		return c;
	}

	public int countDistinct() {
		int[] arri = getArray();
		for (int i=0; i<size-1; i++) {
			for (int j=i+1; j<size; j++) {
				if (arri[i] > arri[j]) {
					int h = arri[i];
					arri[i] = arri[j];
					arri[j] = h;
				}
			}
		}
		int c = 0;
		if (arri.length > 0) {
			c = 1;
			int last = arri[0];
			for (int i=1; i<size; i++) {
				if (arri[i] != last) {
					c++;
					last = arri[i];
				}
			}
		}
		return c;
	}

	public int indexOf(int o) {
		return firstPos(o);
	}

	public int firstPos(int o) {
		for (int i=0; i<size; i++) {
			if (arr[i] == o) {
				return i;
			}
		}
		return -1;
	}

	public int lastIndexOf(int o) {
		return lastPos(o);
	}

	public int lastPos(int o) {
		for (int i=size-1; i>=0; i--) {
			if (arr[i] == o) {
				return i;
			}
		}
		return -1;
	}

	public int[] ensureCapacity() {
		int[] arr2 = new int[arr.length + inc];
		System.arraycopy(arr, 0, arr2, 0, arr.length);
		return arr2;
	}

	public int capacity() {
		return arr.length;
	}

	public int size() {
		return size;
	}

	public int length() {
		return size;
	}

	public boolean isEmpty() {
		return (size == 0);
	}

	public int elementAt(int s) throws ArrayIndexOutOfBoundsException{
		if (s >= size) {
			throw new ArrayIndexOutOfBoundsException("Array index does not exist: " + s + " (size: " + size+")");
		}
		return arr[s];
	}

	public void removeAllElements() {
		size = 0;
	}

	public boolean removeElement(int o) {
		for (int i=0; i<size; i++) {
			if (arr[i] == o) {
				removeElementAt(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeLastElement(int o) {
		for (int i=size-1; i>=0; i--) {
			if (arr[i] == o) {
				removeElementAt(i);
				return true;
			}
		}
		return false;
	}

	public int[] getArray() {
		int[] ret = new int[size];
		System.arraycopy(arr, 0, ret, 0, size);
		return ret;
	}

	public byte[] getByteArray() {
		byte[] ret = new byte[size];
		for (int i=0; i<size; i++) {
			ret[i] = (byte)arr[i];
		}
		return ret;
	}

	public void sort() {
		for (int i=0; i<size-1; i++) {
			for (int j=i+1; j<size; j++) {
				if (arr[i] > arr[j]) {
					int h = arr[i];
					arr[i] = arr[j];
					arr[j] = h;
				}
			}
		}
	}

	public void sort(Comparator<Integer> comparator) {
		for (int i=0; i<size-1; i++) {
			for (int j=i+1; j<size; j++) {
				if (comparator.compare(arr[i], arr[j]) > 0) {
					int h = arr[i];
					arr[i] = arr[j];
					arr[j] = h;
				}
			}
		}
	}

	public void sortdesc() {
		for (int i=0; i<size-1; i++) {
			for (int j=i+1; j<size; j++) {
				if (arr[i] < arr[j]) {
					int h = arr[i];
					arr[i] = arr[j];
					arr[j] = h;
				}
			}
		}
	}

	// benutzt den globalen Generator
	public void mix() {
	    for (int i=size-1; i>0; i--) {
	    	int j = (int)(randGen.nextDouble() * (i + 1));
			if (i == j) {
				continue;
			}
			int v = arr[i];
			arr[i] = arr[j];
			arr[j] = v;
	    }
	}

	// bekommt explizit den zu benutzenen Generator übergeben
	public void mix(Random randGen) {
		for (int i=size-1; i>0; i--) {
			int j = (int)(randGen.nextDouble() * (i + 1));
			if (i == j) {
				continue;
			}
			int v = arr[i];
			arr[i] = arr[j];
			arr[j] = v;
	    }
	}

	public void cyclicshift(int start, int end, int shift) { //neg shift-> nach links
		int[] arr2 = new int[arr.length];
		System.arraycopy(arr, 0, arr2, 0, arr.length);
		if (end > start) {
			if (shift < 0) {
				System.arraycopy(arr2, start - shift, arr, start, end - start + shift);
				System.arraycopy(arr2, start, arr, end + shift, -shift);
			} else {
				System.arraycopy(arr2, start, arr, start + shift, end - start - shift);
				System.arraycopy(arr2, end - shift, arr, start, shift);
			}
		}
	}

	@Override
	public String toString() {
		String ret = "";
		for (int i=0; i<size; i++) {
			ret += " , " + arr[i];
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof IntVector)) {
			return false;
		}
		IntVector iv = (IntVector)obj;
		if (iv.size != size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (iv.arr[i] != arr[i]) {
				return false;
			}
		}
		return true;
	}
}
