package in.co.madhur.wunderlistsync.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppPreferences
{
	private Context context;
	public SharedPreferences sharedPreferences;
	
	public enum Keys
	{
		CONNECTED("connected"),
		ENABLE_AUTO_SYNC("enable_auto_sync"),
		AUTO_SYNC_SCHEDULE("auto_backup_schedule"),
		ENABLE_WIFI_ONLY("wifi_only"),
		USER_NAME_GOOGLE("user_name_google"),
		LAST_SYNC_DATE("last_sync_date"),
		WUNDER_USERNAME("wunder_username"),
		WUNDER_PASSWORD("wunder_password");
		
		
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
		String defValue="";
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
	
	public boolean isOnlyWifi()
	{
		
		return sharedPreferences.getBoolean(Keys.ENABLE_WIFI_ONLY.key, false);
	}
	
	public boolean isAutoSync()
	{
		
		return sharedPreferences.getBoolean(Keys.ENABLE_AUTO_SYNC.key, false);
		
	}

}
