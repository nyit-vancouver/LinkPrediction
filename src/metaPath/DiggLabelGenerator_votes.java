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
 * 		This is to generate list of pairs and labels based on user-Story rating relation for the next time interval
 *		Output file example
 *			6 
 *			0,1:1
 *			0,2:1
 *			1,0:1
 *			1,2:0
 *			2,0:1
 *			2,1:0
 * 
 * 		For a user u at time interval t, stories rated at time t+1 are positive samples (1)
 * 			for negative samples (0) eliminate storyids from [2113,12221] and sample randomly equal to the number of positive samples 
 * 
 * @author aminmf
 */
public class DiggLabelGenerator_votes {


	public static void main(String[] args) 
	{	 

		ArrayList<Integer> storyIds = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> userStoryTable = new ArrayList<ArrayList<Integer>>();
		ArrayList<HashMap<Integer,Integer>> allUsersLabels = new ArrayList<HashMap<Integer,Integer>>();

		String userStoryAllTimeFileName = "Digg/digg_votes_all.txt";
		String userStoryLabelFileName = "Digg/5intervals_votes/labels_for_4of5_newLinks_in_5of5.txt";	// this is labels for current time
		String userStoryNextTimeFileName = "Digg/5intervals_votes/digg_votes_5of5.txt";		// this is relations for next time
		String currentLineAllTimeFile, currentLineCurrentTimeFile, currentLineNextTimeFile, numOfNonZero=null;;
		int userIndex = 0, StoryIndex = 0;

		// reading original user_Story_relation file
		try{
			BufferedReader brAllData = new BufferedReader(new FileReader(userStoryAllTimeFileName));
			BufferedReader brNextTimeData = new BufferedReader(new FileReader(userStoryNextTimeFileName));
			BufferedWriter bwUserStoryLabel = new BufferedWriter(new FileWriter(new File(userStoryLabelFileName)));

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
				//System.out.println("nodeIndex:" + nodeIndex);
				from = to+1;
				to = currentLineAllTimeFile.indexOf(":", from);
				if (to<=0) to = currentLineAllTimeFile.length();
				numOfNonZero = currentLineAllTimeFile.substring(from,to);
				//System.out.println("numOfNonZero:" + numOfNonZero);

				ArrayList<Integer> n = new ArrayList<Integer>();

				for (int j=0; j < Integer.parseInt(numOfNonZero) ; j++){
					from = to+1;
					to = currentLineAllTimeFile.indexOf(",", from);
					StoryIndex = Integer.parseInt(currentLineAllTimeFile.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + neighborIndex);
					n.add(StoryIndex);
				}
				userStoryTable.add(n);
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
					StoryIndex = Integer.parseInt(currentLineNextTimeFile.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + neighborIndex);
					userPositiveLabels.put(StoryIndex,1);

				}

				//System.out.println(i + ", " + userPositiveLabels.size());

				allUsersLabels.add(userPositiveLabels);
			}

			System.out.println("Loaded user_Story matrix in memory.");
			System.out.println("Total links: " + total_size*2);

			brAllData.close();




			// for each user u and Story m, lablel = 0 or 1
			for (int u=0; u<339778; u++){
				// reset Storyids for the user
				storyIds.clear();
				for (int i=336225; i<339778; i++)
					storyIds.add(i);
				// read rated stories
				//System.out.println(StoryIds);
				//System.out.println(userStoryTable.get(u));
				for (Integer StoryId : userStoryTable.get(u)){
					storyIds.remove(StoryId);
				}
				//Collections.shuffle(storyIds);

				// generate negative samples from StoryIds as much as positive ones
				int count = allUsersLabels.get(u).size();			
				if (count == 0) count=1; // generate at least one negative sample even if there is no positive one
				Random rand = new Random();
				for(int j=0; j<count; j++) {
					if (storyIds.size()==0)
						break;
					int index = rand.nextInt(storyIds.size());
					allUsersLabels.get(u).put(storyIds.remove(index),0);
				}
				if (u%50000==0)
					System.out.println("Done till " + u);

			}


			for (int u=0; u<339778; u++){
				for (int s: allUsersLabels.get(u).keySet()) {
					int lablel = allUsersLabels.get(u).get(s);
					bwUserStoryLabel.write(u + "," +  s + ":" + lablel + "\n");
				}
			}

			bwUserStoryLabel.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 


	}


}
