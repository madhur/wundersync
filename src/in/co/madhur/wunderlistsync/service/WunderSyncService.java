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
	WunderAPI service;

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
		
		GetAllWunderTasks();
		
		return START_NOT_STICKY;
	}

	private void GetAllWunderTasks()
	{
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint("https://api.wunderlist.com")
	    .build();

		service = restAdapter.create(WunderAPI.class);
		new GetWunderListTasks().execute(0);
		
	
		
	}
	
	
	private class GetWunderListTasks extends AsyncTask<Integer, Integer, Integer>
	{
		
		

		@Override
		protected Integer doInBackground(Integer... params)
		{
			LoginResponse s=service.login("ahuja.madhur@gmail.com", "goldi25");
			Log.v("df", s.toString());
			return 1;
		}
		
		
	}

}
