package in.co.madhur.wunderlistsync;

import java.util.Date;

public class SyncConfig
{

	private boolean isOneWay;
	private boolean isManual;
	private Date lastSyncDate;
	
	public SyncConfig(boolean isOneWay, boolean isManual, Date lastSyncDate)
	{
		this.isOneWay=isOneWay;
		this.isManual=isManual;
		this.lastSyncDate=lastSyncDate;
	}
	
	
	public boolean isOneWay()
	{
		return isOneWay;
	}
	public void setOneWay(boolean isOneWay)
	{
		this.isOneWay = isOneWay;
	}
	public boolean isManual()
	{
		return isManual;
	}
	public void setManual(boolean isManual)
	{
		this.isManual = isManual;
	}
	public Date getLastSyncDate()
	{
		return lastSyncDate;
	}
	public void setLastSyncDate(Date lastSyncDate)
	{
		this.lastSyncDate = lastSyncDate;
	}
}
