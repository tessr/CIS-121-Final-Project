package edu.upenn.cis.cis121.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;

public class NetworkAlgorithms {
	
	DBWrapper dbw;
	
	private class Tuple {
		
		int user_id;
		int depth;
		
		public Tuple(int ui, int ll)
		{
			user_id = ui;
			depth = ll;
		}
	}


	public NetworkAlgorithms(String dbUser, String dbPass, String dbSID, String dbHost, 
			int port)
	{
		dbw = new DBWrapper(dbUser, dbPass, dbSID, dbHost, port);
	}
	
	
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
					}
				}
				
			}
		}
		
		return -1;
		
	}

}
