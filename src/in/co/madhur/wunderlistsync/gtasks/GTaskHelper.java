package in.co.madhur.wunderlistsync.gtasks;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.api.model.WTask;
import in.co.madhur.wunderlistsync.database.DbHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
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
		// TODO: For the first time sync, we need to ensure that if the list
		// with same title exist, we re-use that one instead of creating a new
		// one.
		List<TaskList> taskLists2 = client.tasklists().list().execute().getItems();

		// MarkListstoSync(selectedListIds);

		for (int i = 0; i < lists.size(); ++i)
		{
			if (Arrays.asList(selectedListIds).contains(lists.get(i).getId()))
			{
				CreateListIfNotExist(lists.get(i), taskLists2);
			}

		}

	}

	public void DeleteEmptyLists() throws IOException
	{
		List<TaskList> taskLists2 = client.tasklists().list().execute().getItems();

		// MarkListstoSync(selectedListIds);

		for (int i = 0; i < taskLists2.size(); ++i)
		{
			List<Task> tasks = client.tasks().list(taskLists2.get(i).getId()).execute().getItems();
			if (tasks != null)
			{
				if (tasks.size() == 0)
				{
					Log.d(App.TAG, "Deleting list empty "
							+ taskLists2.get(i).getTitle());
					client.tasklists().delete(taskLists2.get(i).getId());
				}
			}
			else
			{
				
				Log.d(App.TAG, "Null tasks returned for: " + taskLists2.get(i).getTitle() + "id : "+ taskLists2.get(i).getId() );
				
			}

		}

	}

	private void CreateListIfNotExist(WList wList, List<TaskList> taskLists2)
			throws IOException
	{
		DbHelper dbHelper = DbHelper.getInstance(context);
		String google_list_id = dbHelper.getGoogleListdofWlist(wList.getId());

		if (TextUtils.isEmpty(google_list_id))
		{
			// There is no corresponding google list id set... We will have to
			// create a new one

			// Check if the list with same title exists. If yes, re-use that
			// instead of creating a new one.

			Log.d(App.TAG, "Creating google list: " + wList.getTitle());

			TaskList taskList;
			taskList = new TaskList();
			taskList.setTitle(wList.getTitle());
			TaskList newtaskList = client.tasklists().insert(taskList).execute();

			// Update db to reflect the corresponding google id and set the
			// synced field
			dbHelper.setGoogleListofWlist(wList.getId(), newtaskList.getId());

		}
		else
			Log.d(App.TAG, wList.getTitle()
					+ " List Already exists on google with id "
					+ google_list_id);

	}

	public void CreateOrEnsureTasks(List<WTask> tasks, String[] selectedListIds)
			throws IOException
	{
		for (int i = 0; i < tasks.size(); ++i)
		{
			// Ensure the task we are creating or ensuring exists in the list of
			// selected to be synced
			if (Arrays.asList(selectedListIds).contains(tasks.get(i).getList_id()))
			{
				CreateTaskIfNotExist(tasks.get(i));
			}

		}

	}

	private void CreateTaskIfNotExist(WTask wTask) throws IOException
	{
		DbHelper dbHelper = DbHelper.getInstance(context);

		String google_task_id = dbHelper.getGoogleTaskdofWTask(wTask.getId());

		String google_list_id = dbHelper.getGoogleListdofWlist(wTask.getList_id());

		if (TextUtils.isEmpty(google_list_id))
		{
			Log.e(App.TAG, "Creating a google task but no appropriate google list was found");
			return;

		}

		if (TextUtils.isEmpty(google_task_id))
		{
			// There is no corresponding google list id set... We will have to
			// create a new one

			// Check if the list with same title exists. If yes, re-use that
			// instead of creating a new one.

			Log.d(App.TAG, "Creating google task: " + wTask.getTitle());

			// Add a task to the default task list
			Task task = new Task();
			task.setTitle(wTask.getTitle());

			if (wTask.getDue_date() != null)
			{
				task.setDue(DateTime.parseRfc3339((String) wTask.getDue_date()));
			}

			task.setNotes(wTask.getNote());

			Task newTask = client.tasks().insert(google_list_id, task).execute();

			// Update db to reflect the corresponding google id and set the
			// synced field
			dbHelper.setGoogleTaskofWTask(wTask.getId(), newTask.getId());

		}
		else
			Log.d(App.TAG, wTask.getTitle()
					+ " Task Already exists on google with id "
					+ google_list_id);

	}

}
