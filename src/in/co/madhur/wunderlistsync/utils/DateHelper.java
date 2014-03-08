package in.co.madhur.wunderlistsync.utils;

import in.co.madhur.wunderlistsync.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.api.client.util.DateTime;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

public class DateHelper
{
	public static Long persistDate(String dateStr)
	{
		try
		{
			if (TextUtils.isEmpty(dateStr))
				return (long) 0;

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));

			return format.parse(dateStr).getTime();
		}
		catch (ParseException e)
		{
			Log.e(App.TAG, "Parse exception :" + dateStr);
		}

		return (long) 0;

	}
	
	public static DateTime ConvertShortToGoogleFormat(String dateStr)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		DateTime d = null;
		try
		{
			d = new DateTime(sdf.parse(dateStr));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return d;
	}

	public static Date loadDate(Cursor cursor, int index)
	{
		if (cursor.isNull(index))
		{
			return null;
		}
		return new Date(cursor.getLong(index));
	}

	/**
	 * Helper class for handling ISO 8601 strings of the following format:
	 * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
	 */
	public static final class ISO8601
	{
		/** Transform ISO 8601 string to Calendar. */
		public static Long toDate(final String iso8601string)
				throws ParseException
		{
			Log.v(App.TAG, "Parsing " + iso8601string);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date=format.parse(iso8601string);
			return date.getTime();
		}
	}
}
