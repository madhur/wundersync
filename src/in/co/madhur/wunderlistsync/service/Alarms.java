package in.co.madhur.wunderlistsync.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.Consts.SyncType;

import static in.co.madhur.wunderlistsync.App.*;

import java.util.UUID;

public class Alarms
{
	private final AppPreferences mPreferences;
	private Context mContext;

	public Alarms(Context context)
	{
		this(context.getApplicationContext(), new AppPreferences(context));
	}

	Alarms(Context context, AppPreferences preferences)
	{
		mContext = context.getApplicationContext();
		mPreferences = preferences;
	}

	

	public long scheduleRegularBackup()
	{
		return scheduleBackup(mPreferences.getSchedule(), SyncType.REGULAR);
	}

	public long scheduleBootupBackup()
	{
		return scheduleBackup(mPreferences.getSchedule(),  SyncType.REGULAR);
	}

	public void cancel()
	{
		getAlarmManager(mContext).cancel(createPendingIntent(mContext, SyncType.UNKNOWN));
	}

	private long scheduleBackup(int inSeconds, SyncType syncType)
	{
		

		if (mPreferences.isAutoSync() && inSeconds > 0)
		{
			final long atTime = System.currentTimeMillis()
					+ (inSeconds * 1000l);
			// getAlarmManager(mContext).set(AlarmManager.RTC_WAKEUP, atTime, createPendingIntent(mContext, syncType));
			getAlarmManager(mContext).setInexactRepeating(AlarmManager.RTC_WAKEUP, atTime, inSeconds*1000, createPendingIntent(mContext, syncType));
			if (LOCAL_LOGV)
			{
				Log.v(TAG, "Scheduled sync due "
						+ (inSeconds > 0 ? "in " + inSeconds + " seconds"
								: "now"));
			}
			return atTime;
		}
		else
		{
			if (LOCAL_LOGV)
				Log.v(TAG, "Not scheduling sync because auto sync is disabled.");
			return -1;
		}
	}

	private static AlarmManager getAlarmManager(Context ctx)
	{
		return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	}

	private static PendingIntent createPendingIntent(Context ctx, SyncType backupType)
	{
		final UUID uuid = UUID.randomUUID();

		final Intent intent = (new Intent(ctx, WunderSyncService.class)).setAction(backupType.name()
				+ "-" + uuid.toString());

		return PendingIntent.getService(ctx, 0, intent, 0);
	}
}
