package in.co.madhur.wunderlistsync;

public class SyncState
{
	private WunderSyncState state;
	private int itemsToSync;
	private int syncedItems;
	
	public SyncState(WunderSyncState state)
	{
		this.state=state;
	}

}
