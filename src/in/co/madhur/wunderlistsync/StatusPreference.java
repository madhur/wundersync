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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StatusPreference extends Preference implements
		View.OnClickListener
{

	private MainActivity mainActivity;
	private Button syncButton;
	private View mView;
	private ProgressBar syncBar;
	private TextView syncStatus, statusLabel;
	private ImageView statusIcon;

	public StatusPreference(MainActivity context)
	{
		super(context);
		this.mainActivity = context;
		this.setSelectable(false);
		this.setOrder(1);

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
			statusLabel = (TextView) mView.findViewById(R.id.status_label);
			statusIcon = (ImageView) mView.findViewById(R.id.status_icon);
			return mView;
		}
		else
			return mView;
	}

	@Subscribe
	public void PublishProgress(TaskSyncState syncState)
	{
		Log.v(App.TAG, "PublishProgress");
		switch (syncState.getState())
		{
			case ERROR:
				setStatus(R.string.state_error, syncState.getErrorMessage());
				setButtonLabel(R.string.sync);
				syncBar.setIndeterminate(false);
				statusLabel.setText(mainActivity.getString(R.string.error));
				statusIcon.setImageResource(R.drawable.ic_error);
				break;

			case FETCH_GOOGLE_TASKS:
				syncBar.setIndeterminate(true);
				setStatus(R.string.state_google_tasks);
				setButtonLabel(R.string.stop_sync);
				statusIcon.setImageResource(R.drawable.ic_syncing);
				break;

			case FETCH_WUNDERLIST_TASKS:
				syncBar.setIndeterminate(true);
				setStatus(R.string.state_wunder_tasks);
				setButtonLabel(R.string.stop_sync);
				statusIcon.setImageResource(R.drawable.ic_syncing);
				break;

			case FINISHED:
				setStatus(R.string.state_finished);
				syncBar.setIndeterminate(false);
				setButtonLabel(R.string.sync);
				statusIcon.setImageResource(R.drawable.ic_idle);
				break;

			case LOGIN:
				setStatus(R.string.state_login);
				syncBar.setIndeterminate(true);
				setButtonLabel(R.string.stop_sync);
				statusIcon.setImageResource(R.drawable.ic_syncing);
				break;

			case SYNCING:
				syncBar.setIndeterminate(false);
				setStatus(R.string.state_syncing);
				setButtonLabel(R.string.stop_sync);
				statusIcon.setImageResource(R.drawable.ic_syncing);
				break;

			case USER_RECOVERABLE_ERROR:
				mainActivity.startActivityForResult(syncState.getAuthIntent(), Consts.REQUEST_AUTHORIZATION);
				setStatus(R.string.state_finished);
				syncBar.setIndeterminate(false);
				setButtonLabel(R.string.sync);
				statusIcon.setImageResource(R.drawable.ic_syncing);
		}

	}

	@Override
	public void onClick(View v)
	{
		if (v == syncButton)
		{
			if (WunderSyncService.IsSyncTaskRunning())
			{
				// Sync is already running. Terminate it.
				Log.d(App.TAG, "Sync task is running. Cancel requested");
				
				

			}
			else
			{
				// Sync is not running. Start it.
				if (!Connection.isNetworkGood(mainActivity))
				{
					setStatus(R.string.error_sync);
					return;
				}

				mainActivity.StartSync();
			}

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
