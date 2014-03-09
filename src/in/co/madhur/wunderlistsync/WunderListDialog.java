package in.co.madhur.wunderlistsync;

import in.co.madhur.wunderlistsync.AppPreferences.Keys;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class WunderListDialog extends AlertDialog
{
	ListView listView;
	Context context;
	AppPreferences preferences;
	Button OkButton, CancelButton;
	
	protected WunderListDialog(Context context)
	{
		super(context);
		this.context = context;
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setTitle("Select WunderList lists to sync");
		
		
		setButton(BUTTON_NEGATIVE, "Cancel",  new OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(context, "text1", Toast.LENGTH_LONG).show();
				
			}
		});
		
		setButton(BUTTON_POSITIVE, "OK",  new OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(context, "text2", Toast.LENGTH_LONG).show();
				
			}
		});
		
		
		Rect displayRectangle = new Rect();
		Window window = this.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		window.setBackgroundDrawableResource(R.color.black);
		
		// inflate and adjust layout
		
		
		
		setContentView(R.layout.select_lists);

		listView = (ListView) this.findViewById(R.id.wunder_listview);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		preferences = new AppPreferences(context);
		final TaskSyncConfig config = new TaskSyncConfig(true, true, null);

		config.setUsername(preferences.GetWunderUserName());
		config.setPassword(preferences.GetWunderPassword());
		config.setToken(preferences.GetMetadata(Keys.TOKEN));
		config.setGoogleAccount(preferences.GetUserName());

		this.setOnShowListener(new OnShowListener()
		{

			@Override
			public void onShow(DialogInterface dialog)
			{
				//new WunderListsSyncTask(config).execute(config);

			}
		});
	}

	

}
