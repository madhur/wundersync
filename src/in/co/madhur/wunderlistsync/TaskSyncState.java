package in.co.madhur.wunderlistsync;

public class TaskSyncState
{
	private WunderSyncState state;
	private int itemsToSync;
	private int syncedItems;
	private String errorMessage;
	
	public TaskSyncState(WunderSyncState state)
	{
		this.setState(state);
	}
	
	public TaskSyncState(String errorMessage)
	{
		this.state=WunderSyncState.ERROR;
		this.errorMessage=errorMessage;
		
	}

	public int getItemsToSync()
	{
		return itemsToSync;
	}

	public void setItemsToSync(int itemsToSync)
	{
		this.itemsToSync = itemsToSync;
	}

	public int getSyncedItems()
	{
		return syncedItems;
	}

	public void setSyncedItems(int syncedItems)
	{
		this.syncedItems = syncedItems;
	}

	public WunderSyncState getState()
	{
		return state;
	}

	public void setState(WunderSyncState state)
	{
		this.state = state;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

}
