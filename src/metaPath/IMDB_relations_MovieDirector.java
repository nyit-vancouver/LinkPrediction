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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to create usermoviealltime.txt to be used as input for the temporal inference work (TKDE16)
 *		file format example
 *			410 
 *			0,5:1,1:4,1:5,1:49,1:50,1
 *			1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1
 *
 *		
 *		We have 2113 users and 10197 movies. 10109 Movies were rated so the aggregated list is userids (0, 1, ..., 2112) followed by movieids + 2113 (2113, ... , 2113+10109)  
 *		Since there are missing ids in original data we need to convert them. E.g. userids start from 75.  
 * 
 * @user aminmf
 */
public class IMDB_relations_MovieDirector {
	private static HashMap<Integer, Integer> user_id_map = new HashMap<Integer, Integer>();    
	private static HashMap<Integer, Integer> movie_id_map = new HashMap<Integer, Integer>();    

	private static TreeMap<Integer, ArrayList<Integer>> user_movies_map = new TreeMap<Integer, ArrayList<Integer>>();    
	private static TreeMap<Integer, ArrayList<Integer>> movie_users_map = new TreeMap<Integer, ArrayList<Integer>>();    

	public static void main(String[] args) throws ClassNotFoundException 
	{	 

		String currentLineString, currentLineString1, director;
		int movieIndex, userIndex, userIdCounter = 0, movieIdCounter = 2113;
		TreeSet<String> directors = new TreeSet<String>();

		try{
			BufferedReader br = new BufferedReader(new FileReader("MovielensIMDB/original_user_movies.txt"));
			BufferedReader br1 = new BufferedReader(new FileReader("MovielensIMDB/movie_directors.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("MovielensIMDB/movie_director_relation.txt")));
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("MovielensIMDB/directors.txt")));
			//[Action, Adventure, Animation, Children, Comedy, Crime, Documentary, Drama, Fantasy, Film-Noir, Horror, IMAX, Musical, Mystery, Romance, Sci-Fi, Short, Thriller, War, Western]

			bw.write("newMovieID\t"+ "directorID\n");
			bw1.write("directorID\t"+ "director\n");


			// reading from the saved mappings
			FileInputStream fileIn = new FileInputStream("MovielensIMDB/user_id_map.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			user_id_map = (HashMap<Integer, Integer>) in.readObject();
			in.close();
			fileIn.close();
			FileInputStream fileIn1 = new FileInputStream("MovielensIMDB/movie_id_map.ser");
			ObjectInputStream in1 = new ObjectInputStream(fileIn1);
			movie_id_map = (HashMap<Integer, Integer>) in1.readObject();
			in1.close();
			fileIn1.close();


			// ignore headers in the first line
			currentLineString = br.readLine();
			currentLineString1 = br1.readLine();

			// Total records all times are 855598
			// 3 intervals have 285199, 5 intervals have 171119, and 7 intervals have 122228 records in each file 
			int lineCounter = 0;



			while ((currentLineString1 = br1.readLine()) != null) {

				ArrayList<Integer> moviesList = new ArrayList<Integer>();
				ArrayList<Integer> directorList = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString1,"\t");  
				movieIndex = Integer.parseInt(st.nextToken());
				director = st.nextToken();
				directors.add(director);
				if (movie_id_map.containsKey(movieIndex)){
					movieIndex = movie_id_map.get(movieIndex);
					bw.write(movieIndex + "\t" + director+"\n");
				}

			}


			System.out.println(directors);
			int i=0;
			for (String g: directors){
				bw1.write(i++ + "\t" + g+"\n");
			}


			while ((currentLineString = br.readLine()) != null) {
				lineCounter++;
				if (lineCounter==1)
					break;

				//if (lineCounter>=122228)
				//	continue;
				//if (lineCounter<=5*122228 || lineCounter>=6*122228)
				//	continue;
				//if (lineCounter<=6*122228)
				//	continue;

				ArrayList<Integer> moviesList = new ArrayList<Integer>();
				ArrayList<Integer> usersList = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				userIndex = Integer.parseInt(st.nextToken());
				movieIndex = Integer.parseInt(st.nextToken());

				// ignoring date fields
				int temp = Integer.parseInt(st.nextToken());
				temp = Integer.parseInt(st.nextToken());
				temp = Integer.parseInt(st.nextToken());


				// using the map read from file
				userIndex = user_id_map.get(userIndex);
				movieIndex = movie_id_map.get(movieIndex);

				if (user_movies_map.containsKey(userIndex)){
					moviesList = user_movies_map.get(userIndex);
				}
				moviesList.add(movieIndex);
				user_movies_map.put(userIndex, moviesList);

				if (movie_users_map.containsKey(movieIndex)){
					usersList = movie_users_map.get(movieIndex);
				}
				usersList.add(userIndex);
				movie_users_map.put(movieIndex, usersList);
			}

			/*
			System.out.println(userIdCounter);
			System.out.println(movieIdCounter);
			//System.out.println(movie_users_map);


			// file format example for the temporal inference movie (TKDE16)
			//410 
			//0,5:1,1:4,1:5,1:49,1:50,1
			//1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1

			// add number of nodes in the first line (2113 users + 10109 rated movies = 12222)
			bw.write("12222\n");
			// users-movies list
			for (int i=0; i<2113; i++){
				if (user_movies_map.containsKey(i)){
					ArrayList<Integer> list = user_movies_map.get(i);
					bw.write(i + "," + list.size());
					Set<Integer> set = new HashSet<>();
					set.addAll(list);
					list.clear();
					list.addAll(set);
					Collections.sort(list);

					for (Integer m: list){
						bw.write(":" + m + ",1");
					}
					bw.write("\n");
				}else{
					bw.write(i + ",0\n");
				}
			}
			// movies-users list (10109+2113=12222)
			for (int i=2113; i<12222; i++){
				if (movie_users_map.containsKey(i)){
					ArrayList<Integer> list = movie_users_map.get(i);
					Set<Integer> set = new HashSet<>();
					set.addAll(list);
					list.clear();
					list.addAll(set);
					Collections.sort(list);


					bw.write(i + "," + list.size());
					for (Integer u: list){
						bw.write(":" + u + ",1");
					}
					bw.write("\n");
				}else{
					bw.write(i + ",0\n");
				}
			}
			 */
			br.close();
			br1.close();
			bw.close();
			bw1.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
