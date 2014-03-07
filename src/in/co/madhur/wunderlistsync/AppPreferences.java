package in.co.madhur.wunderlistsync;

import java.io.UnsupportedEncodingException;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class AppPreferences
{
	private Context context;
	public SharedPreferences sharedPreferences;
	String defValue="";
	
	public enum Keys
	{
		GOOGLE_CONNECTED("connected"),
		ENABLE_AUTO_SYNC("enable_auto_sync"),
		AUTO_SYNC_SCHEDULE("auto_backup_schedule"),
		ENABLE_WIFI_ONLY("wifi_only"),
		USER_NAME_GOOGLE("user_name_google"),
		LAST_SYNC_DATE("last_sync_date"),
		WUNDER_USERNAME("wunder_username"),
		WUNDER_PASSWORD("wunder_password"),
		TOKEN("wunder_token"),
		WUNDER_CREDENTIALS("wunder_credentials"),
		SELECT_SYNC_LISTS("select_sync_list"),
		ENABLE_CALENDAR_SYNC("enable_sync_calendar"),
		CALENDAR_SYNC("sync_calendar");
		
		
		
		public final String key;

		private Keys(String key)
		{
			this.key = key;

		}

		
		
	};
	
	public AppPreferences(Context context)
	{
		this.context=context;
		this.sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	
	public String GetMetadata(Keys key)
	{
		
		return sharedPreferences.getString(key.key,defValue);
		
	}
	
	public void SetMetadata(Keys key, String value)
	{
		Editor edit=sharedPreferences.edit();
		edit.putString(key.key, value);
		edit.commit();
		
	}
	
	public String GetUserName()
	{
		return sharedPreferences.getString(Keys.USER_NAME_GOOGLE.key, "");
		
	}
	
	public void SetUserName(String username)
	{
		Editor edit=sharedPreferences.edit();
		edit.putString(Keys.USER_NAME_GOOGLE.key, username);
		edit.commit();
		
	}
	
	public void SetWunderUserName(String username)
	{
		Editor edit=sharedPreferences.edit();
		edit.putString(Keys.WUNDER_USERNAME.key, username);
		edit.commit();
		
	}
	
	public String GetWunderUserName()
	{
		return sharedPreferences.getString(Keys.WUNDER_USERNAME.key, defValue);
	}
	
	public boolean isGoogleConnected()
	{
		return sharedPreferences.getBoolean(Keys.GOOGLE_CONNECTED.key, false);
		
	}
	
	
	
	public String[] getSelectedListsIds()
	{
		String lists=GetMetadata(Keys.SELECT_SYNC_LISTS);
		
		return lists.split(";");
		
	}
	
	public String GetWunderPassword()
	{
		
		String encPassword=GetMetadata(Keys.WUNDER_PASSWORD);
		if(TextUtils.isEmpty(encPassword))
			return defValue;
		
		String password = null; 
		
		
		try
		{
			byte[] data = Base64.decode(encPassword, Base64.DEFAULT);
			password = new String(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{
			Log.e(App.TAG, e1.getMessage());
		}
		catch(IllegalArgumentException e1)
		{
			Log.e(App.TAG, e1.getMessage());
		}
		
		return password;
		
	}
	
	public void SetWunderPassword(String password)
	{
		byte[] data = null;
		String base64 = null;
		try
		{
			data = password.getBytes("UTF-8");
			base64 = Base64.encodeToString(data, Base64.DEFAULT);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e(App.TAG, e.getMessage());
		}
		
		Editor edit=sharedPreferences.edit();
		edit.putString(Keys.WUNDER_PASSWORD.key, base64);
		edit.commit();
		
	}
	
	public boolean isEmptyCred()
	{
		String userName=GetMetadata(Keys.WUNDER_USERNAME);
		String password=GetMetadata(Keys.WUNDER_PASSWORD);
		
		if(userName.trim().length()==0 || password.trim().length()==0)
			return true;
		
		return false;
		
	}
	
	public boolean isOnlyWifi()
	{
		
		return sharedPreferences.getBoolean(Keys.ENABLE_WIFI_ONLY.key, false);
	}
	
	public boolean isAutoSync()
	{
		
		return sharedPreferences.getBoolean(Keys.ENABLE_AUTO_SYNC.key, false);
		
	}

}
