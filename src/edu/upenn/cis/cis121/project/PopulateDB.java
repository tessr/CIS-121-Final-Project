package edu.upenn.cis.cis121.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import edu.upenn.cis.cis121.project.DBUtils;

/**
 * Populate the database with some appropriate information.
 * @author Tess Rinearson - tessr@seas.upenn.edu
 *
 */

public class PopulateDB {
	
	private Connection _conn;
	
	public PopulateDB(String dbUser, String dbPass, String dbSID,
			String dbHost, int port)
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
	
	public void populatePlaces(String filename)
	{
		InputStream in;
		try {
			in = new FileInputStream(new File(filename));
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			
			while(line != null)
			{
				String[] split = line.split(",");
				String place_id = split[0];
				String place_name = split[1];
				place_name = place_name.replace("'", "''");
				String type_id = split[2];
				String latitude = split[3];
				String longitude = split[4];
				
				String query = "insert into Places (place_id, place_name, " +
						"type_id, latitude, longitude) values (" + place_id + 
						",'" + place_name + "'," + type_id + "," + latitude +
						"," + longitude + ")";
				
				try 
				{
					DBUtils.executeUpdate(query);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				
				line = br.readLine();
				
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void populateLikes(String filename)
	{
		InputStream in;
		try {
			in = new FileInputStream(new File(filename));
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			
			while(line != null)
			{	
				String query = "insert into Likes (user_id,place_id) values (" 
				+ line + ")";

				try 
				{
					DBUtils.executeUpdate(query);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				
				line = br.readLine();
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the database connection.
	 * @throws SQLException
	 */
	public void closeDBConnection() {
		try {
			DBUtils.closeDBConnection();
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.err);
		}
	}
	
	

}
