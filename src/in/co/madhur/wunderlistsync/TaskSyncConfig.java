package in.co.madhur.wunderlistsync;

import in.co.madhur.wunderlistsync.api.model.WList;

import java.util.Date;
import java.util.List;

public class TaskSyncConfig
{

	private boolean isOneWay;
	private boolean isManual;
	private Date lastSyncDate;
	private String username;
	private String password;
	private String token;
	private String googleAccount;
	private String[] selectedListIds;
	
	public TaskSyncConfig(boolean isOneWay, boolean isManual, Date lastSyncDate)
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


	public String getUsername()
	{
		return username;
	}


	public void setUsername(String username)
	{
		this.username = username;
	}


	public String getPassword()
	{
		return password;
	}


	public void setPassword(String password)
	{
		this.password = password;
	}


	public String getToken()
	{
		return token;
	}


	public void setToken(String token)
	{
		this.token = token;
	}


	public String getGoogleAccount()
	{
		return googleAccount;
	}


	public void setGoogleAccount(String googleAccount)
	{
		this.googleAccount = googleAccount;
	}


	public String[] getSelectedListIds()
	{
		return selectedListIds;
	}


	public void setSelectedListIds(String[] selectedListIds)
	{
		this.selectedListIds = selectedListIds;
	}


}
