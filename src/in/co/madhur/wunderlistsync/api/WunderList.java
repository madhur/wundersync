package in.co.madhur.wunderlistsync.api;

import retrofit.RestAdapter;

public class WunderList
{
	private static WunderList wunderList;
	private static RestAdapter restAdapter;
	private static WunderAPI service;
	private WunderList()
	{
		
		
	}
	
	public LoginResponse Login(String userName, String password)
	{
		LoginResponse s=service.login(userName, password);
		return s;
	}
	
	
	public static WunderList getInstance()
	{
		if(wunderList==null)
		{
			
			restAdapter = new RestAdapter.Builder()
		    .setEndpoint(APIConsts.API_URL)
		    .build();

			service = restAdapter.create(WunderAPI.class);
			
			wunderList= new WunderList();
			return wunderList;
		}
		else
			return wunderList;
		
	}

}
