package metaPath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to generate list of pairs and labels based on user-friend relation for the next time interval
 *		Output file example
 *			6 
 *			0,1:1
 *			0,2:1
 *			1,0:1
 *			1,2:0
 *			2,0:1
 *			2,1:0
 * 
 * 		For a user u at time interval t, friendship made at time t+1 are positive samples (1)
 * 			for negative samples (0) eliminate friendids from [2113,12221] and sample randomly equal to the number of positive samples 
 * 
 * @author aminmf
 */
public class DiggLabelGenerator_friends {


	public static void main(String[] args) 
	{	 

		ArrayList<Integer> friendIds = new ArrayList<Integer>();
		//ArrayList<Integer> friendIds = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> userFriendTable = new ArrayList<ArrayList<Integer>>();
		ArrayList<HashMap<Integer,Integer>> allUsersLabels = new ArrayList<HashMap<Integer,Integer>>();

		String userFriendAllTimeFileName = "Digg/digg_friends_all.txt";
		String userFriendLabelFileName = "Digg/7intervals_friends/labels_for_6of7_newLinks_in_7of7.txt";	// this is labels for current time
		String userFriendNextTimeFileName = "Digg/7intervals_friends/digg_friends_7of7.txt";		// this is relations for next time
		String currentLineAllTimeFile, currentLineCurrentTimeFile, currentLineNextTimeFile, numOfNonZero=null;;
		int userIndex = 0, friendIndex = 0;

		// reading original user_friend_relation file
		try{
			BufferedReader brAllData = new BufferedReader(new FileReader(userFriendAllTimeFileName));
			BufferedReader brNextTimeData = new BufferedReader(new FileReader(userFriendNextTimeFileName));
			BufferedWriter bwuserFriendLabel = new BufferedWriter(new FileWriter(new File(userFriendLabelFileName)));

			// file format example (3 nodes)
			//3
			//0,0
			//1,3:2,1:3,1:4,1
			//...

			int total_size = 0;

			currentLineAllTimeFile = brAllData.readLine();
			currentLineNextTimeFile = brNextTimeData.readLine();

			int numOfNodes = Integer.parseInt(currentLineAllTimeFile);
			int from = 0;
			int to = 0;
			for (int i=0; i < numOfNodes; i++){
				HashMap<Integer,Integer> userPositiveLabels = new HashMap<Integer,Integer>();

				currentLineAllTimeFile = brAllData.readLine();
				from = 0;
				to = currentLineAllTimeFile.indexOf(",", from);
				userIndex = Integer.parseInt(currentLineAllTimeFile.substring(from,to));
				//System.out.println("userIndex:" + userIndex);
				from = to+1;
				to = currentLineAllTimeFile.indexOf(":", from);
				if (to<=0) to = currentLineAllTimeFile.length();
				numOfNonZero = currentLineAllTimeFile.substring(from,to);
				//System.out.println("numOfNonZero:" + numOfNonZero);

				ArrayList<Integer> n = new ArrayList<Integer>();

				for (int j=0; j < Integer.parseInt(numOfNonZero) ; j++){
					from = to+1;
					to = currentLineAllTimeFile.indexOf(",", from);
					friendIndex = Integer.parseInt(currentLineAllTimeFile.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + friendIndex);
					n.add(friendIndex);
				}
				userFriendTable.add(n);
				total_size += n.size();



				// Reading the next time data for positive samples
				currentLineNextTimeFile = brNextTimeData.readLine();
				from = 0;
				to = currentLineNextTimeFile.indexOf(",", from);
				userIndex = Integer.parseInt(currentLineNextTimeFile.substring(from,to));
				from = to+1;
				to = currentLineNextTimeFile.indexOf(":", from);
				if (to<=0) to = currentLineNextTimeFile.length();
				numOfNonZero = currentLineNextTimeFile.substring(from,to);
				//System.out.println("numOfNonZero:" + numOfNonZero);

				for (int j=0; j < Integer.parseInt(numOfNonZero) ; j++){
					from = to+1;
					to = currentLineNextTimeFile.indexOf(",", from);
					friendIndex = Integer.parseInt(currentLineNextTimeFile.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + friendIndex);
					userPositiveLabels.put(friendIndex,1);

				}

				//System.out.println(i + ", " + userPositiveLabels.size());

				allUsersLabels.add(userPositiveLabels);
			}

			System.out.println("Loaded user_friend matrix in memory.");
			System.out.println("Total links: " + total_size*2);

			brAllData.close();




			// for each user u and friend m, lablel = 0 or 1
			for (int u=0; u<279631; u++){
				// reset friendids for the user
				friendIds.clear();
				for (int i=0; i<279631; i++)
					friendIds.add(i);
				// read new friends
				//System.out.println(friendIds);
				//System.out.println("userFriendTable.get(u)-" + u + ":" + userFriendTable.get(u));
				for (Integer friendId : userFriendTable.get(u)){
					friendIds.remove(friendId);
				}
				//Collections.shuffle(friendIds);

				// generate negative samples from friendIds as much as positive ones
				int count = allUsersLabels.get(u).size();			
				if (count == 0) count=1; // generate at least one negative sample even if there is no positive one
				Random rand = new Random();
		        
				for(int j=0; j<count; j++) {
		            int index = rand.nextInt(friendIds.size());
					allUsersLabels.get(u).put(friendIds.remove(index),0);
		        }
				
				if (u%10000==0)
					System.out.println("Done till " + u);
			}


			for (int u=0; u<279631; u++){
				for (int m: allUsersLabels.get(u).keySet()) {
					int lablel = allUsersLabels.get(u).get(m);
					bwuserFriendLabel.write(u + "," +  m + ":" + lablel + "\n");
				}
			}

			
			
			bwuserFriendLabel.close();
			
		}catch (IOException e) 
		{
			e.printStackTrace();
		} 


	}


}
