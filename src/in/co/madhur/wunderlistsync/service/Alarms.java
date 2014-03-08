package in.co.madhur.wunderlistsync.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.UUID;

public class Alarms
{
	private static final int BOOT_BACKUP_DELAY = 60;

	private final Preferences mPreferences;
	private Context mContext;

	public Alarms(Context context)
	{
		this(context.getApplicationContext(), new Preferences(context));
	}

	Alarms(Context context, Preferences preferences)
	{
		mContext = context.getApplicationContext();
		mPreferences = preferences;
	}

	public long scheduleIncomingBackup()
	{
		return scheduleBackup(mPreferences.getIncomingTimeoutSecs(), INCOMING, false);
	}

	public long scheduleRegularBackup()
	{
		return scheduleBackup(mPreferences.getRegularTimeoutSecs(), REGULAR, false);
	}

	public long scheduleBootupBackup()
	{
		return scheduleBackup(BOOT_BACKUP_DELAY, REGULAR, false);
	}

	public long scheduleImmediateBackup()
	{
		return scheduleBackup(-1, BROADCAST_INTENT, true);
	}

	public void cancel()
	{
		getAlarmManager(mContext).cancel(createPendingIntent(mContext, UNKNOWN));
	}

	private long scheduleBackup(int inSeconds, BackupType backupType, boolean force)
	{
		if (LOCAL_LOGV)
		{
			Log.v(TAG, "scheduleBackup(" + mContext + ", " + inSeconds + ", "
					+ backupType + ", " + force + ")");
		}

		if (force || (mPreferences.isEnableAutoSync() && inSeconds > 0))
		{
			final long atTime = System.currentTimeMillis()
					+ (inSeconds * 1000l);
			getAlarmManager(mContext).set(AlarmManager.RTC_WAKEUP, atTime, createPendingIntent(mContext, backupType));
			if (LOCAL_LOGV)
			{
				Log.v(TAG, "Scheduled backup due "
						+ (inSeconds > 0 ? "in " + inSeconds + " seconds"
								: "now"));
			}
			return atTime;
		}
		else
		{
			if (LOCAL_LOGV)
				Log.v(TAG, "Not scheduling backup because auto sync is disabled.");
			return -1;
		}
	}

	private static AlarmManager getAlarmManager(Context ctx)
	{
		return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	}

	private static PendingIntent createPendingIntent(Context ctx, BackupType backupType)
	{
		final UUID uuid = UUID.randomUUID();

		final Intent intent = (new Intent(ctx, SmsBackupService.class)).setAction(backupType.name()
				+ "-" + uuid.toString()) // create fresh pending intent
		.putExtra(BackupType.EXTRA, backupType.name());

		return PendingIntent.getService(ctx, 0, intent, 0);
	}
}
