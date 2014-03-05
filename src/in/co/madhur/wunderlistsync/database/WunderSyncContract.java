package in.co.madhur.wunderlistsync.database;

import android.provider.BaseColumns;

public final class WunderSyncContract
{

	public WunderSyncContract()
	{
	}

	/* Inner class that defines the table contents */
	
	public static abstract class WunderUser implements BaseColumns
	{
		public static final String TABLE_NAME = "wunderuser";
		
		public static final String EMAIL="email";
		
	}
	
	public static abstract class GoogleUser implements BaseColumns
	{
		public static final String TABLE_NAME="googleuser";
		public static final String EMAIL="email";
		
		
		
	}
	
	
	public static abstract class WunderTasks implements BaseColumns
	{
		public static final String TABLE_NAME = "wundertasks";
		public static final String OLD_TABLE_NAME="wundertasks_old";
		
		public static final String ID = "id";
		public static final String LIST_ID = "list_id";
		public static final String TITLE = "title";
		public static final String OWNER_ID = "owner_id";
		public static final String CREATED_AT = "created_at";
		public static final String CREATED_BY_ID = "created_by_id";
		public static final String UPDATED_AT = "updated_at";
		public static final String STARRED = "starred";
		public static final String COMPLETED_AT = "completed_at";
		public static final String COMPLETED_BY_ID = "completed_by_id";
		public static final String DELETED_AT = "deleted_at";
	}

	public static abstract class GoogleTasks implements BaseColumns
	{
		public static final String TABLE_NAME = "googletasks";
		public static final String OLD_TABLE_NAME="googletasks_old";
		
		public static final String ID = "id";
		public static final String WUNDERTASK_ID = "wundertask_id";  // add this column
		public static final String TITLE = "title";
		public static final String STATUS = "status";
		public static final String DUE = "due";
		public static final String COMPLETED = "completed";
		public static final String DELETED = "deleted";
		public static final String UPDATED = "updated";
		public static final String NOTES = "notes";

	}
	
	public static abstract class AllWLists implements BaseColumns
	{
		public static final String TABLE_NAME = "wunderlists";
		public static final String OLD_TABLE_NAME = "wunderlists_old";
		
		public static final String ID = "id";
		public static final String TITLE = "title";
		public static final String OWNER_ID = "owner_id";
		public static final String CREATED_AT = "created_at";
		public static final String UPDATED_AT = "updated_at";
		// later update this, cehck if default value can be set
		public static final String ISSYNCED = "issynced";
		// add these columns
		public static final String GOOGLE_LIST_ID="google_list_id";
	}

	
}
