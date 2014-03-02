package in.co.madhur.wunderlistsync;

public enum WunderSyncState
{
	LOGIN,
	FETCH_WUNDERLIST_TASKS,
	FETCH_GOOGLE_TASKS,
	SYNCING,
	ERROR,
	FINISHED,
	USER_RECOVERABLE_ERROR

}
