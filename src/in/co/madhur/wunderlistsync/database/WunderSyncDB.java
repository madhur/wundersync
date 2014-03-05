package in.co.madhur.wunderlistsync.database;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.AllWLists;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.GoogleTasks;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.GoogleUser;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.WunderTasks;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.WunderUser;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WunderSyncDB extends SQLiteOpenHelper
{

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "wundersync.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String DEFAULT = " default";
	private static final String FALSE = " false";
	private static final String EMPTY = "''";

	private static final String SQL_CREATE_WUNDERUSER = "CREATE TABLE " + "%s"
			+ " (" + WunderUser._ID + " TEXT PRIMARY KEY," + WunderUser.EMAIL
			+ TEXT_TYPE + " )";

	private static final String SQL_CREATE_GOOGLEUSER = "CREATE TABLE " + "%s"
			+ " (" + GoogleUser.EMAIL + TEXT_TYPE + " PRIMARY KEY  " + ")";

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
			+ WunderTasks.COMPLETED_BY_ID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.DELETED_AT
			+ TEXT_TYPE
			+ COMMA_SEP
			+ WunderTasks.ISSYNCED
			+ TEXT_TYPE
			+ DEFAULT
			+ FALSE

			+ COMMA_SEP
			+ WunderTasks.GOOGLE_LIST_ID
			+ TEXT_TYPE
			+ DEFAULT
			+ EMPTY + " )";

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
			+ AllWLists.ISSYNCED
			+ TEXT_TYPE
			+ DEFAULT
			+ FALSE

			+ COMMA_SEP
			+ AllWLists.GOOGLE_LIST_ID
			+ TEXT_TYPE
			+ DEFAULT
			+ EMPTY + " )";

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
			+ TEXT_TYPE + COMMA_SEP + GoogleTasks.NOTES + TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRY_WUNDERUSER = "DROP TABLE IF EXISTS "
			+ WunderUser.TABLE_NAME;

	private static final String SQL_DELETE_ENTRY_GOOGLEUSER = "DROP TABLE IF EXISTS "
			+ GoogleUser.TABLE_NAME;

	private static final String SQL_DELETE_ENTRY_WUNDERTASK = "DROP TABLE IF EXISTS "
			+ WunderTasks.TABLE_NAME;

	private static final String SQL_DELETE_ENTRY_WUNDERLISTS = "DROP TABLE IF EXISTS "
			+ AllWLists.TABLE_NAME;

	private static final String SQL_DELETE_ENTRY_GOOGLETASK = "DROP TABLE IF EXISTS "
			+ GoogleTasks.TABLE_NAME;

	public WunderSyncDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static String[] GetDeleteEntries()
	{

		return new String[] { SQL_DELETE_ENTRY_WUNDERUSER,
				SQL_DELETE_ENTRY_GOOGLEUSER, SQL_DELETE_ENTRY_WUNDERTASK,
				SQL_DELETE_ENTRY_WUNDERLISTS, SQL_DELETE_ENTRY_GOOGLETASK };
	}

	public void onCreate(SQLiteDatabase db)
	{

		db.execSQL(String.format(SQL_CREATE_GOOGLEUSER, GoogleUser.TABLE_NAME));
		db.execSQL(String.format(SQL_CREATE_WUNDERUSER, WunderUser.TABLE_NAME));

		db.execSQL(String.format(SQL_CREATE_WUNDERTASK, WunderTasks.TABLE_NAME));
		// db.execSQL(String.format(SQL_CREATE_WUNDERTASK,
		// WunderTasks.OLD_TABLE_NAME));

		db.execSQL(String.format(SQL_CREATE_GOOGLETASK, GoogleTasks.TABLE_NAME));
		// db.execSQL(String.format(SQL_CREATE_GOOGLETASK,
		// GoogleTasks.OLD_TABLE_NAME));

		Log.d(App.TAG, SQL_CREATE_WUNDERLISTS);
		db.execSQL(String.format(SQL_CREATE_WUNDERLISTS, AllWLists.TABLE_NAME));
		// db.execSQL(String.format(SQL_CREATE_WUNDERLISTS,
		// AllWLists.OLD_TABLE_NAME));

	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

		db.execSQL(SQL_DELETE_ENTRY_WUNDERUSER);
		db.execSQL(SQL_DELETE_ENTRY_GOOGLEUSER);
		db.execSQL(SQL_DELETE_ENTRY_WUNDERTASK);
		db.execSQL(SQL_DELETE_ENTRY_WUNDERLISTS);
		db.execSQL(SQL_DELETE_ENTRY_GOOGLETASK);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}
}
