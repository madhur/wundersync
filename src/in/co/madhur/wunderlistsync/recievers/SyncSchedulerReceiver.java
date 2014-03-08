package in.co.madhur.wunderlistsync.recievers;

import in.co.madhur.wunderlistsync.App;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
			Log.i(TAG, "Received bootup but not set up to back up.");
		}
	}

	private boolean shouldSchedule(Context context)
	{
		final Preferences preferences = getPreferences(context);

		final boolean autoSync = preferences.isEnableAutoSync();
		final boolean loginInformationSet = getAuthPreferences(context).isLoginInformationSet();
		final boolean firstBackup = preferences.isFirstBackup();
		final boolean schedule = (autoSync && loginInformationSet && !firstBackup);

		if (!schedule)
		{
			final String message = new StringBuilder().append("Not set up to back up. ").append("autoSync=").append(autoSync).append(", loginInfoSet=").append(loginInformationSet).append(", firstBackup=").append(firstBackup).toString();

			log(context, message, preferences.isAppLogDebug());
		}
		return schedule;
	}

	private void log(Context context, String message, boolean appLog)
	{
		Log.d(TAG, message);
		if (appLog)
		{
			new AppLog(DateFormat.getDateFormatOrder(context)).appendAndClose(message);
		}
	}

	protected Alarms getAlarms(Context context)
	{
		return new Alarms(context);
	}

	protected Preferences getPreferences(Context context)
	{
		return new Preferences(context);
	}

	protected AuthPreferences getAuthPreferences(Context context)
	{
		return new AuthPreferences(context);
	}

}
