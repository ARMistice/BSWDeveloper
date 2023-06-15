package de.brettspielwelt.client.boards;

import de.Data;

public class BoardTimer
{
	static final int TIMER_TOTAL     = -3;		// Zur Abfrage: Gesamtzeit des Spiels
	static final int TIMER_SUSPENDED = -3;		// Als Zustand: Unterbrochen
	static final int TIMER_SHARED    = -2;		// Zustand/Abfrage: Parallelspiel
	static final int TIMER_IDLE      = -1;		// Zustand/Abfrage: Wartezeit

	public long[] spielMillis = new long[8];	// Gesamtzeit, für die jeder Spieler dran war
	public long[] warteMillis = new long[8];	// Summe der Zeiten, die jeder Spieler für erste Reaktion brauchte
	public long idleMillis = 0L;				// Gesamtzeit, in der nichts passiert
	public long sharedMillis = 0L;				// Gesamtzeit, in der die Spieler gleichzeitig dran sind
	public long totalSharedMillis = 0L;			// Summe der Einzelzeiten aller Spieler, in denen die Spieler gleichzeitig dran sind
	public long totalMillis = 0L;				// Gesamtzeit des bisherigen Spiels
	protected int warteSpieler = TIMER_SUSPENDED;		// -3 = suspended // -2 = shared (alle) // -1 = idle // 0 - (anz-1) = Spieler // größer = unterbrochen
	protected int lastWarteSpieler = TIMER_SUSPENDED;	// Zustand vor der Unterbrechung
	public boolean[] warteShared = new boolean[8];		// Strichliste, er bei SHARED noch dran ist
	protected boolean spielerWechsel = true;			// Bei letzten Check wurde der aktive Spieler gewechselt
	protected long lastWechsel = System.currentTimeMillis();	// Letzter Wechsel des aktiven Spielers
	protected long lastAnswer = System.currentTimeMillis();		// Letzter Abrechnungszeitpunkt

	protected long[] allMillis = new long[21];			// Alle Zeiten in einem (8 Spieler/IDLE/SHARED idle(Y/N) + TOTAL)   
	protected boolean[] laufendeUhr = new boolean[21];	// Angabe, ob Zeit läuft (8 Spieler/IDLE/SHARED idle(Y/N) + TOTAL)   

	public BaseBoard board;								// Informer des gemessenen Spiels
	public int anzMitSpieler = 0;						// Anzahl der Mitspieler
	public int iAmId = -1;								// Nummer dieses Spielers
	public String[] spielerName = new String[8];		// Spielernamen

	public BoardTimer()
	{
	}

	public void construct(BaseBoard board)
	{
		this.board = board;
	}

	public void getData(int typ, Data dat)
	{
		int c = 0;

		switch (typ)
		{
		case 4801:
			reset((String[])dat.v.elementAt(c++));
			iAmId = ((Integer)dat.v.elementAt(c++)).intValue();
			break;
		case 4802:
			spielMillis = (long[])dat.v.elementAt(c++);
			warteMillis = (long[])dat.v.elementAt(c++);
			long[] millis = (long[])dat.v.elementAt(c++);
			idleMillis = millis[0];
			sharedMillis = millis[1];
			totalSharedMillis = millis[2];
			totalMillis = millis[3];
			lastWechsel = System.currentTimeMillis() - millis[4];
			lastAnswer = System.currentTimeMillis() - millis[5];
			int[] warte = (int[])dat.v.elementAt(c++);
			warteSpieler = warte[0];
			lastWarteSpieler = warte[1];
			spielerWechsel = (warte[2] != 0);
			warteShared = (boolean[])dat.v.elementAt(c++);
			calcAllMillis();	// Rohdaten in Datenvektoren umwandeln
			break;
		}
	}

	protected void calcAllMillis()
	{
		allMillis[0] = totalMillis;							laufendeUhr[0] = (warteSpieler != TIMER_SUSPENDED);
		allMillis[1] = sharedMillis;						laufendeUhr[1] = (warteSpieler == TIMER_SHARED);
		allMillis[2] = idleMillis;							laufendeUhr[2] = (warteSpieler == TIMER_IDLE) || (spielerWechsel && (warteSpieler >= 0));
		allMillis[11] = sharedMillis + idleMillis;			laufendeUhr[11] = (warteSpieler == TIMER_SHARED) || (spielerWechsel && (warteSpieler >= 0));
		allMillis[12] = idleMillis + totalSharedMillis;		laufendeUhr[12] = (warteSpieler == TIMER_IDLE);
		for (int i = 0; i < anzMitSpieler; i++)
		{
			allMillis[12] -= warteMillis[i];
			allMillis[i - TIMER_TOTAL] = spielMillis[i];		laufendeUhr[i - TIMER_TOTAL] = (warteSpieler == i);
			allMillis[i + 10 - TIMER_TOTAL] = warteMillis[i];	laufendeUhr[i + 10 - TIMER_TOTAL] = (warteSpieler == i) && ((warteSpieler == TIMER_SHARED) ? warteShared[i] : spielerWechsel);
		}
		for (int i = anzMitSpieler; i < 8; i++)
		{
			allMillis[i - TIMER_TOTAL] = 0L;					laufendeUhr[i - TIMER_TOTAL] = false;
			allMillis[i + 10 - TIMER_TOTAL] = 0L;				laufendeUhr[i + 10 - TIMER_TOTAL] = false;
		}
	}

	// Start des Spiels: Initialisierung...
	protected void reset(String[] spieler)
	{
		System.out.println("BoardTimer.reset()");
		anzMitSpieler = spieler.length;
		for (int i = 0; i < anzMitSpieler; i++)
		{
			spielerName[i] = spieler[i];
		}
		for (int i = 0; i < spielMillis.length; i++)
		{
			spielMillis[i] = 0L;
			warteMillis[i] = 0L;
		}
		idleMillis = 0L;
		sharedMillis = 0L;
		totalSharedMillis = 0L;
		totalMillis = 0L;
		warteSpieler = TIMER_IDLE;
		spielerWechsel = true;
		lastWechsel = System.currentTimeMillis();
		lastAnswer = System.currentTimeMillis();
	}

	// Aktuelle Spieldauer eines Spielers oder Zustands...
	public long getMillis(int player)
	{
		boolean idle = (player >= 8);

		return getMillis(idle ? player - 10 : player, idle);
	}

	public long getMillis(int player, boolean idle)
	{
		int index = player + (idle ? 10 : 0) - TIMER_TOTAL;

		return allMillis[index] + (laufendeUhr[index] ? System.currentTimeMillis() - lastAnswer : 0L);
	}

	public long getSecs(int player)
	{
		return (getMillis(player) + 500L) / 1000L;
	}

	public long getSecs(int player, boolean idle)
	{
		return (getMillis(player + 10) + 500L) / 1000L;
	}

	// Aktuelle Spieldauer des aktuellen Spielers oder Zustands...
	public long getCurrentMillis()
	{
		return getMillis(warteSpieler);
	}

	public long getCurrentSecs()
	{
		return (getCurrentMillis() + 500L) / 1000L;
	}
}

//-------------------------------------------------------------------
// 
// $Log: BoardTimer.java,v $
// Revision 1.2  2013/07/28 03:53:25  slc
// timer.lastWechsel - Zeit seit letztem Wechsel des aktiven Spielers
// Localized
//
// Revision 1.1  2007/10/01 06:00:27  slc
// Basis-Klasse zur Kapselung der Zeitmessung des Boards und Synchronisation mit dem Server
//
//-------------------------------------------------------------------
