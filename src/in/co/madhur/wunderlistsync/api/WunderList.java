package in.co.madhur.wunderlistsync.api;

import retrofit.RestAdapter;

public class WunderList
{
	private static WunderList wunderList;
	private static RestAdapter restAdapter;
	private static WunderAPI service;
	private static String authorizationHeader;
	private static String username, password;
	
	private WunderList()
	{
		
		
	}
	
	public LoginResponse Login(String userName, String password)
	{
		LoginResponse s=service.login(userName, password);
		return s;
	}
	
	
	public LoginResponse Login(String token)
	{
		//LoginResponse s=service.login(userName, password);
		//return s;
		return null;
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

	public String getAuthorizationHeader()
	{
		return authorizationHeader;
	}

	public void setAuthorizationHeader(String authorizationHeader)
	{
		this.authorizationHeader = authorizationHeader;
	}

}
