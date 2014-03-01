package in.co.madhur.wunderlistsync.api;

import java.util.List;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

public interface WunderAPI
{
	@FormUrlEncoded
	@POST("/login")
	  LoginResponse login(@Field("email") String username, @Field("password")String password);
	
	@GET(value = "/me")
	  Me getUserInfo(@Header("Authorization") String authorization);
	
	@GET(value = "/me/tasks")
	List<WunderTask> GetWunderTasks(@Header("Authorization") String authorization);
	
	@POST(value = "/me/tasks")
	List<WunderTask> CreateWunderTask(@Header("Authorization") String authorization, String listId, String title,  String isStarred, String dueDate);
}
