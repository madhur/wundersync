package in.co.madhur.wunderlistsync.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Date;

public class AppLog
{
	// keep max 32k worth of logs
	static final int MAX_SIZE = 32 * 1024;
	public static final int ID = 1;

	private PrintWriter writer;
	private String dateFormat;

	public AppLog(char[] format)
	{
		this(App.LOG, format);
	}

	private AppLog(String name, char[] format)
	{
		for (char c : format)
		{
			if (c == DateFormat.MONTH)
			{
				dateFormat = "MM-dd kk:mm";
				break;
			}
			if (c == DateFormat.DATE)
			{
				dateFormat = "dd-MM kk:mm";
				break;
			}
		}

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			final File logFile = getFile(name);
			if (logFile.isFile() && logFile.exists())
				rotate(logFile);

			try
			{
				writer = new PrintWriter(new FileWriter(logFile, true));
			}
			catch (IOException e)
			{
				Log.w(App.TAG, "error opening app log", e);
			}
		}
	}

	public void append(String s)
	{
		if (writer != null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(format(new Date())).append(" ").append(s);
			writer.println(sb);
			if (App.LOCAL_LOGV)
				Log.v(App.TAG, "[AppLog]: " + sb);
		}
	}

	public void appendAndClose(String s)
	{
		append(s);
		close();
	}

	public void close()
	{
		if (App.LOCAL_LOGV)
			Log.v(App.TAG, "AppLog#close()");
		if (writer != null)
			writer.close();
	}

	public CharSequence format(Date d)
	{
		return DateFormat.format(dateFormat, d);
	}

	private void rotate(final File logFile)
	{
		if (logFile.length() > MAX_SIZE)
		{
			if (App.LOCAL_LOGV)
				Log.v(App.TAG, "rotating logfile " + logFile);
			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						LineNumberReader r = new LineNumberReader(new FileReader(logFile));

						while (r.readLine() != null)
							;
						r.close();

						int keep = Math.round(r.getLineNumber() * 0.3f);
						if (keep > 0)
						{
							r = new LineNumberReader(new FileReader(logFile));

							while (r.readLine() != null
									&& r.getLineNumber() < keep)
								;

							File newFile = new File(logFile.getAbsolutePath()
									+ ".new");
							PrintWriter pw = new PrintWriter(new FileWriter(newFile));
							String line;
							while ((line = r.readLine()) != null)
								pw.println(line);

							pw.close();
							r.close();

							if (newFile.renameTo(logFile) && App.LOCAL_LOGV)
							{
								Log.v(App.TAG, "rotated file, new size = "
										+ logFile.length());
							}
						}
					}
					catch (IOException e)
					{
						Log.e(App.TAG, "error rotating file " + logFile, e);
					}
				}
			}.start();
		}
	}

	public static Dialog displayAsDialog(String name, Context context)
	{
		final int PAD = 5;
		final TextView view = new TextView(context);
		view.setId(ID);

		readLog(name, view);

		final ScrollView sView = new ScrollView(context)
		{
			{
				addView(view);
				setPadding(PAD, PAD, PAD, PAD);
			}

			@Override
			protected void onLayout(boolean changed, int l, int t, int r, int b)
			{
				super.onLayout(changed, l, t, r, b);
				scrollTo(0, view.getHeight());
			}
		};

		return new AlertDialog.Builder(context).setCustomTitle(null).setPositiveButton(android.R.string.ok, null).setView(sView).create();
	}

	public static boolean readLog(String name, TextView view)
	{
		return readLog(getFile(name), view);
	}

	public static boolean readLog(File f, TextView view)
	{
		StringBuilder text = new StringBuilder();
		if (view != null && f.exists())
		{
			BufferedReader br = null;
			try
			{
				br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null)
				{
					text.append(line);
					text.append('\n');
				}
			}
			catch (IOException e)
			{
				Log.e(App.TAG, "error reading", e);
			}
			finally
			{
				if (br != null)
				{
					try
					{
						br.close();
					}
					catch (IOException ignored)
					{
					}
				}
			}
			view.setText(text.length() > 0 ? text
					: view.getContext().getString(R.string.app_log_empty));
		}

		return text.length() > 0;
	}

	static File getFile(String name)
	{
		return new File(Environment.getExternalStorageDirectory(), name);
	}
}
