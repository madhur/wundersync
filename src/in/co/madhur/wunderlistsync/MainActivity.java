package in.co.madhur.wunderlistsync;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.TasksScopes;
import com.squareup.otto.Subscribe;

import in.co.madhur.wunderlistsync.AppPreferences.Keys;
import in.co.madhur.wunderlistsync.api.AuthError;
import in.co.madhur.wunderlistsync.api.AuthException;
import in.co.madhur.wunderlistsync.api.WunderList;
import in.co.madhur.wunderlistsync.api.model.LoginResponse;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.database.DbHelper;
import in.co.madhur.wunderlistsync.gtasks.*;
import in.co.madhur.wunderlistsync.service.WunderSyncService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity
{
	StatusPreference statusPrefence;
	GoogleAccountCredential credential;

	AppPreferences appPreferences;
	// ArrayAdapter<String> adapter;

	public com.google.api.services.tasks.Tasks service;
	// public List<String> tasksList;
	// public int numAsyncTasks;
	// private ListView listView;

	final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();

	final com.google.api.client.json.JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// setContentView(R.layout.calendarlist);
		appPreferences = new AppPreferences(this);
		statusPrefence = new StatusPreference(this);

		getPreferenceScreen().addPreference(statusPrefence);
		// listView = (ListView) findViewById(R.id.list);
		// Google Accounts

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//
		// if (checkGooglePlayServicesAvailable())
		// {
		// haveGooglePlayServices();
		// }
		App.getEventBus().register(statusPrefence);
		SetListeners();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		App.getEventBus().unregister(statusPrefence);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * 
	 */

	public void StartSync()
	{

		if (appPreferences.isEmptyCred())
		{
			showDialog(Dialogs.MISSING_CREDENTIALS.ordinal());
			return;
		}
		
		if(appPreferences.GetUserName().equalsIgnoreCase(""))
		{
			
			showDialog(Dialogs.MISSING_GOOGLE_CONNECT.ordinal());
			return;
		}

		Intent syncIntent = new Intent();
		syncIntent.setClass(this, WunderSyncService.class);
		this.startService(syncIntent);

	}

	@Override
	protected Dialog onCreateDialog(final int id)
	{
		String title = null, msg = null;
		switch (Dialogs.values()[id])
		{
			case MISSING_CREDENTIALS:
				title = getString(R.string.app_name);
				msg = getString(R.string.missing_wunderlist_cred);
				break;

			case MISSING_GOOGLE_CONNECT:
				title=getString(R.string.app_name);
				msg=getString(R.string.missing_google_cred);
				break;
				
			case START_SYNC:
				title=getString(R.string.app_name);
				msg=getString(R.string.start_sync_dialog);
				return new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						MainActivity.this.StartSync();
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						
					}
				}).create();
				
				
			default:
				return null;
		}
		return createMessageDialog(title, msg);
	}

	private Dialog createMessageDialog(String title, String msg)
	{
		return new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		}).create();
	}

	private void SetListeners()
	{
		findPreference(Keys.GOOGLE_CONNECTED.key).setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				boolean newVal = (Boolean) newValue;

				if (newVal)
				{
					int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
					if (status != ConnectionResult.SUCCESS)
					{
						Log.e(App.TAG, String.valueOf(status));
						Toast.makeText(getBaseContext(), getString(R.string.gps_missing), Toast.LENGTH_LONG).show();
						finish();
						return false;
					}

					credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(TasksScopes.TASKS));

					if (TextUtils.isEmpty(appPreferences.GetUserName()))
					{
						try
						{

							startActivityForResult(credential.newChooseAccountIntent(), Consts.REQUEST_ACCOUNT_PICKER);
						}
						catch (ActivityNotFoundException e)
						{

							Toast.makeText(getBaseContext(), getString(R.string.gps_missing), Toast.LENGTH_LONG).show();

							return true;
						}

					}
					else
					{

						credential.setSelectedAccountName(appPreferences.GetUserName());
						service = new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential).setApplicationName("WunderSync").build();
						preference.setSummary(String.format(getString(R.string.connected_string), appPreferences.GetUserName()));
						// getString(R.string.)
					}

				}
				else
				{
					credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(TasksScopes.TASKS));

					credential.setSelectedAccountName("");
					appPreferences.SetUserName("");
					preference.setSummary(getString(R.string.not_connected));
					appPreferences.SetMetadata(Keys.LAST_SYNC_DATE, "");
				}

				return true;
			}
		});

		findPreference(Keys.SELECT_SYNC_LISTS.key).setOnPreferenceClickListener(new OnPreferenceClickListener()
		{

			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				if (appPreferences.isEmptyCred())
				{

					showDialog(Dialogs.MISSING_CREDENTIALS.ordinal());
					return true;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				final View loginView = inflater.inflate(R.layout.select_lists, null);

				builder.setTitle(R.string.select_sync_list).setPositiveButton(android.R.string.ok, null).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// User cancelled the dialog
					}
				}).setView(loginView).setNeutralButton(R.string.refresh, null);

				AlertDialog dialog = builder.create();

				dialog.setOnShowListener(new OnShowListener()
				{
					ListView listView;
					ProgressBar progressBar;
					TextView statusMessage;

					@Override
					public void onShow(final DialogInterface dialog)
					{
						Log.v(App.TAG, "onShow");

						Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
						// Button cancelButton = ((AlertDialog)
						// dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
						Button refreshButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);

						listView = (ListView) loginView.findViewById(R.id.wunder_listview);
						listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

						progressBar = (ProgressBar) loginView.findViewById(R.id.pbHeaderProgress);
						statusMessage = (TextView) loginView.findViewById(R.id.statusMessage);

						final AppPreferences preferences = new AppPreferences(MainActivity.this);
						final ListSyncConfig config = new ListSyncConfig(true, true, null, false);

						config.setUsername(preferences.GetWunderUserName());
						config.setPassword(preferences.GetWunderPassword());
						config.setToken(preferences.GetMetadata(Keys.TOKEN));

						Log.e(App.TAG, "Firing get list task");
						new WunderListsSyncTask(config).execute(config);

						b.setOnClickListener(new View.OnClickListener()
						{

							@Override
							public void onClick(View view)
							{

								// Save into old_lists table into database along
								// with selected values
								HashMapAdapter adapter = (HashMapAdapter) listView.getAdapter();
								long[] checkIds = listView.getCheckedItemIds();

								dialog.dismiss();
							}
						});

						refreshButton.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								if (!Connection.isNetworkGood(MainActivity.this))
								{
									Toast.makeText(MainActivity.this, getString(R.string.error_refresh_internet), Toast.LENGTH_LONG).show();
									return;
								}

								final ListSyncConfig config = new ListSyncConfig(true, true, null, true);

								config.setUsername(preferences.GetWunderUserName());
								config.setPassword(preferences.GetWunderPassword());
								config.setToken(preferences.GetMetadata(Keys.TOKEN));

								Log.e(App.TAG, "Firing get list task");
								new WunderListsSyncTask(config).execute(config);

							}
						});

					}

					class WunderListsSyncTask extends
							AsyncTask<ListSyncConfig, Void, WListResult>
					{
						ListSyncConfig config;

						public WunderListsSyncTask(ListSyncConfig config)
						{
							this.config = config;
						}

						@Override
						protected void onPreExecute()
						{
							super.onPreExecute();
							progressBar.setVisibility(ProgressBar.VISIBLE);
							statusMessage.setVisibility(TextView.GONE);
						}

						@Override
						protected WListResult doInBackground(ListSyncConfig... params)
						{
							ListSyncConfig config = params[0];
							WunderList wunderList = null;
							DbHelper dbHelper = null;
							try
							{
								List<WList> lists;
								dbHelper = DbHelper.getInstance(MainActivity.this);

								if (config.isForceRefresh())
								{
									Log.d(App.TAG, "Its a force refresh");

									if (!TextUtils.isEmpty(params[0].getToken()))
									{

										try
										{
											wunderList = WunderList.getInstance(params[0].getToken());
										}
										catch (AuthException e)
										{
											Log.v(App.TAG, "Old token expired, getting new token...");
											if (e.getErrorCode() == AuthError.OLD_TOKEN_EXPIRED)
											{
												wunderList = WunderList.getInstance(config.getUsername(), config.getPassword());
												Log.v(App.TAG, "New token: "
														+ wunderList.GetToken());
											}

										}
									}
									else
									{
										wunderList = WunderList.getInstance(config.getUsername(), config.getPassword());
										appPreferences.SetMetadata(Keys.TOKEN, wunderList.GetToken());

									}

									// Get list from REST API
									lists = wunderList.GetLists();

									// purge the previous data
									dbHelper.TruncateListsOld();

									// Write lists into the database
									dbHelper.WriteListsOld(lists);

								}
								else
								{

									lists = dbHelper.ReadLists();

									if (lists.size() == 0)
									{
										Log.d(App.TAG, "Empty list from db, getting from wunderlist");

										if (!TextUtils.isEmpty(params[0].getToken()))
										{

											try
											{
												wunderList = WunderList.getInstance(params[0].getToken());
											}
											catch (AuthException e)
											{
												Log.v(App.TAG, "Old token expired, getting new token...");
												if (e.getErrorCode() == AuthError.OLD_TOKEN_EXPIRED)
												{
													wunderList = WunderList.getInstance(config.getUsername(), config.getPassword());
													Log.v(App.TAG, "New token: "
															+ wunderList.GetToken());
												}

											}
										}
										else
										{
											wunderList = WunderList.getInstance(config.getUsername(), config.getPassword());
											appPreferences.SetMetadata(Keys.TOKEN, wunderList.GetToken());

										}

										// Get list from REST API
										lists = wunderList.GetLists();

										// purge the previous data
										dbHelper.TruncateListsOld();

										// Write lists into the database
										dbHelper.WriteListsOld(lists);
									}
								}

								return new WListResult(lists);

							}
							catch (Exception e)
							{

								if (e.getMessage() != null)
								{
									Log.e(App.TAG, e.getMessage());
									return new WListResult(e.getMessage());
								}

								return new WListResult(e.getMessage());

							}

						}

						@Override
						protected void onPostExecute(WListResult result)
						{
							super.onPostExecute(result);
							if (result != null && result.isError() == false)
							{
								HashMapAdapter adapter = new HashMapAdapter(result.getLists());
								listView.setAdapter(adapter);
								listView.setVisibility(ListView.VISIBLE);
							}
							else
							{
								statusMessage.setText(result.getErrorMessage());
								statusMessage.setVisibility(TextView.VISIBLE);
								listView.setVisibility(ListView.GONE);
							}

							progressBar.setVisibility(ProgressBar.GONE);

						}

					}

				});

				dialog.show();

				return true;

			}
		});

		findPreference(Keys.WUNDER_CREDENTIALS.key).setOnPreferenceClickListener(new OnPreferenceClickListener()
		{

			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// Inflate and set the layout for the dialog
				// Pass null as the parent view because its going in the dialog
				// layout
				final View loginView = inflater.inflate(R.layout.login, null);

				builder.setMessage(R.string.wunderlist_cred_dialog).setPositiveButton(android.R.string.ok, null).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// User cancelled the dialog
					}
				}).setView(loginView);

				AlertDialog dialog = builder.create();

				dialog.setOnShowListener(new OnShowListener()
				{
					EditText userName = (EditText) loginView.findViewById(R.id.username);
					EditText password = (EditText) loginView.findViewById(R.id.password);

					@Override
					public void onShow(final DialogInterface dialog)
					{
						Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
						userName.setText(appPreferences.GetWunderUserName());

						password.setText(appPreferences.GetWunderPassword());

						b.setOnClickListener(new View.OnClickListener()
						{

							@Override
							public void onClick(View view)
							{

								if (userName.getText().length() == 0
										|| password.getText().length() == 0)
								{
									new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.app_name)).setMessage(getString(R.string.error_empty_cred)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
									{
										public void onClick(DialogInterface dialog, int which)
										{

										}
									})

									.show();

									return;

								}

								appPreferences.SetWunderUserName(userName.getText().toString());
								appPreferences.SetWunderPassword(password.getText().toString());

								dialog.dismiss();
							}
						});

					}
				});
				dialog.show();

				return true;
			}
		});

	}

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
					findPreference(Keys.GOOGLE_CONNECTED.key).setSummary(String.format(getString(R.string.connected_string), appPreferences.GetUserName()));
					
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
					

					findPreference(Keys.GOOGLE_CONNECTED.key).setSummary(String.format(getString(R.string.connected_string), appPreferences.GetUserName()));
					
					showSyncDialog();

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
						findPreference(Keys.GOOGLE_CONNECTED.key).setSummary(String.format(getString(R.string.connected_string), appPreferences.GetUserName()));
						// AsyncLoadTasks.run(this);
					}
				}
				break;
		}
	}

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
			showSyncDialog();
			
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
	
	private void showSyncDialog()
	{
		if(!appPreferences.isEmptyCred() && appPreferences.isGoogleConnected())
			showDialog(Dialogs.START_SYNC.ordinal());
		
	}

	private boolean checkGooglePlayServicesAvailable()
	{
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode))
		{
			 showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	private void chooseAccount()
	{
		credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(TasksScopes.TASKS));
		startActivityForResult(credential.newChooseAccountIntent(), Consts.REQUEST_ACCOUNT_PICKER);
	}

}
