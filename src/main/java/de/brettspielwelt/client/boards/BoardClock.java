//-------------------------------------------------------------------
// 
// BoardClock - Basis-Klasse zum Stoppen der Spielzeit mittels BoardTimer
// 
// @author SLC
// 
// $Revision: 1.1 $
// 
//-------------------------------------------------------------------
// Bugs:
//-------------------------------------------------------------------
// Ideen und fehlende Features:
//-------------------------------------------------------------------

package de.brettspielwelt.client.boards;

public class BoardClock implements Runnable
{
	public BaseBoard board;		// Informer des gemessenen Spiels
	public BoardTimer timer;	// Mit dem Server (SpielTimer) synchronisierter Timer

	protected long limit = 0;
	protected long[] limits = null;

	protected boolean quit = false;
	protected Thread runThread = null;

	public BoardClock()
	{
	}

	public void setParam(long param)
	{
	}

	public void construct(BaseBoard board)
	{
		this.board = board;
		this.timer = board.timer;

		start();
	}

	public void destruct()
	{
		stop();
	}

	public void start()
	{
		quit = false;

		if (runThread == null)
		{
			runThread = new Thread((Runnable)this);
			runThread.setName(board.getPanelName() + "-Clock");
			runThread.start();

			System.out.println("Started RunThread " + runThread.getName());
		}
	}

	public void stop()
	{
		if (runThread != null)
		{
			System.out.println("Stopping RunThread " + runThread.getName() + "...");

			runThread = null;
			quit = true;
		}
	}

	public void run()
	{
		initInstance();

		while (! quit)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
			if (timer.warteSpieler != BoardTimer.TIMER_SUSPENDED)
			{
				ticker();
			}
		}

		exitInstance();
	}

	protected void initInstance()
	{
	}

	protected void exitInstance()
	{
	}

	protected void ticker()
	{
		show();
	}

	// Aktuelle Spieldauer eines Spielers oder Zustands...
	public long getMillis(int player)
	{
		boolean idle = (player >= 8);

		return getMillis(idle ? player - 10 : player, idle);
	}

	public long getMillis(int player, boolean idle)
	{
		return timer.getMillis(player, idle);
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
		return timer.getCurrentMillis();
	}

	public long getCurrentSecs()
	{
		return (getCurrentMillis() + 500L) / 1000L;
	}

	// Alle Spieldauern im Chat ausgeben...
	public void show()
	{
/***
		if ((timer.warteSpieler >= 0) && (timer.warteSpieler != timer.iAmId))
		{
			return;
		}
		String s = "";
		for (int i = 0; i < timer.anzMitSpieler; i++)
		{
			s += ", " + timer.spielerName[i] + " " + getSecs(i) + " (" + getSecs(i, true) + ") sec";
		}
		board.master.sendString("-- Total: " + getSecs(BoardTimer.TIMER_TOTAL) + " (" + getSecs(BoardTimer.TIMER_SHARED, true) + ") sec, Idle: " +
				getSecs(BoardTimer.TIMER_IDLE) + " (" + getSecs(BoardTimer.TIMER_IDLE, true) + ") sec, Shared: " + getSecs(BoardTimer.TIMER_SHARED) + " sec" +
				s + " (active #" + timer.warteSpieler + ")");
***/
	}
}

//-------------------------------------------------------------------
// 
// $Log: BoardClock.java,v $
// Revision 1.1  2007/10/01 05:59:32  slc
// Basis-Klasse zum Überwachen eines Zeitlimits des Boards
//
//-------------------------------------------------------------------
