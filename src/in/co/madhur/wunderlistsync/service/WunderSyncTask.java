package in.co.madhur.wunderlistsync.service;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.TaskSyncConfig;
import in.co.madhur.wunderlistsync.TaskSyncState;
import in.co.madhur.wunderlistsync.WunderSyncState;
import in.co.madhur.wunderlistsync.AppPreferences.Keys;
import in.co.madhur.wunderlistsync.api.APIConsts;
import in.co.madhur.wunderlistsync.api.AuthError;
import in.co.madhur.wunderlistsync.api.AuthException;
import in.co.madhur.wunderlistsync.api.WunderList;
import in.co.madhur.wunderlistsync.api.model.LoginResponse;
import in.co.madhur.wunderlistsync.api.model.Me;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.api.model.WTask;
import in.co.madhur.wunderlistsync.database.DbHelper;
import in.co.madhur.wunderlistsync.gtasks.GTaskHelper;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.TasksScopes;

public class WunderSyncTask extends
		AsyncTask<TaskSyncConfig, TaskSyncState, TaskSyncState>
{
	com.google.api.services.tasks.Tasks taskService;
	final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
	final com.google.api.client.json.JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	private GoogleAccountCredential credential;
	TaskSyncConfig config;
	boolean isCalendarSync;
	Long calendarId;
	private final WunderSyncService service;
	private final AppPreferences preferences;

	public WunderSyncTask(TaskSyncConfig config, WunderSyncService service)
	{
		this.service=service;
		this.config = config;
		preferences=new AppPreferences(service);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

		credential = GoogleAccountCredential.usingOAuth2(service, Collections.singleton(TasksScopes.TASKS));
		credential.setSelectedAccountName(config.getGoogleAccount());
		taskService = new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential).setApplicationName("WunderSync").build();

	}

	@Override
	protected void onProgressUpdate(TaskSyncState... values)
	{
		super.onProgressUpdate(values);

		App.getEventBus().post(values[0]);

	}

	@Override
	protected TaskSyncState doInBackground(TaskSyncConfig... params)
	{
		TaskSyncConfig config = params[0];
		LoginResponse loginResponse;
		WunderList wunderList = null;
		DbHelper dbHelper = null;
		GTaskHelper taskHelper = null;

		try
		{
			service.acquireLocks();
			
			publishProgress(new TaskSyncState(WunderSyncState.LOGIN));

			if (!TextUtils.isEmpty(params[0].getToken()))
			{

				try
				{
					wunderList = WunderList.getInstance(params[0].getToken());
				}
				catch (AuthException e)
				{
					Log.v(App.TAG, "Old token expired, getting new token...");
					if (e.getErrorCode() == AuthError.OLD_TOKEN_EXPIRED)
					{
						wunderList = WunderList.getInstance(config.getUsername(), config.getPassword());
						preferences.SetMetadata(Keys.TOKEN, wunderList.GetToken());
						Log.v(App.TAG, "New token: " + wunderList.GetToken());
					}
					else if (e.getErrorCode() == AuthError.USER_NOT_FOUND)
					{
						throw new AuthException(APIConsts.USER_NOT_FOUND);
					}

				}
			}
			else
			{
				wunderList = WunderList.getInstance(config.getUsername(), config.getPassword());
				preferences.SetMetadata(Keys.TOKEN, wunderList.GetToken());

			}

			publishProgress(new TaskSyncState(WunderSyncState.FETCH_WUNDERLIST_TASKS));

			Me userInfo = wunderList.GetUserInfo();

			List<WList> lists = wunderList.GetLists();

			List<WTask> tasks = wunderList.GetTasks();

			dbHelper = DbHelper.getInstance(service);

			dbHelper.EnsureUsers(config.getGoogleAccount(), config.getUsername(), userInfo.getId());

			// Try dropping the tables if previosuly exists. This can happen
			// if previous sync was incorrectly aborted
			// try
			// {
			// Log.d(App.TAG, "Truncating previous tables");
			// dbHelper.TruncateTables();
			// }
			// catch (SQLiteException e)
			// {
			// // Silently catch and continue, since this is not an error
			// Log.d(App.TAG, e.getMessage());
			// }

			Log.d(App.TAG, "Writing wunderlists to db");
			dbHelper.WriteLists(lists);

			Log.d(App.TAG, "Writing wunder tasks to db");
			dbHelper.WriteWunderTasks(tasks);

			publishProgress(new TaskSyncState(WunderSyncState.FETCH_GOOGLE_TASKS));
			taskHelper = GTaskHelper.GetInstance(taskService, service);

			publishProgress(new TaskSyncState(WunderSyncState.SYNCING));

			taskHelper.CreateOrEnsureLists(lists, config.getSelectedListIds());

			taskHelper.CreateOrEnsureTasks(tasks, config.getSelectedListIds());

			// taskHelper.DeleteEmptyLists();

			// Log.d(App.TAG, "Writing google tasks to db");
			// List<Task> gTasks =
			// taskService.tasks().list("@default").execute().getItems();
			// dbHelper.writeGoogleTasks(gTasks);

			// Log.d(App.TAG, "moving data to old");
			// dbHelper.MoveData();

			// Log.d(App.TAG, "Truncating tables");
			// dbHelper.TruncateTables();
			// preferences.SetNowDate();

		}
		catch (UserRecoverableAuthIOException e)
		{
			Log.e(App.TAG, "UserRecoverableAuthIOException");
			return new TaskSyncState(WunderSyncState.USER_RECOVERABLE_ERROR, e.getIntent());

		}
		catch (GoogleAuthIOException e)
		{

			Log.e(App.TAG, "GoogleAuthIOException");
			return new TaskSyncState("GoogleAuthIOException");
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
			{
				Log.e(App.TAG, e.getMessage());
				e.printStackTrace();
				return new TaskSyncState(e.getMessage());
			}

			return new TaskSyncState("An error occured, please check logs");

		}
		finally
		{
			service.releaseLocks();
		}
		return new TaskSyncState(WunderSyncState.FINISHED);

	}

	@Override
	protected void onPostExecute(TaskSyncState result)
	{
		super.onPostExecute(result);
		App.getEventBus().post(result);

	}

}
