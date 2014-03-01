package in.co.madhur.wunderlistsync.database;

import in.co.madhur.wunderlistsync.database.WunderSyncContract.WunderTasks;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WunderSyncDB extends SQLiteOpenHelper
{

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "wundersync.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_WUNDERTASK = "CREATE TABLE "
			+ WunderTasks.TABLE_NAME + " (" + WunderTasks._ID
			+ " TEXT PRIMARY KEY," + WunderTasks.LIST_ID + TEXT_TYPE
			+ COMMA_SEP + WunderTasks.TITLE + TEXT_TYPE + COMMA_SEP
			+ WunderTasks.OWNER_ID + TEXT_TYPE+ COMMA_SEP + WunderTasks.CREATED_AT 
			+TEXT_TYPE+
			 COMMA_SEP + WunderTasks.CREATED_BY_ID + TEXT_TYPE+ COMMA_SEP + WunderTasks.UPDATED_AT+ TEXT_TYPE+COMMA_SEP
			+ WunderTasks.STARRED +TEXT_TYPE+ COMMA_SEP + WunderTasks.COMPLETED_AT +TEXT_TYPE
			+ COMMA_SEP + WunderTasks.COMPLETED_BY_ID + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ WunderTasks.TABLE_NAME;

	public WunderSyncDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_WUNDERTASK);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}
}
