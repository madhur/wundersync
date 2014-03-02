package in.co.madhur.wunderlistsync.database;

import in.co.madhur.wunderlistsync.database.WunderSyncContract.AllWLists;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.GoogleTasks;
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
	private static final String SQL_CREATE_WUNDERTASK = "CREATE TABLE " + "%s"
			+ " ("
			+ WunderTasks._ID
			+ " TEXT PRIMARY KEY,"
			+ WunderTasks.LIST_ID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.TITLE
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.OWNER_ID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.CREATED_AT
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.CREATED_BY_ID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.UPDATED_AT
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.STARRED
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.COMPLETED_AT
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.COMPLETED_BY_ID + " )";
	
	
	private static final String SQL_CREATE_WUNDERLISTS = "CREATE TABLE " + "%s"
			+ " ("
			+ AllWLists._ID
			+ " TEXT PRIMARY KEY,"
			+ AllWLists.TITLE
			+ TEXT_TYPE
			+ COMMA_SEP
			+ AllWLists.OWNER_ID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ AllWLists.CREATED_AT
			+ TEXT_TYPE
			+ COMMA_SEP
			+ AllWLists.UPDATED_AT
			+ TEXT_TYPE
			+ COMMA_SEP
			+ AllWLists.ISSYNCED + " )";

	private static final String SQL_CREATE_GOOGLETASK = "CREATE TABLE " + "%s"
			+ " ("
			+ GoogleTasks._ID
			+ " TEXT PRIMARY KEY,"
			+ GoogleTasks.TITLE
			+ TEXT_TYPE
			+ COMMA_SEP
			+ GoogleTasks.STATUS
			+ TEXT_TYPE
			+ COMMA_SEP
			+ GoogleTasks.DUE
			+ TEXT_TYPE
			+ COMMA_SEP
			+ GoogleTasks.COMPLETED
			+ TEXT_TYPE
			+ COMMA_SEP
			+ GoogleTasks.DELETED
			+ TEXT_TYPE
			+ COMMA_SEP
			+ GoogleTasks.UPDATED
			+ TEXT_TYPE
			+ COMMA_SEP
			+ GoogleTasks.NOTES
			+ TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ WunderTasks.TABLE_NAME;

	public WunderSyncDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(String.format(SQL_CREATE_WUNDERTASK, WunderTasks.TABLE_NAME));
		db.execSQL(String.format(SQL_CREATE_WUNDERTASK, WunderTasks.OLD_TABLE_NAME));
		
		db.execSQL(String.format(SQL_CREATE_GOOGLETASK, GoogleTasks.TABLE_NAME));
		db.execSQL(String.format(SQL_CREATE_GOOGLETASK, GoogleTasks.OLD_TABLE_NAME));
		
		db.execSQL(String.format(SQL_CREATE_WUNDERLISTS, AllWLists.TABLE_NAME));
		db.execSQL(String.format(SQL_CREATE_WUNDERLISTS, AllWLists.OLD_TABLE_NAME));
		
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
