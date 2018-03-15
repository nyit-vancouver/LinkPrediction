package metaPath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

/**
 * Meta path counts are calculated using matrix multiplication. 
 *
 * We have 2113 users and 10109 rated stories
 * 
 *  // Table digg_friends contains 1,731,658 friendship links of 71,367 distinct users. 
	// Table digg_votes contains 3,018,197 votes on 3553 stories made by 139,409 distinct users. 
	// Voters who do not appear in the digg_friends table did not specify any friends at the time data was collected. 
	// mutual (1/0), friendship_time_stamp, user_id, friend_id
	// 1,1214799565,336224,266641
 * 
 * 	We have 279631 users (voter and non-voter) from digg_friends
		We have 336225 users (both from digg_vote and digg_friends)
 *   
 */


public class DiggMetaPathUSUS {

	/**
	 * Given index of a user and a story, calculate number of different meta-path of type U-S-U-S
	 * @param userIndex u
	 * @param storyIndex s
	 * @return pathCount between them
	 */

	//private static short[][] user_stories = new short[336225][3553];
	private static Map<Integer, ArrayList<Integer>> user_stories_map = new HashMap<Integer, ArrayList<Integer>>();    
	private static Map<Integer, ArrayList<Integer>> story_users_map = new HashMap<Integer, ArrayList<Integer>>();    

	//private static short[][] story_users1 = new short[3553][336225];

	public static int pathCount_USUS(int u, int s){
		int pathCount = 0;

		s = s-336225;  // re-indexing storyID to start from 0 

		// Let A be user_story matrix and B be story_user matrix
		// D_um = sum C_uk*A_km for k=1..2113, and C_uk = sum A_ul*Blk for l=1..10109

		int[] C_u = new int[336225];

		//old version : not efficient
		//for(int k=0;k<336225; k++){
		//	for(int l=0;l<3553; l++)
		//		C_u[k] += user_stories[u][l] * story_users[l][k];
		//}
		//for(int k=0;k<336225; k++)
		//	pathCount += C_u[k] * user_stories[k][s];


		/*for(int k=0;k<336225; k++){
			if (user_stories_map.containsKey(u))
				for(short l: user_stories_map.get(u))
					C_u[k] += story_users[l][k];  // in fact C_u[k] += 1 * story_users[l][k]
		}*/


		if (user_stories_map.containsKey(u))
			for(int l: user_stories_map.get(u))
				for (int k : story_users_map.get(l)){
					C_u[k]++;
				}

		for(int k=0;k<336225; k++){
			if (user_stories_map.containsKey(k))
				if (user_stories_map.get(k).contains(s))
					pathCount += C_u[k]; // pathCount += C_u[k] * 1
		}


		//System.out.println("u: " + u + ", s: " + s + " -> " + pathCount);

		return pathCount;
	}



	public static void main(String[] args) throws ClassNotFoundException 
	{	 
		String currentInterval = args[0]; // e.g. is interval=2
		String intervals = args[1]; // e.g. is intervals=7

		//String currentInterval = "2"; //args[0]; // e.g. is interval=2
		//String intervals = "3"; //args[1]; // e.g. is intervals=7

		String usre_story_file_name = "Digg/" + intervals + "intervals_votes/digg_votes_" + currentInterval + "of" + intervals + ".txt"; // user-story and story-user infor for current time
		String labels_file_name = "Digg/" + intervals + "intervals_votes/labels_for_" + currentInterval + "of" + intervals + "_newLinks_in_" + Integer.toString((Integer.parseInt(currentInterval)+1)) + "of" + intervals + ".txt";				  // labels for current time based on next time
		String metaPath_file_name = "Digg/" + intervals + "intervals_votes/USUS_" + currentInterval + "of" + intervals + ".txt"; // outputFile


		String currentLine, numOfConnectedNodes;
		int from = 0, to = 0, userIndex = 0, storyIndex = 0;

		try{

			BufferedWriter bwUserstoryLabel = new BufferedWriter(new FileWriter(new File(metaPath_file_name)));

			BufferedReader userstoryFile = new BufferedReader(new FileReader(usre_story_file_name));
			// file format example (3 nodes)
			//3
			//0,0
			//1,3:2,1:3,1:4,1
			//...			
			int numOfNodes = Integer.parseInt(userstoryFile.readLine()); // reading numOfNodes
			for (int i=0; i < 336225; i++){  // considering only users and not stories
				ArrayList<Integer> storiesList = new ArrayList<Integer>();
				currentLine = userstoryFile.readLine();
				from = 0;
				to = currentLine.indexOf(",", from);
				userIndex = Integer.parseInt(currentLine.substring(from,to));
				from = to+1;
				to = currentLine.indexOf(":", from);
				if (to<=0) to = currentLine.length();
				numOfConnectedNodes = currentLine.substring(from,to);
				for (int j=0; j < Integer.parseInt(numOfConnectedNodes) ; j++){
					from = to+1;
					to = currentLine.indexOf(",", from);
					storyIndex = Integer.parseInt(currentLine.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					ArrayList<Integer> a = new ArrayList<Integer>();
					// old version : not efficient
					//user_stories[userIndex][storyIndex-336225]=1;
					if (user_stories_map.containsKey(userIndex))
						a = user_stories_map.get(userIndex);
					a.add( (storyIndex-336225));	
					user_stories_map.put(userIndex, a);
				}
			}
			for (int i=0; i < 3553; i++){  // considering only stories
				ArrayList<Integer> usersList = new ArrayList<Integer>();
				currentLine = userstoryFile.readLine();
				from = 0;
				to = currentLine.indexOf(",", from);
				storyIndex = Integer.parseInt(currentLine.substring(from,to));
				from = to+1;
				to = currentLine.indexOf(":", from);
				if (to<=0) to = currentLine.length();
				numOfConnectedNodes = currentLine.substring(from,to);
				for (int j=0; j < Integer.parseInt(numOfConnectedNodes) ; j++){
					from = to+1;
					to = currentLine.indexOf(",", from);
					userIndex = Integer.parseInt(currentLine.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					ArrayList<Integer> a = new ArrayList<Integer>();
					// old version : not efficient
					//story_users[storyIndex-336225][userIndex]=1;
					if (story_users_map.containsKey((storyIndex-336225)))
						a = story_users_map.get((storyIndex-336225));
					a.add(userIndex);	
					story_users_map.put( (storyIndex-336225), a);

				}
			}



			BufferedReader labels = new BufferedReader(new FileReader(labels_file_name));
			// file format example
			//0,1:1
			//...
			//2,3:0
			long startTime = System.currentTimeMillis();
			long endTime, duration;

			int sourceNode, destNode, counter = 0;
			while ((currentLine = labels.readLine()) != null){
				counter++;
				from = 0;
				to = currentLine.indexOf(",", from);
				sourceNode = Integer.parseInt(currentLine.substring(from,to));
				from = to+1;
				to = currentLine.indexOf(":", from);
				destNode = Integer.parseInt(currentLine.substring(from,to));

				//heteSim_UMUM(sourceNode, destNode);

				//System.out.println("label: " + currentLine.substring(to));
				//System.out.println("sourceNode: " + sourceNode + "destNode: " + destNode);
				bwUserstoryLabel.write(pathCount_USUS(sourceNode, destNode)+"\n");

				if (counter%100000==0){
					System.out.println(counter);
					endTime = System.currentTimeMillis();
					duration = (endTime - startTime);
					System.out.println("Time passed " + duration/1000 + " seconds!");
				}
				//System.out.println("-pathSim(" + sourceNode + "," + destNode+ ") = " + pathSim(Integer.toString(sourceNode), Integer.toString(destNode)) );
			}


			userstoryFile.close();
			bwUserstoryLabel.close();

		}catch (IOException e) 
		{
			System.out.println(e);
			e.printStackTrace();
		} 

	}

}
