package in.co.madhur.wunderlistsync.service;

import android.util.Log;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.calendar.CalendarAccessor;


class CalendarSyncer
{
	private final CalendarAccessor calendarAccessor;
	private final long calendarId;
	private boolean syncEnabled;

	CalendarSyncer(CalendarAccessor calendarAccessor, long calendarId)
	{
		this.calendarAccessor = calendarAccessor;
		this.calendarId = calendarId;
	}

	public void syncCalendar()
	{
		enableSync();

//		for (Map<String, String> m : result.getMapList())
//		{
//			try
//			{
////				final int duration = Integer.parseInt(m.get(CallLog.Calls.DURATION));
////				final int callType = Integer.parseInt(m.get(CallLog.Calls.TYPE));
////				final String number = m.get(CallLog.Calls.NUMBER);
////				final Date then = new Date(Long.valueOf(m.get(CallLog.Calls.DATE)));
//
//				// insert into calendar
//				//calendarAccessor.addEntry(calendarId, then, duration, callFormatter.callTypeString(callType, record.getName()), callFormatter.formatForCalendar(callType, record.getNumber(), duration));
//			}
//			catch (NumberFormatException e)
//			{
//				Log.w(App.TAG, "error", e);
//			}
//		}
	}

	private void enableSync()
	{
		if (!syncEnabled)
		{
			calendarAccessor.enableSync(calendarId);
			syncEnabled = true;
		}
	}
}
