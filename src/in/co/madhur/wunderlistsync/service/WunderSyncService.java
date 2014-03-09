package in.co.madhur.wunderlistsync.service;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.MainActivity;
import in.co.madhur.wunderlistsync.R;
import in.co.madhur.wunderlistsync.TaskSyncConfig;
import in.co.madhur.wunderlistsync.AppPreferences.Keys;
import in.co.madhur.wunderlistsync.utils.AppLog;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;

public class WunderSyncService extends Service
{
	private AppPreferences preferences;
	private static WunderSyncTask syncTask;
	private PowerManager.WakeLock mWakeLock;
	private WifiManager.WifiLock mWifiLock;

	private AppLog appLog;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.appLog = new AppLog(DateFormat.getDateFormatOrder(this));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (appLog != null)
			appLog.close();
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
		TaskSyncConfig config = new TaskSyncConfig(true, true, null);

		config.setUsername(preferences.GetWunderUserName());
		config.setPassword(preferences.GetWunderPassword());
		config.setToken(preferences.GetMetadata(Keys.TOKEN));
		config.setGoogleAccount(preferences.GetUserName());
		config.setSelectedListIds(preferences.getSelectedListsIds());
		Log.v(App.TAG, "Executing task");
		syncTask = new WunderSyncTask(config, this);
		syncTask.execute(config);

	}

	protected void acquireLocks()
	{
		if (mWakeLock == null)
		{
			PowerManager pMgr = (PowerManager) getSystemService(POWER_SERVICE);
			mWakeLock = pMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, App.TAG);
		}
		mWakeLock.acquire();

		if (isConnectedViaWifi())
		{
			// we have Wifi, lock it
			WifiManager wMgr = getWifiManager();
			if (mWifiLock == null)
			{
				mWifiLock = wMgr.createWifiLock(getWifiLockType(), App.TAG);
			}
			mWifiLock.acquire();
		}
	}

	protected void releaseLocks()
	{
		if (mWakeLock != null && mWakeLock.isHeld())
		{
			mWakeLock.release();
			mWakeLock = null;
		}
		if (mWifiLock != null && mWifiLock.isHeld())
		{
			mWifiLock.release();
			mWifiLock = null;
		}
	}

	private void appLog(int id, Object... args)
	{
		final String msg = getString(id, args);
		if (appLog != null)
		{
			appLog.append(msg);
		}
		else if (App.LOCAL_LOGV)
		{
			Log.d(App.TAG, "AppLog: " + msg);
		}
	}

	private NotificationManager getNotifier()
	{
		return (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
	}

	private ConnectivityManager getConnectivityManager()
	{
		return (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private WifiManager getWifiManager()
	{
		return (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	}

	private Notification createNotification(int resId)
	{
		Notification n = new Notification(R.drawable.ic_notification, getString(resId), System.currentTimeMillis());
		n.flags = Notification.FLAG_ONGOING_EVENT;
		return n;
	}

	private PendingIntent getPendingIntent()
	{
		return PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private boolean isConnectedViaWifi()
	{
		WifiManager wifiManager = getWifiManager();
		return (wifiManager != null
				&& wifiManager.isWifiEnabled()
				&& getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
	}

	public static boolean IsSyncTaskRunning()
	{
		if (syncTask != null
				&& syncTask.getStatus() == AsyncTask.Status.RUNNING)
			return true;
		return false;

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private int getWifiLockType()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1 ? WifiManager.WIFI_MODE_FULL_HIGH_PERF
				: WifiManager.WIFI_MODE_FULL;
	}

}
