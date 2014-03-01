package in.co.madhur.wunderlistsync.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.SyncConfig;
import in.co.madhur.wunderlistsync.TaskSyncState;
import in.co.madhur.wunderlistsync.WunderSyncState;
import in.co.madhur.wunderlistsync.AppPreferences.Keys;
import in.co.madhur.wunderlistsync.api.AuthError;
import in.co.madhur.wunderlistsync.api.AuthException;
import in.co.madhur.wunderlistsync.api.LoginResponse;
import in.co.madhur.wunderlistsync.api.WunderAPI;
import in.co.madhur.wunderlistsync.api.WunderList;
import in.co.madhur.wunderlistsync.api.WunderTask;
import in.co.madhur.wunderlistsync.database.DbHelper;
import retrofit.RestAdapter;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract.SyncState;
import android.test.RenamingDelegatingContext;
import android.text.TextUtils;
import android.util.Log;

public class WunderSyncService extends Service
{
	AppPreferences preferences;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);

		SyncWunder();

		return START_NOT_STICKY;
	}

	private void SyncWunder()
	{
		preferences = new AppPreferences(this);
		SyncConfig config = new SyncConfig(true, true, null);
		
		config.setUsername(preferences.GetWunderUserName());
		config.setPassword(preferences.GetWunderPassword());
		config.setToken(preferences.GetMetadata(Keys.TOKEN));
		config.setGoogleAccount(preferences.GetUserName());
		
		Log.v(App.TAG, "Executing task");
		new WunderSyncTask(config).execute(config);

	}

	private class WunderSyncTask extends
			AsyncTask<SyncConfig, TaskSyncState, TaskSyncState>
	{
		com.google.api.services.tasks.Tasks taskService;
		final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
		final com.google.api.client.json.JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		private GoogleAccountCredential credential;
		SyncConfig config;
		
		public WunderSyncTask(SyncConfig config)
		{
			this.config=config;
		}
		
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(TasksScopes.TASKS));
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
		protected TaskSyncState doInBackground(SyncConfig... params)
		{
			SyncConfig config = params[0];
			LoginResponse loginResponse;
			WunderList wunderList = null;
			DbHelper dbHelper = null;

			try
			{
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
							Log.v(App.TAG, "New token: "
									+ wunderList.GetToken());
						}

					}
				}

				publishProgress(new TaskSyncState(WunderSyncState.FETCH_WUNDERLIST_TASKS));
				List<WunderTask> tasks = wunderList.GetTasks();

				dbHelper = DbHelper.getInstance(WunderSyncService.this);

				// Try dropping the tables if previosuly exists. This can happen
				// if previous sync was incorrectly aborted
				try
				{
					Log.d(App.TAG, "Truncating previous tables");
					dbHelper.TruncateTables();
				}
				catch (SQLiteException e)
				{
					// Silently catch and continue, since this is not an error
					Log.d(App.TAG, e.getMessage());
				}

				Log.d(App.TAG, "Writing wunder tasks to db");
				dbHelper.WriteWunderTasks(tasks);

				Log.d(App.TAG, "moving data to told");
				dbHelper.MoveData();
				
				Log.d(App.TAG, "Truncating tables");
				dbHelper.TruncateTables();

				// Publish UI update to google progress
				publishProgress(new TaskSyncState(WunderSyncState.FETCH_GOOGLE_TASKS));
				
				List<Task> gTasks = taskService.tasks().list("@default").execute().getItems();

				Log.d(App.TAG, "Writing google tasks to db");
				dbHelper.writeGoogleTasks(gTasks);

			}
			catch (Exception e)
			{
				Log.e(App.TAG, e.getClass().getName());
				
				if(e.getMessage()!=null)
				{
					Log.e(App.TAG, e.getMessage());
					return new TaskSyncState(e.getMessage());
				}

			}
			finally
			{

				
			}
			return new TaskSyncState(WunderSyncState.FINISHED);

		}

		@Override
		protected void onPostExecute(TaskSyncState result)
		{
			super.onPostExecute(result);
			App.getEventBus().post(result);
			
			WunderSyncService.this.stopSelf();

		}

	}

}
