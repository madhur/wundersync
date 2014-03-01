package in.co.madhur.wunderlistsync.api;

import android.util.Log;
import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.service.AuthException;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

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

	public LoginResponse Login(String userName, String password) throws AuthException
	{
		Log.v(App.TAG, "Executing login");
		LoginResponse response = null; 
		try
		{
			response = service.login(userName, password);
		}
		catch (RetrofitError e)
		{
			if(e.getResponse().getStatus()==403)
				throw new AuthException();
			
			Log.v(App.TAG, response.toString());
		}
		return response;
	}

	public LoginResponse Login(String token)
	{
		// LoginResponse s=service.login(userName, password);
		// return s;
		return null;
	}

	public boolean IsLoginRequired(String token)
	{

		return true;
	}

	public static WunderList getInstance()
	{
		if (wunderList == null)
		{

			restAdapter = new RestAdapter.Builder().setEndpoint(APIConsts.API_URL).build();

			service = restAdapter.create(WunderAPI.class);

			wunderList = new WunderList();
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
