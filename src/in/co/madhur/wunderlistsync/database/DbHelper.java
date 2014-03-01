package in.co.madhur.wunderlistsync.database;

import java.util.List;

import com.google.api.services.tasks.model.Task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatatypeMismatchException;
import android.database.sqlite.SQLiteStatement;

import in.co.madhur.wunderlistsync.api.WunderTask;
import in.co.madhur.wunderlistsync.database.WunderSyncContract.WunderTasks;

public class DbHelper
{
	private static Context context;
	private static WunderSyncDB db;
	private static DbHelper dbHelper;
	
	public static synchronized DbHelper getInstance  (Context context)
	{
		if(dbHelper==null)
		{
			DbHelper.context = context;
			db = new WunderSyncDB(context);
			dbHelper=new DbHelper();
			return dbHelper;
		}
		else
			return dbHelper;

	}

	public void WriteWunderTasks(List<WunderTask> tasks)
	{
		try
		{
			SQLiteDatabase database = db.getWritableDatabase();

			String sql = "INSERT INTO " + WunderTasks.TABLE_NAME
					+ " VALUES (?,?,?,?,?,?,?,?,?,?);";
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

				statement.execute();
			}

			database.setTransactionSuccessful();
			database.endTransaction();
		}
		catch (SQLiteDatatypeMismatchException e)
		{
			throw e;

		}
	}
	
	public void RenameTables()
	{
		SQLiteDatabase database = db.getWritableDatabase();

		String sql1=String.format("Drop table {0}", WunderTasks.OLD_TABLE_NAME);
		String sql2 =String.format("Alter Table {0} rename to {1}", WunderTasks.TABLE_NAME, WunderTasks.OLD_TABLE_NAME);
		
		database.beginTransaction();
		database.execSQL(sql1);
		database.execSQL(sql2);
		database.setTransactionSuccessful();
		database.endTransaction();
	}

	public void writeGoogleTasks(List<Task> gTasks)
	{
		
	}

}
