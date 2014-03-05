package in.co.madhur.wunderlistsync.database;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.tasks.model.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatatypeMismatchException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.TaskSyncState;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.api.model.WTask;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.AllWLists;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.GoogleTasks;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.GoogleUser;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.WunderTasks;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.WunderUser;

public class DbHelper
{
	private static Context context;
	private static WunderSyncDB db;
	private static DbHelper dbHelper;

	public static synchronized DbHelper getInstance(Context context)
	{
		if (dbHelper == null)
		{
			DbHelper.context = context;
			db = new WunderSyncDB(context);
			dbHelper = new DbHelper();
			return dbHelper;
		}
		else
			return dbHelper;

	}

	public void WriteWunderTasks(List<WTask> tasks) throws Exception
	{
		SQLiteDatabase database = db.getWritableDatabase();

		try
		{

			String sql = "INSERT OR REPLACE INTO " + WunderTasks.TABLE_NAME
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
			SQLiteStatement statement = database.compileStatement(sql);
			database.beginTransaction();
			for (int i = 0; i < tasks.size(); i++)
			{
				statement.clearBindings();

				statement.bindString(1, tasks.get(i).getId());

				statement.bindString(2, tasks.get(i).getList_id());

				statement.bindString(3, tasks.get(i).getTitle());

				statement.bindString(4, tasks.get(i).getOwner_id());

				statement.bindString(5, tasks.get(i).getCreated_at());

				statement.bindString(6, tasks.get(i).getCreated_by_id());

				statement.bindString(7, tasks.get(i).getUpdated_at());

				statement.bindString(8, tasks.get(i).getStarred().toString());

				statement.bindString(9, tasks.get(i).getCompleted_at());

				statement.bindString(10, tasks.get(i).getCompleted_by_id().toString());

				statement.bindString(11, tasks.get(i).getDeleted_at().toString());

				statement.execute();
			}

			database.setTransactionSuccessful();
			// database.endTransaction();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;

		}
		finally
		{
			database.endTransaction();
			database.close();
		}
	}

	public void MoveData() throws Exception
	{
		SQLiteDatabase database = db.getWritableDatabase();

		try
		{
			String sql1 = String.format("delete from %s", WunderTasks.OLD_TABLE_NAME);
			String sql2 = String.format("delete from %s", GoogleTasks.OLD_TABLE_NAME);
			String sql3 = String.format("delete from %s", AllWLists.OLD_TABLE_NAME);

			String sql4 = String.format("INSERT INTO %s SELECT * FROM %s", WunderTasks.OLD_TABLE_NAME, WunderTasks.TABLE_NAME);
			String sql5 = String.format("INSERT INTO %s SELECT * FROM %s", GoogleTasks.OLD_TABLE_NAME, GoogleTasks.TABLE_NAME);
			String sql6 = String.format("INSERT INTO %s SELECT * FROM %s", AllWLists.OLD_TABLE_NAME, AllWLists.TABLE_NAME);

			database.beginTransaction();
			database.execSQL(sql1);
			database.execSQL(sql2);
			database.execSQL(sql3);
			database.execSQL(sql4);
			database.execSQL(sql5);
			database.execSQL(sql6);
			database.setTransactionSuccessful();
			// database.endTransaction();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.endTransaction();
			database.close();
		}
	}

	public void TruncateListsOld() throws Exception
	{
		SQLiteDatabase database = db.getWritableDatabase();

		try
		{

			String sql1 = String.format("delete from %s", AllWLists.OLD_TABLE_NAME);

			database.beginTransaction();
			database.execSQL(sql1);
			database.setTransactionSuccessful();
			// database.endTransaction();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	public void TruncateTables() throws Exception
	{
		SQLiteDatabase database = db.getWritableDatabase();

		try
		{

			String sql1 = String.format("delete from %s", WunderTasks.TABLE_NAME);
			String sql2 = String.format("delete from %s", GoogleTasks.TABLE_NAME);
			String sql3 = String.format("delete from %s", AllWLists.TABLE_NAME);

			database.beginTransaction();
			database.execSQL(sql1);
			database.execSQL(sql2);
			database.execSQL(sql3);
			database.setTransactionSuccessful();
			// database.endTransaction();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	public List<WList> ReadLists()
	{

		SQLiteDatabase database = db.getWritableDatabase();

		Cursor c = database.query(AllWLists.OLD_TABLE_NAME, // The table to
															// query
				null, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
		);

		List<WList> wList = new ArrayList<WList>();
		WList listObj;
		if (c.moveToFirst())
		{
			do
			{
				listObj = new WList();
				listObj.setId(c.getString(c.getColumnIndexOrThrow(AllWLists._ID)));
				listObj.setTitle(c.getString(c.getColumnIndexOrThrow(AllWLists.TITLE)));
				listObj.setOwner_id(c.getString(c.getColumnIndexOrThrow(AllWLists.OWNER_ID)));
				listObj.setUpdated_at(c.getString(c.getColumnIndexOrThrow(AllWLists.UPDATED_AT)));
				listObj.setCreated_at(c.getString(c.getColumnIndexOrThrow(AllWLists.CREATED_AT)));

				wList.add(listObj);

			}
			while (c.moveToNext());
		}

		return wList;

	}

	private void WriteLists(List<WList> lists, String tableName)
			throws Exception
	{

		SQLiteDatabase database = db.getWritableDatabase();

		try
		{

			String sql = "INSERT OR REPLACE INTO " + tableName + "("
					+ AllWLists._ID + "," + AllWLists.TITLE + ","
					+ AllWLists.OWNER_ID + "," + AllWLists.CREATED_AT + ","
					+ AllWLists.UPDATED_AT + ") VALUES (?,?,?,?,?);";
			SQLiteStatement statement = database.compileStatement(sql);
			database.beginTransaction();
			for (int i = 0; i < lists.size(); i++)
			{
				statement.clearBindings();

				statement.bindString(1, lists.get(i).getId());

				statement.bindString(2, lists.get(i).getTitle());

				statement.bindString(3, lists.get(i).getOwner_id());

				statement.bindString(4, lists.get(i).getCreated_at());

				statement.bindString(5, lists.get(i).getUpdated_at());

				statement.execute();
			}

			database.setTransactionSuccessful();
			// database.endTransaction();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;

		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	public void EnsureUsers(String googleUser, String wunderUserEmail, String wunderUserId)
			throws Exception
	{

		EnsureGoogleUser(googleUser);
		EnsureWunderUser(wunderUserEmail, wunderUserId);

	}

	public void DeleteAllTables() throws Exception
	{

		SQLiteDatabase database = db.getWritableDatabase();

		String[] deleteEntries = WunderSyncDB.GetDeleteEntries();

		try
		{

			database.beginTransaction();

			for (int i = 0; i < deleteEntries.length; ++i)
			{
				database.execSQL(deleteEntries[i]);
			}
			database.setTransactionSuccessful();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	private void EnsureGoogleUser(String googleUser) throws Exception
	{

		SQLiteDatabase database = db.getWritableDatabase();

		try
		{
			String sql1 = String.format("INSERT OR REPLACE INTO %s (%s) VALUES ('%s' );", GoogleUser.TABLE_NAME, GoogleUser.EMAIL, googleUser);
			Log.d(App.TAG, sql1);

			database.beginTransaction();
			database.execSQL(sql1);
			database.setTransactionSuccessful();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	private void EnsureWunderUser(String wunderUserEmail, String wunderUserId)
			throws Exception
	{

		SQLiteDatabase database = db.getWritableDatabase();

		try
		{
			String sql1 = String.format("INSERT OR REPLACE INTO %s (%s, %s) VALUES ('%s', '%s' );", WunderUser.TABLE_NAME, WunderUser._ID, WunderUser.EMAIL, wunderUserId, wunderUserEmail);
			Log.d(App.TAG, sql1);

			database.beginTransaction();
			database.execSQL(sql1);
			database.setTransactionSuccessful();
		}
		catch (Exception e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	public void WriteListsOld(List<WList> lists) throws Exception
	{

		WriteLists(lists, AllWLists.OLD_TABLE_NAME);

	}

	public void WriteLists(List<WList> lists) throws Exception
	{

		WriteLists(lists, AllWLists.TABLE_NAME);

	}

	public void writeGoogleTasks(List<Task> tasks) throws Exception
	{

		SQLiteDatabase database = db.getWritableDatabase();

		try
		{

			String sql = "INSERT INTO " + GoogleTasks.TABLE_NAME
					+ " VALUES (?,?,?,?,?,?,?,?);";
			SQLiteStatement statement = database.compileStatement(sql);
			database.beginTransaction();
			for (int i = 0; i < tasks.size(); i++)
			{
				statement.clearBindings();

				statement.bindString(1, tasks.get(i).getId());

				statement.bindString(2, tasks.get(i).getTitle());

				statement.bindString(3, tasks.get(i).getStatus());

				if (tasks.get(i).getDue() != null)
					statement.bindString(4, tasks.get(i).getDue().toString());
				else
					statement.bindString(4, "");

				if (tasks.get(i).getCompleted() != null)
					statement.bindString(5, tasks.get(i).getCompleted().toString());
				else
					statement.bindString(5, "");

				if (tasks.get(i).getDeleted() != null)
					statement.bindString(6, tasks.get(i).getDeleted().toString());
				else
					statement.bindString(6, "");

				if (tasks.get(i).getUpdated() != null)
					statement.bindString(7, tasks.get(i).getUpdated().toString());
				else
					statement.bindString(7, "");

				if (tasks.get(i).getNotes() != null)
					statement.bindString(8, tasks.get(i).getNotes());
				else
					statement.bindString(8, "");

				statement.execute();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
			{
				Log.e(App.TAG, e.getMessage());
			}
			throw e;

		}
		finally
		{
			database.endTransaction();
			database.close();
		}

	}

	public String getGoogleListIdofWlist(String id)
	{
		SQLiteDatabase database = db.getWritableDatabase();

		try
		{
			Cursor c = database.query(AllWLists.TABLE_NAME, new String[] { AllWLists.GOOGLE_LIST_ID }, AllWLists._ID
					+ "=?", new String[] { id }, null, null, null);
			if (c.moveToFirst())
			{
				return c.getString(c.getColumnIndexOrThrow(AllWLists.GOOGLE_LIST_ID));

			}
		}
		catch (SQLException e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.close();
		}

		return "";

	}

	public void setGoogleListofWlist(String wListid, String googleListId)
	{
		SQLiteDatabase database = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AllWLists.GOOGLE_LIST_ID, googleListId);
		values.put(AllWLists.ISSYNCED, Boolean.TRUE.toString());

		try
		{
			database.update(AllWLists.TABLE_NAME, values, AllWLists._ID + "=?", new String[] { wListid });
		}
		catch (SQLException e)
		{
			Log.e(App.TAG, e.getMessage());
			throw e;
		}
		finally
		{
			database.close();
		}

	}
}
