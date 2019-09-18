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
 * We have 2113 users and 10109 rated movies  
 */


public class IMDBMetaPathUMDM {

	/**
	 * Given index of a user and a movie, calculate number of different meta-path of type U-M-D-M (user-movie-director-movie)
	 * 
	 * @param userIndex u
	 * @param movieIndex m
	 * @return pathCount between them
	 */

	// old version: not efficient
	//private static int[][] user_movies = new int[2113][10109];
	//private static int[][] movie_directors = new int[10109][4060];
	//private static int[][] director_movies = new int[4060][10109];

	private static Map<Integer, ArrayList<Integer>> user_movies_map = new HashMap<Integer, ArrayList<Integer>>();    
	private static Map<Integer, ArrayList<Integer>> movie_directors_map = new HashMap<Integer, ArrayList<Integer>>();    
	private static Map<Integer, ArrayList<Integer>> director_movies_map = new HashMap<Integer, ArrayList<Integer>>();    

	private static HashMap<String, Integer> director_id_map = new HashMap<String, Integer>();    

	// UMDM = UM*MD*DM

	public static int pathCount_UMDM(int u, int m){
		int pathCount = 0;

		m = m-2113;  // re-indexing movieID to start from 0 

		// user_movie_director_u[i] = sum user_movies[u][j]*movie_directors[j][i] for j=1..10109
		int[] user_movie_director_u = new int[4060];

		//old version : not efficient
		//for(int i=0;i<4060; i++)
		//	for(int j=0;j<10109; j++)
		//		user_movie_director_u[i] += user_movies[u][j] * movie_directors[j][i];

		if (user_movies_map.containsKey(u))
			for(int l: user_movies_map.get(u))
				if (movie_directors_map.containsKey(l))
					for (int k : movie_directors_map.get(l)){
						user_movie_director_u[k]++;
					}

		//old version : not efficient
		//for(int k=0;k<4060; k++)
		//	pathCount += user_movie_director_u[k] * director_movies[k][m];

		for(int k=0;k<4060; k++){
			if (director_movies_map.containsKey(k))
				if (director_movies_map.get(k).contains(m))
					pathCount += user_movie_director_u[k]; // pathCount += user_movie_director_u[k] * 1
		}

		//System.out.println("u: " + u + ", m: " + m + " -> " + pathCount);

		return pathCount;
	}


	public static void main(String[] args) throws ClassNotFoundException 
	{	
		String currentInterval = "5";//args[0]; // e.g. is interval=1
		String intervals = "7"; //args[1]; // e.g. is intervals=7
		String usre_movie_file_name = "IMDB/" + intervals + "intervals/user_movie_relation_" + currentInterval + "of" + intervals + ".txt"; // user-movie and movie-user infor for current time
		String labels_file_name = "IMDB/" + intervals + "intervals/labels_for_" + currentInterval + "of" + intervals + "_newMovies_in_" + Integer.toString((Integer.parseInt(currentInterval)+1)) + "of" + intervals + ".txt";				  // labels for current time based on next time
		String movie_directors_file_name = "MovielensIMDB/movie_director_relation.txt";	// all time movie-directors
		String directors_file_name = "MovielensIMDB/directors.txt";				  // all directors ids
		String metaPath_file_name = "IMDB/" + intervals + "intervals/UMDM_" + currentInterval + "of" + intervals + ".txt"; // outputFile

		String currentLine, numOfConnectedNodes;
		int from = 0, to = 0, userIndex = 0, movieIndex = 0, directorIndex = 0;

		try{

			BufferedWriter bwUserMovieLabel = new BufferedWriter(new FileWriter(new File(metaPath_file_name)));

			BufferedReader userMovieFile = new BufferedReader(new FileReader(usre_movie_file_name));
			// userMovieFile file format example (3 nodes)
			//3
			//0,0
			//1,3:2,1:3,1:4,1
			//...			
			int numOfNodes = Integer.parseInt(userMovieFile.readLine()); // reading numOfNodes
			for (int i=0; i < 2113; i++){  // considering only users and not movies
				ArrayList<Integer> moviesList = new ArrayList<Integer>();
				currentLine = userMovieFile.readLine();
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
					movieIndex = Integer.parseInt(currentLine.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;

					// old version : not efficient
					// user_movies[userIndex][movieIndex-2113]=1;

					ArrayList<Integer> a = new ArrayList<Integer>();
					if (user_movies_map.containsKey(userIndex))
						a = user_movies_map.get(userIndex);
					a.add( (movieIndex-2113));	
					user_movies_map.put(userIndex, a);

				}
			}


			BufferedReader directorIdsFile = new BufferedReader(new FileReader(directors_file_name));
			// moviedirectorsFile format example
			//directorID	director
			//2620	majid_majidi
			currentLine = directorIdsFile.readLine(); // ignore the first line
			while ((currentLine = directorIdsFile.readLine()) != null){
				StringTokenizer st = new StringTokenizer(currentLine,"\t");  
				directorIndex = Integer.parseInt(st.nextToken());
				director_id_map.put(st.nextToken(), directorIndex);
			}

			BufferedReader moviedirectorsFile = new BufferedReader(new FileReader(movie_directors_file_name));
			// moviedirectorsFile format example
			//newMovieID	directorID
			//4430	majid_majidi
			currentLine = moviedirectorsFile.readLine(); // ignore the first line
			while ((currentLine = moviedirectorsFile.readLine()) != null){
				StringTokenizer st = new StringTokenizer(currentLine,"\t");  
				movieIndex = Integer.parseInt(st.nextToken());
				directorIndex = director_id_map.get(st.nextToken());

				// old version : not efficient
				//movie_directors[movieIndex-2113][directorIndex] = 1;
				//director_movies[directorIndex][movieIndex-2113] = 1;

				ArrayList<Integer> b = new ArrayList<Integer>();
				if (movie_directors_map.containsKey(movieIndex-2113))
					b = movie_directors_map.get(movieIndex-2113);
				b.add( (directorIndex));	
				movie_directors_map.put(movieIndex-2113, b);

				ArrayList<Integer> c = new ArrayList<Integer>();
				if (director_movies_map.containsKey(directorIndex))
					c = director_movies_map.get(directorIndex);
				c.add( (movieIndex-2113));	
				director_movies_map.put(directorIndex, c);

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
				//bwUserMovieLabel.write(heteSim_UMDM(sourceNode, destNode)+"\n");
				bwUserMovieLabel.write(pathCount_UMDM(sourceNode, destNode)+"\n");

				if (counter%100000==0){
					System.out.println(counter);
					endTime = System.currentTimeMillis();
					duration = (endTime - startTime);
					System.out.println("Time passed " + duration/1000 + " seconds!");
				}
				//System.out.println("-pathSim(" + sourceNode + "," + destNode+ ") = " + pathSim(Integer.toString(sourceNode), Integer.toString(destNode)) );
			}


			userMovieFile.close();
			moviedirectorsFile.close();
			directorIdsFile.close();
			bwUserMovieLabel.close();

		}catch (IOException e) 
		{
			System.out.println(e);
			e.printStackTrace();
		} 

	}

}