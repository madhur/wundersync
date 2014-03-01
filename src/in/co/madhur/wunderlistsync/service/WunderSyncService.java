package in.co.madhur.wunderlistsync.service;

import java.util.List;

import com.google.api.services.tasks.TasksScopes;

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
import retrofit.RestAdapter;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract.SyncState;
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

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

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
				
				Log.v(App.TAG, String.valueOf(tasks.size()));
				
			}
			catch (AuthException e)
			{
				Log.v(App.TAG, "AuthException");
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
