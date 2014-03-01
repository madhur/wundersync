package in.co.madhur.wunderlistsync;

import java.util.Date;

public class SyncConfig
{

	private boolean isOneWay;
	private boolean isManual;
	private Date lastSyncDate;
	private String username;
	private String password;
	private String token;
	private String googleAccount;
	
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
}
