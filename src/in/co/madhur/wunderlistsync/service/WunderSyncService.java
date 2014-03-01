package in.co.madhur.wunderlistsync.service;

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
		Log.v(App.TAG, "Executing task");
		new WunderSyncTask().execute(config);

	}

	private class WunderSyncTask extends
			AsyncTask<SyncConfig, TaskSyncState, TaskSyncState>
	{
		com.google.api.services.tasks.Tasks taskService;
		final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
		final com.google.api.client.json.JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		private GoogleAccountCredential credential;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(TasksScopes.TASKS));
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
							Log.v(App.TAG, "New token: " + wunderList.GetToken());
						}

					}
				}

				publishProgress(new TaskSyncState(WunderSyncState.FETCH_WUNDERLIST_TASKS));
				List<WunderTask> tasks = wunderList.GetTasks();
				
				DbHelper dbHelper=DbHelper.getInstance(WunderSyncService.this);
				
				Log.d(App.TAG, "Writing wunder tasks to db");
				dbHelper.WriteWunderTasks(tasks);
				
				Log.d(App.TAG, "renaminmg tables");
				dbHelper.RenameTables();
				
				
				List<Task> gTasks = taskService.tasks().list("@default").setFields("items/title").execute().getItems();
				
				dbHelper.writeGoogleTasks(gTasks);
				
				
			}
			catch (Exception e)
			{
				Log.v(App.TAG, "Exception");
				return new TaskSyncState(e.getMessage());

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

}
