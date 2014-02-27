package in.co.madhur.wunderlistsync.service;

import in.co.madhur.wunderlistsync.SyncConfig;
import in.co.madhur.wunderlistsync.api.LoginResponse;
import in.co.madhur.wunderlistsync.api.WunderAPI;
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
		AppPreferences preferences=new AppPreferences(this);
		SyncConfig config=new SyncConfig(true, true, null);
		new WunderSyncTask().execute(config);
		
	}
	
	
	private class WunderSyncTask extends AsyncTask<SyncConfig, SyncState, SyncState>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		

		@Override
		protected void onProgressUpdate(SyncState... values)
		{
			super.onProgressUpdate(values);
			
			
		}
		
		
		@Override
		protected SyncState doInBackground(SyncConfig... params)
		{
			
			
			return null;
			
			
			//publishProgress(1);
			
			
		}
		
		@Override
		protected void onPostExecute(SyncState result)
		{
			super.onPostExecute(result);
		}


		
		
		
	}

}
