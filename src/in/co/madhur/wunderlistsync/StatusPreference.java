package in.co.madhur.wunderlistsync;

import com.squareup.otto.Subscribe;

import in.co.madhur.wunderlistsync.service.WunderSyncService;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusPreference extends Preference implements
		View.OnClickListener
{

	private MainActivity mainActivity;
	private Button syncButton;
	private View mView;
	private ProgressBar syncBar;
	private TextView syncStatus, statusLabel;

	public StatusPreference(MainActivity context)
	{
		super(context);
		this.mainActivity = context;
		this.setSelectable(false);
		this.setOrder(0);
		App.getEventBus().register(this);
	}
	
	

	@Override
	protected View onCreateView(ViewGroup parent)
	{
		super.onCreateView(parent);
		if (mView == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = layoutInflater.inflate(R.layout.status, null);
			syncButton = (Button) mView.findViewById(R.id.sync_button);
			syncBar = (ProgressBar) mView.findViewById(R.id.details_sync_progress);
			syncButton.setOnClickListener(this);
			syncStatus = (TextView) mView.findViewById(R.id.details_sync_label);
			statusLabel=(TextView) mView.findViewById(R.id.status_label);
			return mView;
		}
		else
			return mView;
	}
	
	@Subscribe
	public void PublishProgress(TaskSyncState syncState)
	{
		Log.v(App.TAG, "PublishProgress");
		switch(syncState.getState())
		{
			case ERROR:
				setStatus(R.string.state_error, syncState.getErrorMessage());
				setButtonLabel(R.string.sync);
				syncBar.setIndeterminate(false);
				break;
				
			case FETCH_GOOGLE_TASKS:
				syncBar.setIndeterminate(true);
				setStatus(R.string.state_google_tasks);
				setButtonLabel(R.string.stop_sync);
				break;
				
			case FETCH_WUNDERLIST_TASKS:
				syncBar.setIndeterminate(true);
				setStatus(R.string.state_wunder_tasks);
				setButtonLabel(R.string.stop_sync);
				break;
				
			case FINISHED:
				syncBar.setProgress(syncBar.getMax());
				setStatus(R.string.state_finished);
				setButtonLabel(R.string.sync);
				break;
				
			case LOGIN:
				setStatus(R.string.state_login);
				syncBar.setIndeterminate(true);
				setButtonLabel(R.string.stop_sync);
				break;
				
			case SYNCING:
				syncBar.setIndeterminate(false);
				setStatus(R.string.state_syncing);
				setButtonLabel(R.string.stop_sync);
				break;
			
		}
		
		
	}



	@Override
	public void onClick(View v)
	{
		if (v == syncButton)
		{
			if (!Connection.isNetworkGood(mainActivity))
			{
				setStatus(R.string.error_sync);
				return;
			}
			
			mainActivity.StartSync();

		}

	}

	private void setButtonLabel(int buttonLabel)
	{
		syncButton.setText(mainActivity.getString(buttonLabel));
		
	}
	

	private void setStatus(int status)
	{
		syncStatus.setText(mainActivity.getString(status));
		statusLabel.setText(mainActivity.getString(status));
	}
	
	private void setStatus(int status, String additionalInfo)
	{
		syncStatus.setText(String.format(mainActivity.getString(status), additionalInfo));

	}
	


}
