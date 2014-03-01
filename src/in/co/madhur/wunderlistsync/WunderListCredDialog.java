package in.co.madhur.wunderlistsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class WunderListCredDialog extends AlertDialog
{
	private Context context;

	protected WunderListCredDialog(Context context)
	{
		super(context);
		this.context=context;
	}

	public interface DialogListener
	{
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		 View loginView=inflater.inflate(R.layout.login, null);
		    
		    
		builder.setMessage(R.string.preferences_credentials_desc).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// FIRE ZE MISSILES!
			}
		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// User cancelled the dialog
			}
		}).setView(loginView);
		// Create the AlertDialog object and return it
	}
	
	@Override
	public void setView(View view)
	{
		// TODO Auto-generated method stub
		super.setView(view);
	}
}
