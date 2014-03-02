package in.co.madhur.wunderlistsync;

import in.co.madhur.wunderlistsync.api.model.WList;

import java.util.List;

public class WListResult
{
	private List<WList> lists;
	private String errorMessage;
	private boolean isError;
	
	public WListResult(List<WList> lists)
	{
		this.lists=lists;
		isError=false;
	}
	
	public WListResult(String message)
	{
		
		this.errorMessage=message;
		isError=true;
	}
	
	public List<WList> getLists()
	{
		return lists;
	}
	public void setLists(List<WList> lists)
	{
		this.lists = lists;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public boolean isError()
	{
		return isError;
	}

	public void setError(boolean isError)
	{
		this.isError = isError;
	}

}
