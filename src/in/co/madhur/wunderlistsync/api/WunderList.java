package in.co.madhur.wunderlistsync.api;

import java.util.List;

import android.util.Log;
import in.co.madhur.wunderlistsync.App;
import in.co.madhur.wunderlistsync.Consts;
import in.co.madhur.wunderlistsync.api.model.LoginResponse;
import in.co.madhur.wunderlistsync.api.model.Me;
import in.co.madhur.wunderlistsync.api.model.WList;
import in.co.madhur.wunderlistsync.api.model.WTask;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class WunderList
{
	private static WunderList wunderList;
	private static RestAdapter restAdapter;
	private static WunderAPI service;
	private static String authorizationHeader;
	//private static String username, password;
	private static String token;
	
	private WunderList()
	{

	}

	// The caller should save the token in preference
	private static  LoginResponse Login(String userName, String password) throws AuthException
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
				throw new AuthException(Consts.AUTH_ERROR);
			if(e.getResponse().getStatus()==404)
				throw new AuthException(Consts.USER_NOT_FOUND);
			
			Log.v(App.TAG, response.toString());
		}
		
		// Save the token 
		token=response.getToken();
		
		return response;
	}

	public void SetToken(String newToken)
	{
		token=newToken;
		
	}
	
	public String GetToken()
	{
		return token;
	}

	private static boolean IsLoginRequired() throws AuthException
	{
		Me userInfo;
		
		try
		{
			// Try to get data using provided token
			userInfo=service.getUserInfo(token);
		}
		catch(RetrofitError e)
		{
			if(e.getResponse().getStatus()==401)
			{
				return true;
			}
			Log.e(App.TAG, "IsLoginRequired: Status: " + e.getResponse().getStatus());
			return true;
		}
		
		return false;
	}
	
	public List<WTask> GetTasks()
	{
		List<WTask> tasks;
		
		tasks=service.GetWunderTasks(token);
		
		return tasks;
	}
	
	public List<WList> GetLists()
	{
		List<WList> lists;
		
		lists=service.GetLists(token);
		
		return lists;
		
		
	}

	public static WunderList getInstance(String newToken) throws AuthException
	{
		if (wunderList == null)
		{
			restAdapter = new RestAdapter.Builder().setEndpoint(APIConsts.API_URL).build();
			service = restAdapter.create(WunderAPI.class);
			wunderList = new WunderList();
			token=newToken;
			
			if(IsLoginRequired())
				throw new AuthException(AuthError.OLD_TOKEN_EXPIRED);
			
			return wunderList;
		}
		else
			return wunderList;

	}
	
	public static WunderList getInstance(String userName, String password) throws AuthException
	{
		if (wunderList == null)
		{
			restAdapter = new RestAdapter.Builder().setEndpoint(APIConsts.API_URL).build();
			service = restAdapter.create(WunderAPI.class);
			wunderList = new WunderList();
			
			// Since token is not provided, make explicit call to login which will set the token internally.
			// Callers should get the token by calling wunderlist.getToken();
			Login(userName, password);
			
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
