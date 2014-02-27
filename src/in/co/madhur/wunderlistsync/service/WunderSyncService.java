package in.co.madhur.wunderlistsync.service;

import in.co.madhur.wunderlistsync.api.LoginResponse;
import in.co.madhur.wunderlistsync.api.WunderAPI;
import retrofit.RestAdapter;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class WunderSyncService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		
		SyncWunder();
		
		return START_NOT_STICKY;
	}

	private void SyncWunder()
	{
		
		new WunderSyncTask().execute(0);
		
	
		
	}
	
	
	private class WunderSyncTask extends AsyncTask<Integer, Integer, Integer>
	{
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			
		}
		
		
		@Override
		protected Integer doInBackground(Integer... params)
		{
			
			
			publishProgress(1);
			
			return 1;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
		
	}

}
