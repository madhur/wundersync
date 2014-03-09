package in.co.madhur.wunderlistsync.gtasks;

import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.AppPreferences;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.api.model.WTask;
import in.co.madhur.wunderlistsync.database.DbHelper;
import in.co.madhur.wunderlistsync.utils.AppLog;
import in.co.madhur.wunderlistsync.utils.DateHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

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

		for (int i = 0; i < taskLists2.size(); ++i)
		{
			List<Task> tasks = client.tasks().list(taskLists2.get(i).getId()).execute().getItems();
			if (tasks != null)
			{
				if (tasks.size() < 25)
				{
					Log.d(App.TAG, "Deleting list empty "
							+ taskLists2.get(i).getTitle());
					try
					{
						client.tasklists().delete(taskLists2.get(i).getId()).execute();
					}
					catch (GoogleJsonResponseException e)
					{

						Log.e(App.TAG, "Error deleting "
								+ taskLists2.get(i).getTitle());
					}
				}
				else
					Log.d(App.TAG, "Size of list " + tasks.size());
			}
			else
			{

				Log.d(App.TAG, "Null tasks returned for: "
						+ taskLists2.get(i).getTitle() + "id : "
						+ taskLists2.get(i).getId());

				Log.d(App.TAG, "Deleting list empty "
						+ taskLists2.get(i).getTitle());
				client.tasklists().delete(taskLists2.get(i).getId()).execute();

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

			TaskList taskList = GetTaskList(wList.getTitle());
			
			try
			{
				TaskList newtaskList = client.tasklists().insert(taskList).execute();
				// Update db to reflect the corresponding google id and set the
				// synced field
				
				dbHelper.setGoogleListofWlist(wList.getId(), newtaskList.getId());
			}
			catch (GoogleJsonResponseException e)
			{
				handleGoogleJSONError(e);

			}

			

		}
		else
		{
			// List already exists, check if it needs updation
			Long lastSync = GetLastSyncTime();

			Log.d(App.TAG, wList.getTitle()
					+ " List Already exists on google with id "
					+ google_list_id);

			String lastUpdated = wList.getUpdated_at();
			if (TextUtils.isEmpty(lastUpdated))
			{
				Log.d(App.TAG, "No need of updating");
				return;
			}

			if (DateHelper.persistDate(lastUpdated) > lastSync || lastSync == 0)
			{
				Log.d(App.TAG, wList.getTitle() + " List needs updating");

				TaskList taskList = GetTaskList(wList.getTitle());

				try
				{
					client.tasklists().update(google_list_id, taskList).execute();
				}
				catch (GoogleJsonResponseException e)
				{
					handleGoogleJSONError(e);
				}

			}

		}

	}
	
	private TaskList GetTaskList(String title)
	{
		TaskList taskList;
		taskList = new TaskList();
		taskList.setTitle(title);
		return taskList;
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

	private Task GetTaskObj(String Id, String title, String dueDate, String notes, String completedAt)
	{
		Task task = new Task();
		task.setTitle(title);

		if (!TextUtils.isEmpty(Id))
		{
			task.setId(Id);
		}

		if (!TextUtils.isEmpty(dueDate))
		{
			Log.d(App.TAG, "Converting to google format " + dueDate);
			task.setDue(DateHelper.ConvertShortToGoogleFormat(dueDate));

		}
		if (!TextUtils.isEmpty(completedAt))
		{
			task.setCompleted(DateHelper.ConvertShortToGoogleFormat(completedAt));
			task.setStatus("completed");
		}
		else
			task.setStatus("needsAction");

		task.setNotes(notes);
		return task;

	}

	private void handleGoogleJSONError(GoogleJsonResponseException e)
	{
		String message = e.getStatusMessage();
		try
		{
			JSONObject json = new JSONObject(new JSONTokener(e.getMessage().substring(e.getMessage().indexOf("{"))));
			JSONObject error = json.getJSONArray("errors").getJSONObject(0);
			Long errorCode = json.getLong("code");

			message = error.getString("message");

			Log.e(App.TAG, message);
			Log.e(App.TAG, "Error code: " + errorCode);
			
		}
		catch (Exception ee)
		{

			Log.e(App.TAG, ee.getMessage());
		}

	}
	
	private Long GetLastSyncTime()
	{
		AppPreferences preferences = new AppPreferences(context);

		Long lastSync = preferences.GetLastSyncDate();
		
		return lastSync;
		
		
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
			// There is no corresponding google task id set... We will have to
			// create a new one

			Log.d(App.TAG, "Creating google task: " + wTask.getTitle());

			// Add a task to the default task list
			Task task = GetTaskObj("", wTask.getTitle(), wTask.getDue_date().toString(), wTask.getNote(), wTask.getCompleted_at());

			try
			{
				Task newTask = client.tasks().insert(google_list_id, task).execute();

				// Update db to reflect the corresponding google id and set the
				// synced field
				dbHelper.setGoogleTaskofWTask(wTask.getId(), newTask.getId());
			}
			catch (GoogleJsonResponseException e)
			{
				handleGoogleJSONError(e);
			}

		}
		else
		{
			// The goole task already exists. Check if it needs updation
			Long lastSync = GetLastSyncTime();

			String lastUpdated = wTask.getUpdated_at();
			if (TextUtils.isEmpty(lastUpdated))
			{
				Log.d(App.TAG, "No need of updating");
				return;
			}

			if (DateHelper.persistDate(lastUpdated) > lastSync || lastSync == 0)
			{
				Log.d(App.TAG, wTask.getTitle() + " Task needs updating");

				Task task = GetTaskObj(google_task_id, wTask.getTitle(), wTask.getDue_date().toString(), wTask.getNote(), wTask.getCompleted_at());

				Log.d(App.TAG, "Updating Task " + google_task_id
						+ " with list " + google_list_id);

				Task newTask;
				try
				{

					newTask = client.tasks().update(google_list_id, google_task_id, task).execute();
					dbHelper.setGoogleTaskofWTask(wTask.getId(), newTask.getId());
				}
				catch (GoogleJsonResponseException e)
				{
					handleGoogleJSONError(e);
				}

			}
		}

	}

}
