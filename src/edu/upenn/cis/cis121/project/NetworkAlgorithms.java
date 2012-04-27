package edu.upenn.cis.cis121.project;

import java.sql.Connection;
import java.sql.SQLException;

public class NetworkAlgorithms {
	
	private Connection _conn;

	public NetworkAlgorithms(String dbUser, String dbPass, String dbSID, String dbHost, 
			int port)
	{
		
		try 
		{
			_conn = DBUtils.openDBConnection(dbUser, dbPass, dbSID, dbHost, port);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public int distance(int user_id1, int user_id2) 
			throws IllegalArgumentException
	{
		
	}

}
