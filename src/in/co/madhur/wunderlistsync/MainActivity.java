package in.co.madhur.wunderlistsync;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.tasks.TasksScopes;

import in.co.madhur.wunderlistsync.service.AppPreferences;
import in.co.madhur.wunderlistsync.service.AppPreferences.Keys;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
{
	StatusPreference statusPrefence;
	GoogleAccountCredential credential;

	AppPreferences appPreferences;

	public com.google.api.services.tasks.Tasks service;
	public List<String> tasksList;
	public int numAsyncTasks;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.calendarlist);
		appPreferences = new AppPreferences(this);

	}

	@Override
	protected void onResume()
	{
		super.onResume();

	//	SetListeners();
	}

	/**
	 * 
	 */
//	private void SetListeners()
//	{
//		findPreference(Keys.CONNECTED.key).setOnPreferenceChangeListener(new OnPreferenceChangeListener()
//		{
//
//			@Override
//			public boolean onPreferenceChange(Preference preference, Object newValue)
//			{
//				boolean newVal = (Boolean) newValue;
//
//				if (newVal)
//				{
//					int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//					if (status != ConnectionResult.SUCCESS)
//					{
//						Log.e(App.TAG, String.valueOf(status));
//						Toast.makeText(getBaseContext(), getString(R.string.gps_missing), Toast.LENGTH_LONG).show();
//						finish();
//						return false;
//					}
//
//					credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(TasksScopes.TASKS));
//
//					// credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME,
//					// null));
//
//					// credential = GoogleAccountCredential.usingOAuth2(this,
//					// scopes);
//					if (TextUtils.isEmpty(appPreferences.GetUserName()))
//					{
//						try
//						{
//
//							startActivityForResult(credential.newChooseAccountIntent(), Consts.REQUEST_ACCOUNT_PICKER);
//						}
//						catch (ActivityNotFoundException e)
//						{
//
//							Toast.makeText(getBaseContext(), getString(R.string.gps_missing), Toast.LENGTH_LONG).show();
//
//							return true;
//						}
//
//					}
//					else
//					{
//
//					}
//
//				}
//				else
//				{
//
//				}
//
//				return true;
//			}
//		});
//
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
			case Consts.REQUEST_GOOGLE_PLAY_SERVICES:
				if (resultCode == Activity.RESULT_OK)
				{
					haveGooglePlayServices();
				}
				else
				{
					checkGooglePlayServicesAvailable();
				}
				break;
			case Consts.REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK)
				{
					 AsyncLoadTasks.run(this);
				}
				else
				{
					chooseAccount();
				}
				break;
			case Consts.REQUEST_ACCOUNT_PICKER:
				if (resultCode == Activity.RESULT_OK && data != null
						&& data.getExtras() != null)
				{
					String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null)
					{
						credential.setSelectedAccountName(accountName);
						appPreferences.SetUserName(accountName);
						 AsyncLoadTasks.run(this);
					}
				}
				break;
		}
	}

	// private void handleAccountManagerAuth(Intent data) {
	// String token =
	// data.getStringExtra(AccountManagerAuthActivity.EXTRA_TOKEN);
	// String account =
	// data.getStringExtra(AccountManagerAuthActivity.EXTRA_ACCOUNT);
	// if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(account)) {
	// authPreferences.setOauth2Token(account, token);
	// onAuthenticated();
	// } else {
	// String error =
	// data.getStringExtra(AccountManagerAuthActivity.EXTRA_ERROR);
	// if (!TextUtils.isEmpty(error)) {
	// show(Dialogs.ACCOUNTMANAGER_TOKEN_ERROR);
	// }
	// }
	// }

	private void haveGooglePlayServices()
	{
		// check if there is already an account selected
		if (credential.getSelectedAccountName() == null)
		{
			// ask user to choose account
			chooseAccount();
		}
		else
		{
			// load calendars
			// AsyncLoadTasks.run(this);
		}
	}

	public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode)
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, MainActivity.this, Consts.REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}

	private boolean checkGooglePlayServicesAvailable()
	{
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode))
		{
			// showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	private void chooseAccount()
	{
		startActivityForResult(credential.newChooseAccountIntent(), Consts.REQUEST_ACCOUNT_PICKER);
	}
	
	public void refreshView()
	{
		//adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tasksList);
		//listView.setAdapter(adapter);
	}

}
