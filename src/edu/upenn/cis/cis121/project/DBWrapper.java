package edu.upenn.cis.cis121.project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 * A set of functions to connect to the Oracle database.
 * @author Tess Rinearson - tessr@seas.upenn.edu
 *
 */

public class DBWrapper {
	
	/**
	 * Connection to the database, via DBUtils
	 */
	private Connection _conn;
	
	/**
	 * Constructor.
	 * @param dbUser
	 * @param dbPass
	 * @param dbSID
	 * @param dbHost
	 * @param port
	 */
	public DBWrapper(String dbUser, String dbPass, String dbSID, String dbHost, 
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
	
	/**
	 * returns the user_ids of all friends of the user with the input user_id
	 * @param user_id 
	 * @return int list of all friends
	 */
	public int[] getFriends(int user_id)
	{
		ArrayList<Integer> friendlist = new ArrayList<Integer>();
		try {
			String query = "select user_id1 from Friends where user_id2=" + 
					user_id + "union select user_id2 from Friends where " + 
					"user_id1=" + user_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	Integer fid = new Integer(rs.getInt(1));
		    	friendlist.add(fid);
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return convertToPrimitives(friendlist);
	}
	
	/**
	 * Get the first name of a user
	 * @param user_id
	 * @return first name
	 */
	public String getFirstName(int user_id)
	{
		String name = "";
		try {
			String query = "select first_name from users where user_id = " + 
					user_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	name = rs.getString("first_name");
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return name;
	}
	
	/**
	 * Get the last name of a user
	 * @param user_id
	 * @return first name
	 */
	
	public String getLastName(int user_id)
	{
		String name = "";
		try {
			String query = "select last_name from users where user_id = " + 
					user_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	name = rs.getString("last_name");
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return name;
	}
	
	/**
	 * Get the name of a place
	 * @param place_id
	 * @return the place name
	 */
	
	public String getPlaceName(int place_id)
	{
		String name = "";
		try {
			String query = "select place_name from places where place_id = " + 
					place_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	name = rs.getString("place_name");
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return name;
	}
	
	
	/**
	 * returns the place_ids of all the places liked by the user with user_id
	 * @param user_id
	 * @return array of place ids
	 */
	public int[] getLikes(int user_id)
	{
		ArrayList<Integer> likelist = new ArrayList<Integer>();
		try {
			String query = "select place_id from Likes where user_id=" + 
					user_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	Integer lid = new Integer(rs.getInt(1));
		    	likelist.add(lid);
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return convertToPrimitives(likelist);
		
	}
	
	/**
	 * Get the type_id given a place_id
	 * @param place_id
	 * @return the type_id of the place. returns -1 if not found
	 */
	
	public int getType(int place_id)
	{
		int ptid = -1;
		try {
			String query = "select type_id from Places where place_id=" + 
					place_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	ptid = rs.getInt("type_id");
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		return ptid;
	}
	
	/**
	 * returns an array of the form [lat,lon] representing the location
	 * of the place with place_id
	 * @param place_id
	 * @return lat and long
	 */

	public double[] getLocation(int place_id)
	{
		double[] loc = new double[2];
		try {
			String query = "select latitude, longitude from Places where " +
					"place_id = " + place_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	loc[0] = rs.getInt("latitude");
		    	loc[1] = rs.getInt("longitude");
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return loc;
	}
	
	/**
	 * Get the location of a user from the db
	 * @param user_id
	 * @return location [lat, lon]
	 */
	
	public double[] getUserLocation(int user_id)
	{
		double[] loc = new double[2];
		try {
			String query = "select latitude, longitude from Users where " +
					"user_id = " + user_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	loc[0] = rs.getInt("latitude");
		    	loc[1] = rs.getInt("longitude");
		    }
		    
		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
		}
		
		return loc;
	}
	
	/**
	 * Does this user exist?
	 * @param user_id
	 * @return yes or no
	 */
	
	public boolean userExists(int user_id)
	{
		try {
			String query = "select count(user_id) from Users where user_id="
					+ user_id;
			
			Statement st = _conn.createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    while (rs.next()) {
		    	int res = rs.getInt(1);
			    if(res == 1)
			    {
			    	return true;
			    }
			    else
			    {
			    	return false;
			    }
		    }

		    rs.close();
		    st.close();
		} catch (SQLException sqle) {
			System.err.println(sqle.toString());
			return false;
		}
		
		return false;
		
	}
	
	/**
	 * Are two users friends?
	 * @param user1
	 * @param user2
	 * @return yes or no
	 */
	public boolean isFriend(int user1, int user2)
	{
		int[] friends = getFriends(user1);
		for(int ii = 0; ii < friends.length; ii++)
		{
			if(friends[ii] == user2) return true;
		}
		return false;
	}
	
	/**
	 * Converts an ArrayList of Integers to an array of ints.
	 * @param list ArrayList of Integers
	 * @return an array of ints
	 */
	
	private int[] convertToPrimitives(ArrayList<Integer> list)
	{
		int[] res = new int[list.size()];
		for(int ii = 0; ii < res.length; ii++)
		{
			res[ii] = list.get(ii).intValue();
		}
		
		return res;
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
