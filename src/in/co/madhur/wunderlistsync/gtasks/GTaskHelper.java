package in.co.madhur.wunderlistsync.gtasks;

import in.co.madhur.wunderlistsync.api.model.WList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class GTaskHelper
{
	
	private static com.google.api.services.tasks.Tasks client;
	private static GTaskHelper taskHelper;
	
	private GTaskHelper()
	{
		
		
	}
	
	public static GTaskHelper GetInstance(com.google.api.services.tasks.Tasks client)
	{
		if(taskHelper==null)
		{
			
			GTaskHelper.client=client;
			taskHelper=new GTaskHelper();
			return taskHelper;
		}
		else
			return taskHelper;
		
	}

	public void CreateOrEnsureLists(List<WList> lists, String[] selectedListIds) throws IOException 
	{
		// Get all lists @Google initially 
		TaskLists taskLists=client.tasklists().list().execute();
		List<TaskList> taskLists2=taskLists.getItems();
		
		
		for(int i=0;i<lists.size(); ++i)
		{
			if(Arrays.asList(selectedListIds).contains(lists.get(i).getId())   )
			{
				CreateListIfNotExist(lists.get(i).getTitle(), taskLists2);
			}
			
			
			
		}
		
	}

	private void CreateListIfNotExist(String title, List<TaskList> taskLists2) throws IOException
	{
		boolean exist=false;
		
		for(int i=0;i<taskLists2.size();++i)
		{
			if(taskLists2.get(i).getTitle().equals(title))
				exist=true;
			
		}
		
		if(!exist)
		{
			TaskList taskList;
			taskList=new TaskList();
			taskList.setTitle(title);
			client.tasklists().insert(taskList).execute();
			
		}
		
	}
	
	

}
