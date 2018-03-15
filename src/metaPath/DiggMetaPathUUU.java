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


public class DiggMetaPathUUU {

	/**
	 * Given index of a user and a story, calculate number of different meta-path of type U-S-U
	 * @param userIndex u
	 * @param storyIndex s
	 * @return pathCount between them
	 */

	private static boolean[][] user_friends = new boolean[279631][279631];

	public static int pathCount_UUU(int u1, int u2){
		int pathCount = 0;

		for(int l=0;l<279631; l++)
			pathCount += (user_friends[u1][l]?1:0) * (user_friends[u2][l]?1:0);

		System.out.println("u1: " + u1 + ", u2: " + u2 + " -> " + pathCount);

		return pathCount;
	}



	public static void main(String[] args) throws ClassNotFoundException 
	{	 
		String currentInterval = "2"; //args[0]; // e.g. is interval=2
		String intervals = "3"; //args[1]; // e.g. is intervals=7
		String usre_story_file_name = "Digg/" + intervals + "intervals_friends/digg_votes_" + currentInterval + "of" + intervals + ".txt"; // user-story and story-user infor for current time
		String labels_file_name = "Digg/" + intervals + "intervals_friends/labels_for_" + currentInterval + "of" + intervals + "_newLinks_in_" + Integer.toString((Integer.parseInt(currentInterval)+1)) + "of" + intervals + ".txt";				  // labels for current time based on next time
		String metaPath_file_name = "Digg/" + intervals + "intervals_friends/UUU_" + currentInterval + "of" + intervals + ".txt"; // outputFile


		String currentLine, numOfConnectedNodes;
		int from = 0, to = 0, userIndex = 0, friendIndex = 0;

		try{

			BufferedWriter bwUserstoryLabel = new BufferedWriter(new FileWriter(new File(metaPath_file_name)));

			BufferedReader userstoryFile = new BufferedReader(new FileReader(usre_story_file_name));
			// file format example (3 nodes)
			//3
			//0,0
			//1,3:2,1:3,1:4,1
			//...			
			int numOfNodes = Integer.parseInt(userstoryFile.readLine()); // reading numOfNodes
			for (int i=0; i < 279631; i++){  // considering only users and not stories
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
					friendIndex = Integer.parseInt(currentLine.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					user_friends[userIndex][friendIndex]=true;
				}
			}


			// Labels show friendship among users.
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
				bwUserstoryLabel.write(pathCount_UUU(sourceNode, destNode)+"\n");

				if (counter%10000==0){
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
