package in.co.madhur.wunderlistsync.service;

import in.co.madhur.wunderlistsync.Consts;

public class AuthException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMessage()
	{
		super.getMessage();
		return Consts.AUTH_ERROR;
	}

}
