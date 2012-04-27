package edu.upenn.cis.cis121.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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

	public List<Integer> recommendFriends(int user_id, int numRec)
			throws IllegalArgumentException
	{
		int counter = 0;
		
		return null;
	}
	
	private double weight(int user1, int user2)
	{
		HashSet<Integer> places1 = fromPrim(dbw.getLikes(user1));
		HashSet<Integer> places2 = fromPrim(dbw.getLikes(user2));
		
		HashSet<Integer> common_places = new HashSet<Integer>();
		
		ArrayList<Integer> place_types_1 = new ArrayList<Integer>();
		ArrayList<Integer> place_types_2 = new ArrayList<Integer>();
		int common_place_type_count =0;
		
		for(Integer place : places1)
		{
			place_types_1.add(dbw.getType(place.intValue()));
			if(places2.contains(place)) common_places.add(place);
		}
		
		for(Integer place: places2)
		{
			place_types_2.add(dbw.getType(place.intValue()));
		}
		
		for(Integer place_type : place_types_1)
		{
			if(place_types_2.contains(place_type))
			{
				common_place_type_count++;
				place_types_2.remove(place_type);
			}
		}
		
		int common_place_count = common_places.size();
		
		double weight = 1 / ((double) common_place_count + 
				0.1 * (double) common_place_type_count + 0.01);
		
		return weight;

	}
	
	private HashSet<Integer> fromPrim(int[] arr)
	{
		HashSet<Integer> set = new HashSet<Integer>();
		for(int ii = 0; ii < arr.length; ii++)
		{
			set.add(arr[ii]);
		}
		
		return set;
	}
	
}
