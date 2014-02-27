package in.co.madhur.wunderlistsync;

import com.squareup.otto.Subscribe;

import in.co.madhur.wunderlistsync.service.WunderSyncService;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusPreference extends Preference implements
		View.OnClickListener
{

	private Context context;
	private Button syncButton;
	private View mView;
	private ProgressBar syncBar;
	private TextView syncStatus;

	public StatusPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected View onCreateView(ViewGroup parent)
	{
		super.onCreateView(parent);
		if (mView == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = layoutInflater.inflate(R.layout.status, null);
			syncButton = (Button) mView.findViewById(R.id.sync_button);
			syncBar = (ProgressBar) mView.findViewById(R.id.details_sync_progress);
			syncButton.setOnClickListener(this);
			syncStatus = (TextView) mView.findViewById(R.id.details_sync_label);
			return mView;
		}
		else
			return mView;
	}
	


	@Override
	public void onClick(View v)
	{
		if (v == syncButton)
		{
			if (Connection.isNetworkGood(context))
			{
				startSync();
			}
			else
				setStatus(R.string.error_sync);

		}

	}

	

	private void setStatus(int errorSync)
	{
		syncStatus.setText(context.getString(errorSync));

	}

	private void startSync()
	{
		Intent syncIntent = new Intent();
		syncIntent.setClass(context, WunderSyncService.class);
		context.startService(syncIntent);

	}

}
