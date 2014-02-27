package in.co.madhur.wunderlistsync.gtasks;

import in.co.madhur.wunderlistsync.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.tasks.model.Task;

class AsyncLoadTasks extends CommonAsyncTask
{

	AsyncLoadTasks(MainActivity tasksSample)
	{
		super(tasksSample);
	}

	@Override
	protected void doInBackground() throws IOException
	{
		List<String> result = new ArrayList<String>();
		List<Task> tasks = client.tasks().list("@default").setFields("items/title").execute().getItems();
		if (tasks != null)
		{
			for (Task task : tasks)
			{
				result.add(task.getTitle());
			}
		}
		else
		{
			result.add("No tasks.");
		}
		activity.tasksList = result;
	}

	static void run(MainActivity tasksSample)
	{
		new AsyncLoadTasks(tasksSample).execute();
	}
}