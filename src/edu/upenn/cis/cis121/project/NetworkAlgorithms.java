package edu.upenn.cis.cis121.project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Graph algorithms to traverse the social graph and return recommendations. 
 * @author Tess Rinearson - tessr@seas.upenn.edu
 *
 */

public class NetworkAlgorithms {
	
	/**
	 * Databbase wrapper to handle all db connections. 
	 */
	DBWrapper dbw;
	
	/**
	 * Tuple class to help handle pairs for bacon numbers. Ints only.
	 * @author tessr
	 *
	 */
	
	private class Tuple {
		
		/**
		 * the User id of a given node
		 */
		int user_id;
		
		/**
		 * the depth of that same node
		 */
		int depth;
		
		/**
		 * constructor. Takes a user id and a "depth"
		 * @param ui
		 * @param ll
		 */
		public Tuple(int ui, int ll)
		{
			user_id = ui;
			depth = ll;
		}
	}
	
	/**
	 * Pair. Similar to a Tuple (see above), but this time it's comparable and 
	 * uses an int/double combo instead of int/int. For use in the PriorityQueue
	 * @author tessr
	 *
	 */
	private class Pair implements Comparable<Pair> {
		/**
		 * user_id of this node
		 */
		int user_id;
		
		/**
		 * distance from root
		 */
		double distance;
		
		
		/**
		 * constructor
		 * @param ui
		 * @param dd
		 */
		public Pair(int ui, double dd)
		{
			user_id = ui;
			distance = dd;
		}
		
		/**
		 * Overrides equals.
		 */
		public boolean equals(Object other)
		{
			Pair that = (Pair) other;
			return (this.user_id == that.user_id);
		}
		
		/**
		 * Implements comparable.
		 */
		
		public int compareTo(Pair other)
		{
			if (this.distance > other.distance)
			{
				return 1;
			}
			else if (this.distance < other.distance)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
		
	}
	
	/**
	 * "Friends" (or users). Contains much of the same info as the Users table.
	 * @author tessr
	 *
	 */
	
	private class Friend implements Comparable<Friend>
	{
		
		/**
		 * user id
		 */
		int user_id;
		
		/**
		 * distance from the main user
		 */
		double distance;
		
		/**
		 * lat and long
		 */
		double[] location;
		
		/**
		 * Constructor. 
		 * @param ui User ID.
		 * @param loc Location.
		 * @param youloc Location of the primary user. Needed for calculating 
		 * distances/finding closest friends.
		 */
		
		public Friend(int ui, double[] loc, double[] youloc)
		{
			user_id = ui;
			location = loc;
			distance = Math.sqrt(Math.pow((loc[0] - youloc[0]),2) + 
					Math.pow((loc[1] - youloc[1]),2));
		}
		
		/**
		 * Implements comparable.
		 */
		
		public int compareTo(Friend other)
		{
			if (this.distance > other.distance)
			{
				return 1;
			}
			else if (this.distance < other.distance)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}
	
	private class Place implements Comparable<Place>
	{
		/**
		 * place id
		 */
		int place_id;
		
		/**
		 * distance from the center point
		 */
		
		double distance;
		
		/**
		 * lat and long
		 */
		double[] location;
		
		/**
		 * how many people have liked this place?
		 */
		double likes;
		
		/**
		 * Constructor. Likes are counted incrementally.
		 * @param pi
		 * @param loc
		 * @param centerloc
		 */
		
		public Place(int pi, double[] loc, double[] centerloc)
		{
			place_id = pi;
			location = loc;
			distance = Math.sqrt(Math.pow((loc[0] - centerloc[0]),2) + 
					Math.pow((loc[1] - centerloc[1]),2));
			likes = 1;
		}
		
		/**
		 * Get the "suitability" of this place.
		 * @return
		 */
		
		public double getS()
		{
			return likes/(distance + 0.01);
		}
		
		/**
		 * Implements comparable.
		 */
		
		public int compareTo(Place other)
		{
			if (this.getS() > other.getS())
			{
				return 1;
			}
			else if (this.getS() < other.getS())
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
		
		
		public boolean equals(Object other)
		{
			Place that = (Place) other;
			return (this.place_id == that.place_id);
		}
	}

	
	/**
	 * Constructor. This data is necessary to open the connection to the
	 * database.
	 * @param dbUser
	 * @param dbPass
	 * @param dbSID
	 * @param dbHost
	 * @param port
	 */
	public NetworkAlgorithms(String dbUser, String dbPass, String dbSID, String dbHost, 
			int port)
	{
		dbw = new DBWrapper(dbUser, dbPass, dbSID, dbHost, port);
	}
	
	/**
	 * Find the Bacon number of two users. 
	 * @param user_id1
	 * @param user_id2
	 * @return the number of edges between 1 and 2
	 * @throws IllegalArgumentException
	 */
	
	public int distance(int user_id1, int user_id2) 
			throws IllegalArgumentException
	{
		//FIRST check to make sure they are in the db
		if(!dbw.userExists(user_id1) || !dbw.userExists(user_id2))
		{
			throw new IllegalArgumentException();
		}
		
		if(user_id1 == user_id2) return 0;
		
		LinkedList<Tuple> toExplore = new LinkedList<Tuple>();
		HashSet<Integer> explored = new HashSet<Integer>();
		
		int depth = 0;
		Tuple root = new Tuple(user_id1,depth);
		toExplore.add(root);
		
		while(!toExplore.isEmpty())
		{
			Tuple curr = toExplore.poll();
			int child_depth = curr.depth + 1;
			int[] children = dbw.getFriends(curr.user_id);
			for(int ii = 0; ii < children.length; ii++)
			{
				if (children[ii] == user_id2)
				{
					return child_depth;
				}
				else
				{
					Integer child_id = new Integer(children[ii]);
					if(explored.add(child_id))
					{
						Tuple child = new Tuple(children[ii],child_depth);
						toExplore.add(child);
						//feed back in until all nodes are explored.
						//FIFO means it's BFS not DFS
					}
				}
				
			}
		}
		
		return -1;
		
	}
	
	/**
	 * Given a user id find some number (up to numRec) of friend recommendations
	 * based on the social graph. 
	 * @param user_id
	 * @param numRec
	 * @return
	 * @throws IllegalArgumentException
	 */
	
	public List<Integer> recommendFriends(int user_id, int numRec)
			throws IllegalArgumentException
	{
		int counter = 0; //necessary for numRec
		
		PriorityQueue<Pair> toVisit = new PriorityQueue<Pair>();
		HashMap<Integer,Double> distances = new HashMap<Integer,Double>();
		HashSet<Integer> visited = new HashSet<Integer>();
		List<Integer> recommendations = new ArrayList<Integer>();
		
		//add root to things to start
		distances.put(user_id, 0.0);
		toVisit.add(new Pair(user_id,0.0));
		
		while(!toVisit.isEmpty() && counter < numRec)
		{
			Pair curr = toVisit.poll();
			visited.add(curr.user_id); //never worry about this node again
			
			if(!dbw.isFriend(curr.user_id, user_id) && curr.user_id != user_id)
			{
				recommendations.add(curr.user_id); //this will be suggested
				counter++;
			}
			
			int[] friends = dbw.getFriends(curr.user_id);
			for (int ii = 0; ii < friends.length; ii++)
			{
				if(visited.add(friends[ii]))
				{
					double newweight = weight(friends[ii], curr.user_id) + 
						distances.get(curr.user_id);
					if(distances.containsKey(friends[ii]))
					{
						
						if (newweight < distances.get(friends[ii]))
						{
							//start a bad hack. sorry. i'm sleepy
							ArrayList<Pair> temp = new ArrayList<Pair>();
							while(!toVisit.isEmpty())
							{
								Pair tempp = toVisit.poll();
								if (tempp.user_id != friends[ii])
								{
									temp.add(tempp);
								}
							}
							
							for (Pair pp : temp)
							{
								toVisit.add(pp);
							}
							
							toVisit.add(new Pair(friends[ii], newweight));
							//end bad hack

						}
						
					}
					else
					{
						toVisit.add(new Pair(friends[ii],newweight));
						distances.put(friends[ii], newweight);
					}
				}
			}
			
		}
		
		
		return recommendations;
	}
	
	/**
	 * Determine how closely two people are linked based on their likes. A larger
	 * weight means a tighter connection. 
	 * @param user1
	 * @param user2
	 * @return double - the connection "weight" / ranking
	 */
	
	public double weight(int user1, int user2)
	{
		HashSet<Integer> places1 = fromPrim(dbw.getLikes(user1));
		HashSet<Integer> places2 = fromPrim(dbw.getLikes(user2));
		
		HashSet<Integer> common_places = new HashSet<Integer>();
		
		ArrayList<Integer> place_types_1 = new ArrayList<Integer>();
		ArrayList<Integer> place_types_2 = new ArrayList<Integer>();
		int common_place_type_count =0;
		
		for(Integer place : places1)
		{
			//first check for common places
			place_types_1.add(dbw.getType(place.intValue()));
			if(places2.contains(place)) common_places.add(place);
		}
		
		for(Integer place: places2)
		{
			//get all place types
			place_types_2.add(dbw.getType(place.intValue()));
		}
		
		for(Integer place_type : place_types_1)
		{
			if(place_types_2.contains(place_type))
			{
				common_place_type_count++; //count place types
				place_types_2.remove(place_type);
			}
		}
		
		int common_place_count = common_places.size();
		
		double weight = 1 / ((double) common_place_count + 
				0.1 * (double) common_place_type_count + 0.01);
		
		return weight; 
	

	}
	
	/**
	 * Convert a primitive int array into a fancy Integer HashSet.
	 * @param arr
	 * @return
	 */
	
	private HashSet<Integer> fromPrim(int[] arr)
	{
		HashSet<Integer> set = new HashSet<Integer>();
		for(int ii = 0; ii < arr.length; ii++)
		{
			set.add(arr[ii]);
		}
		
		return set;
	}
	
	/**
	 * Recommends activities based on friends and location. 
	 * @param user_id
	 * @param maxFriends
	 * @param maxPlaces
	 * @return
	 * @throws IllegalArgumentException
	 */
	
	public String recommendActivities
		(int user_id, int maxFriends, int maxPlaces) 
				throws IllegalArgumentException
	{
		
		//first, get the close friends. but before that, must have
		//all friends.
		PriorityQueue<Friend> all_friends = new PriorityQueue<Friend>();
		int[] all_friends_primitive = dbw.getFriends(user_id);
		for(int ii = 0; ii < all_friends_primitive.length; ii++)
		{
			int uid = all_friends_primitive[ii];
			Friend fr = new Friend(uid,dbw.getUserLocation(uid),
					dbw.getUserLocation(user_id));
			all_friends.add(fr);
		}
		
		int count = 0; 
		
		ArrayList<Friend> close_friends = new ArrayList<Friend>();
		
		while(count < maxFriends && !all_friends.isEmpty())
		{
			close_friends.add(all_friends.poll());
		}
		
		//add the user 
		double[] uloc = dbw.getUserLocation(user_id);
		close_friends.add(new Friend(user_id,uloc,uloc));
		
		//now find the center of friends
		
		double tot_long = 0;
		double tot_lat = 0;
		
		for(int ii = 0; ii < close_friends.size(); ii++)
		{
			tot_lat += close_friends.get(ii).location[0];
			tot_long += close_friends.get(ii).location[1];
		}
		
		double center_lat = tot_lat/(close_friends.size());
		double center_long = tot_long/(close_friends.size());
		double[] center = {center_lat, center_long};
		
		//and get a set of all the places that people like
		
		PriorityQueue<Place> passable_places = new PriorityQueue<Place>();
		
		String jsonstring ="{\"user\":{\"user_id\":" + user_id + 
				",\"first_name\":\"" + dbw.getFirstName(user_id) +
				"\",\"last_name\":\""+ dbw.getLastName(user_id) + 
				"\",\"latitude\":"+ dbw.getUserLocation(user_id)[0] + 
				",\"longitude\":" + dbw.getUserLocation(user_id)[1] + 
				"},\"friends\": { "; 
		
		int fcount = 0;
		for(Friend ff : close_friends)
		{
			int[] places = dbw.getLikes(ff.user_id);
			for(int ii = 0; ii < places.length; ii++)
			{
				Place curr = new Place(places[ii],
						dbw.getLocation(places[ii]), center);
				if (!passable_places.contains(curr))
					{
					passable_places.add(curr);
					}
				else
				{
					passable_places.remove(curr);
					curr.likes++;
					passable_places.add(curr);
				}
				
			}
			
			//put into json
			
			jsonstring += "\"" + fcount + "\" : {\"user_id\":" + ff.user_id + 
					",\"first_name\":\"" + dbw.getFirstName(ff.user_id) + 
					"\",\"last_name\":\"" + dbw.getLastName(ff.user_id) + 
					"\",\"latitude\":" + dbw.getUserLocation(ff.user_id)[0] + 
					",\"longitude\":" + dbw.getUserLocation(ff.user_id)[1] +"},";
			fcount++;
			
		}
		
		//remove last character (comma)
		
		jsonstring = jsonstring.substring(0,jsonstring.length()-1);
		jsonstring += "}, \"places\": {";
		
		//now all places
		
		ArrayList<Place> best = new ArrayList<Place>();
		
		for(int ii = 0; ii < maxPlaces; ii++)
		{
			best.add(passable_places.poll());
		}
		
		int pcount = 1; 
		for (Place pp : best)
		{
			jsonstring += "\"" + pcount + "\" : {\"place_id\":" + pp.place_id + 
					",\"place_name\":\"" + dbw.getPlaceName(pp.place_id) + 
					"\",\"description\":\"" + dbw.getType(pp.place_id) + 
					"\",\"latitude\":" + dbw.getLocation(pp.place_id)[0] + 
					",\"longitude\":" + dbw.getLocation(pp.place_id)[0] + 
					"},";
			pcount++;
		}
		jsonstring = jsonstring.substring(0,jsonstring.length()-1);
		jsonstring += "} }";
		System.out.println(jsonstring);
		return jsonstring;
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
