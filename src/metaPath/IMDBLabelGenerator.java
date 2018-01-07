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
 * 		This is to generate list of pairs and labels based on user-movie rating relation for the next time interval
 *		Output file example
 *			6 
 *			0,1:1
 *			0,2:1
 *			1,0:1
 *			1,2:0
 *			2,0:1
 *			2,1:0
 * 
 * 		For a user u at time interval t, movies rated at time t+1 are positive samples (1)
 * 			for negative samples (0) eliminate movieids from [2113,12221] and sample randomly equal to the number of positive samples 
 * 
 * @author aminmf
 */
public class IMDBLabelGenerator {


	public static void main(String[] args) 
	{	 

		ArrayList<Integer> movieIds = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> userMovieTable = new ArrayList<ArrayList<Integer>>();
		ArrayList<HashMap<Integer,Integer>> allUsersLabels = new ArrayList<HashMap<Integer,Integer>>();

		String userMovieAllTimeFileName = "IMDB/all/user_movie_relation.txt";
		String userMovieLabelFileName = "IMDB/7intervals/labels_for_6of7_newMovies_in_7of7.txt";	// this is labels for current time
		String userMovieNextTimeFileName = "IMDB/7intervals/user_movie_relation_7of7.txt";		// this is relations for next time
		String currentLineAllTimeFile, currentLineCurrentTimeFile, currentLineNextTimeFile, numOfNonZero=null;;
		int userIndex = 0, movieIndex = 0;

		// reading original user_movie_relation file
		try{
			BufferedReader brAllData = new BufferedReader(new FileReader(userMovieAllTimeFileName));
			BufferedReader brNextTimeData = new BufferedReader(new FileReader(userMovieNextTimeFileName));
			BufferedWriter bwUserMovieLabel = new BufferedWriter(new FileWriter(new File(userMovieLabelFileName)));

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
			//			for (int i=0; i < numOfNodes; i++){
			for (int i=0; i < 2113; i++){  // considering only users and not movies

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
					movieIndex = Integer.parseInt(currentLineAllTimeFile.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + neighborIndex);
					n.add(movieIndex);
				}
				userMovieTable.add(n);
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
					movieIndex = Integer.parseInt(currentLineNextTimeFile.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + neighborIndex);
					userPositiveLabels.put(movieIndex,1);

				}

				System.out.println(i + ", " + userPositiveLabels.size());

				allUsersLabels.add(userPositiveLabels);
			}

			System.out.println("Loaded user_movie matrix in memory.");
			System.out.println("Total links: " + total_size*2);

			brAllData.close();




			// for each user u and movie m, lablel = 0 or 1
			for (int u=0; u<2113; u++){
				// reset movieids for the user
				movieIds.clear();
				for (int i=2113; i<12221; i++)
					movieIds.add(i);
				// read rated movies
				//System.out.println(movieIds);
				//System.out.println(userMovieTable.get(u));
				for (Integer movieId : userMovieTable.get(u)){
					movieIds.remove(movieId);
				}
				Collections.shuffle(movieIds);

				// generate negative samples from movieIds as much as positive ones
				int count = allUsersLabels.get(u).size();
				if (count == 0) count=1; // generate at least one negative sample even if there is no positive one
				for (int mid: movieIds){
					allUsersLabels.get(u).put(mid,0);
					if (--count == 0)
						break;
				}

			}


			for (int u=0; u<2113; u++){
				for (int m: allUsersLabels.get(u).keySet()) {
					int lablel = allUsersLabels.get(u).get(m);
					bwUserMovieLabel.write(u + "," +  m + ":" + lablel + "\n");
				}
			}

			bwUserMovieLabel.close();
			
		}catch (IOException e) 
		{
			e.printStackTrace();
		} 


	}


}
