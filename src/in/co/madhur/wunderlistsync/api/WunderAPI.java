package in.co.madhur.wunderlistsync.api;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface WunderAPI
{
	@FormUrlEncoded
	@POST("/login")
	  LoginResponse login(@Field("email") String username, @Field("password")String password);
	
	
}
