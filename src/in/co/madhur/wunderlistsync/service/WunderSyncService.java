package in.co.madhur.wunderlistsync.service;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.SyncConfig;
import in.co.madhur.wunderlistsync.TaskSyncState;
import in.co.madhur.wunderlistsync.WunderSyncState;
import in.co.madhur.wunderlistsync.AppPreferences.Keys;
import in.co.madhur.wunderlistsync.api.LoginResponse;
import in.co.madhur.wunderlistsync.api.WunderAPI;
import in.co.madhur.wunderlistsync.api.WunderList;
import retrofit.RestAdapter;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract.SyncState;
import android.util.Log;

public class WunderSyncService extends Service
{

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
		AppPreferences preferences = new AppPreferences(this);
		SyncConfig config = new SyncConfig(true, true, null);
		config.setUsername(preferences.GetUserName());
		config.setPassword(preferences.GetMetadata(Keys.WUNDER_PASSWORD));
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
			try
			{
				publishProgress(new TaskSyncState(WunderSyncState.LOGIN));

				WunderList wunderList = WunderList.getInstance();

				if (wunderList.IsLoginRequired(params[0].getToken()))
				{
					Log.v(App.TAG, "Logging in...");
					wunderList.Login(config.getUsername(), config.getPassword());
				}
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
