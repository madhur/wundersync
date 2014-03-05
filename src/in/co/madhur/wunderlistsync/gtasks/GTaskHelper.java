package in.co.madhur.wunderlistsync.gtasks;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.database.DbHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class GTaskHelper
{

	private static com.google.api.services.tasks.Tasks client;
	private static GTaskHelper taskHelper;
	private static Context context;

	private GTaskHelper()
	{

	}

	public static GTaskHelper GetInstance(com.google.api.services.tasks.Tasks client, Context context)
	{
		if (taskHelper == null)
		{

			GTaskHelper.client = client;
			taskHelper = new GTaskHelper();
			GTaskHelper.context = context;
			return taskHelper;
		}
		else
			return taskHelper;

	}

	// lists - List of all WunderList lists
	// selectedListIds - Lists selected by user to sync
	public void CreateOrEnsureLists(List<WList> lists, String[] selectedListIds)
			throws IOException
	{
		// Get all lists @Google initially
		List<TaskList> taskLists2 = client.tasklists().list().execute().getItems();

		//MarkListstoSync(selectedListIds);

		for (int i = 0; i < lists.size(); ++i)
		{
			if (Arrays.asList(selectedListIds).contains(lists.get(i).getId()))
			{
				CreateListIfNotExist(lists.get(i), taskLists2);
			}

		}

	}

	private void CreateListIfNotExist(WList wList, List<TaskList> taskLists2)
			throws IOException
	{
		DbHelper dbHelper = DbHelper.getInstance(context);
		String google_list_id = dbHelper.getGoogleListIdofWlist(wList.getId());

		if (TextUtils.isEmpty(google_list_id))
		{
			// There is no corresponding google list id set... We will have to create a new one

				Log.d(App.TAG, "Creating google list: " + wList.getTitle());
				
				TaskList taskList;
				taskList = new TaskList();
				taskList.setTitle(wList.getTitle());
				TaskList newtaskList=client.tasklists().insert(taskList).execute();
				
				//Update db to reflect the corresponding google id and set the synced field
				dbHelper.setGoogleListofWlist(wList.getId(),  newtaskList.getId());
				

		}
		else
			Log.d(App.TAG, wList.getTitle() + " Already exists on google with id " + google_list_id);

	}

}
