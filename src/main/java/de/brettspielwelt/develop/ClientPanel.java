package de.brettspielwelt.develop;

import de.Data;
import de.brettspielwelt.client.Client;

public interface ClientPanel{
	public void construct(Client ma);

	public void init(String params);
	public void start();
	public void stop();

	public void getData(int typ, Data dat);
	public void getTimerData(int typ, Data dat);

	public String getHelp();

	public int getPaintMode();
	public void setPaintMode(int pm);
	public boolean hasMode(int pm);
	public int changeMode();
}
