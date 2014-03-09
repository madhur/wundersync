package in.co.madhur.wunderlistsync.recievers;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.service.Alarms;
import in.co.madhur.wunderlistsync.utils.AppLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

public class SyncSchedulerReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (App.LOCAL_LOGV)
			Log.v(App.TAG, "onReceive(" + context + "," + intent + ")");

		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			bootup(context);
		}

	}

	private void bootup(Context context)
	{
		if (shouldSchedule(context))
		{
			getAlarms(context).scheduleBootupBackup();
		}
		else
		{
			Log.i(App.TAG, "Received bootup but not set up to back up.");
		}
	}

	private boolean shouldSchedule(Context context)
	{
		final AppPreferences preferences = new AppPreferences(context);

		final boolean autoSync = preferences.isAutoSync();
		final boolean loginInformationSet = preferences.isLoginInformationSet();
		final boolean firstSync = preferences.isFirstSync();
		final boolean schedule = (autoSync && loginInformationSet && !firstSync);

		if (!schedule)
		{
			final String message = new StringBuilder().append("Not set up to back up. ").append("autoSync=").append(autoSync).append(", loginInfoSet=").append(loginInformationSet).append(", firstBackup=").append(firstSync).toString();

			log(context, message, true);
		}
		return schedule;
	}

	private void log(Context context, String message, boolean appLog)
	{
		Log.d(App.TAG, message);
		if (appLog)
		{
			new AppLog(DateFormat.getDateFormatOrder(context)).appendAndClose(message);
		}
	}

	protected Alarms getAlarms(Context context)
	{
		return new Alarms(context);
	}

	

}
