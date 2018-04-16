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


public class IMDBMetaPathUMUM {

	/**
	 * Given index of a user and a movie, calculate number of different meta-path of type U-M-U-M
	 * @param userIndex u
	 * @param movieIndex m
	 * @return pathCount between them
	 */

	private static int[][] user_movies = new int[2113][10109];
	private static int[][] movie_users = new int[10109][2113];

	
	// UMUM = UM*MU*UM
	public static int pathCount_UMUM(int u, int m){
		int pathCount = 0;

		m = m-2113;  // re-indexing movieID to start from 0 

		// Let A be user_movie matrix and B be movie_user matrix
		// D_um = sum C_uk*A_km for k=1..2113, and C_uk = sum A_ul*Blk for l=1..10109

		int[] C_u = new int[2113];

		for(int k=0;k<2113; k++)
			for(int l=0;l<10109; l++)
				C_u[k] += user_movies[u][l] * movie_users[l][k];

		for(int k=0;k<2113; k++)
			pathCount += C_u[k] * user_movies[k][m];

		//System.out.println("u: " + u + ", m: " + m + " -> " + pathCount);
		//System.out.println(a + "-" + b);

		return pathCount;
	}


	public static float heteSim_UMUM(int u, int m){
		float heteSim1 = 0, heteSim2 = 0;

		m = m-2113;  // re-indexing movieID to start from 0 

		// P:UMUM decomposition (1): PL=UMU and PR=UM so PR^-1=MU (reverse path) 

		// user_movie_user_u[i] = sum user_movies[u][j]*movie_users[j][i] for j=1..10109
		int[] user_movie_user_u = new int[2113];

		for(int i=0;i<2113; i++)
			for(int j=0;j<10109; j++)
				user_movie_user_u[i] += user_movies[u][j] * movie_users[j][i];

		int sum1 = 0, sum2 =0; // for normalization
		for(int k=0;k<2113; k++){
			sum1 += user_movie_user_u[k];
			sum2 += movie_users[m][k];
		}
		if (sum1==0 || sum2==0){
			heteSim1=0;
		}else{

			// heteSim1 = user_movie_user_u * movie_users[m]^T / Norm2(user_movie_user_u)*Norm2(movie_users[m])
			for(int k=0;k<2113; k++)
				heteSim1 += (user_movie_user_u[k]/(float)sum1)*(movie_users[m][k]/(float)sum2);

			float a = 0, b = 0, c;
			for(int k=0;k<2113; k++){
				a+=Math.pow((user_movie_user_u[k]/(float)sum1), 2);
				b+=Math.pow((movie_users[m][k]/(float)sum2), 2);
			}
			//System.out.println(a + " - " + b);
			c = (float) (Math.sqrt(a)*Math.sqrt(b));  
			if (c==0) 
				heteSim1=0;
			else
				heteSim1 = heteSim1/c;

			//System.out.println("heteSim1: " + heteSim1);
		}

		// P:UMUM decomposition (2): PL=UM and PR=MUM so PR^-1=MUM (reverse path) 
		// user_movie_user_m[i] = sum movie_users[m][j]*user_movies[j][i] for j=1..2113
		int[] movie_user_movie_m = new int[10109];

		for(int i=0;i<10109; i++)
			for(int j=0;j<2113; j++)
				movie_user_movie_m[i] += movie_users[m][j] * user_movies[j][i];

		sum1=0; sum2=0;
		for(int k=0;k<10109; k++){
			sum1 += user_movies[u][k];
			sum2 += movie_user_movie_m[k];
		}

		if (sum1==0 || sum2==0){
			heteSim2=0;
		}else{
			// heteSim2 = user_movies[u]*movie_user_movie_m / Norm2(user_movies[u])*Norm2(movie_user_movie_m)
			for(int k=0;k<10109; k++)
				heteSim2 += (user_movies[u][k]/(float)sum1)*(movie_user_movie_m[k]/(float)sum2);

			float a = 0, b = 0; float c;
			for(int k=0;k<10109; k++){
				a+=Math.pow((movie_user_movie_m[k]/(float)sum1), 2);
				b+=Math.pow((user_movies[u][k]/(float)sum2), 2);
			}
			//System.out.println(a + " - " + b);
			c = (float) (Math.sqrt(a)*Math.sqrt(b));  
			if (c==0) 
				heteSim2=0;
			else
				heteSim2 = heteSim2/c;
			//System.out.println("heteSim2: " + heteSim2);
		}

		//System.out.println("u: " + u + ", m: " + m + ": heteSim-> " + (heteSim1+heteSim2)/2);

		return (heteSim1+heteSim2)/2;
	}


	/**
	 * Meta paths:
	 * - user-movie-user-movie (UMUM)
	 * - user-movie-genre-movie (UMGM)
	 * - user-movie-director-movie (UMDM)
	 * - user-movie-actor-movie (UMDM)
	 */

	public static void main(String[] args) throws ClassNotFoundException 
	{	 
		String currentInterval = args[0]; // e.g. is interval=2
		String intervals = args[1]; // e.g. is intervals=7
		String usre_movie_file_name = "IMDB/" + intervals + "intervals/user_movie_relation_" + currentInterval + "of" + intervals + ".txt"; // user-movie and movie-user infor for current time
		String labels_file_name = "IMDB/" + intervals + "intervals/labels_for_" + currentInterval + "of" + intervals + "_newMovies_in_" + Integer.toString((Integer.parseInt(currentInterval)+1)) + "of" + intervals + ".txt";				  // labels for current time based on next time
		String metaPath_file_name = "IMDB/" + intervals + "intervals/UMUM_" + currentInterval + "of" + intervals + ".txt"; // outputFile


		String currentLine, numOfConnectedNodes;
		int from = 0, to = 0, userIndex = 0, movieIndex = 0;

		try{

			BufferedWriter bwUserMovieLabel = new BufferedWriter(new FileWriter(new File(metaPath_file_name)));

			BufferedReader userMovieFile = new BufferedReader(new FileReader(usre_movie_file_name));
			// file format example (3 nodes)
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
					user_movies[userIndex][movieIndex-2113]=1;
				}
			}
			for (int i=0; i < 10109; i++){  // considering only movies
				ArrayList<Integer> usersList = new ArrayList<Integer>();
				currentLine = userMovieFile.readLine();
				from = 0;
				to = currentLine.indexOf(",", from);
				movieIndex = Integer.parseInt(currentLine.substring(from,to));
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
					movie_users[movieIndex-2113][userIndex]=1;
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
				//bwUserMovieLabel.write(heteSim_UMUM(sourceNode, destNode)+"\n");
				bwUserMovieLabel.write(pathCount_UMUM(sourceNode, destNode)+"\n");

				if (counter%10000==0){
					System.out.println(counter);
					endTime = System.currentTimeMillis();
					duration = (endTime - startTime);
					System.out.println("Time passed " + duration/1000 + " seconds!");
				}
				//System.out.println("-pathSim(" + sourceNode + "," + destNode+ ") = " + pathSim(Integer.toString(sourceNode), Integer.toString(destNode)) );
			}


			userMovieFile.close();
			bwUserMovieLabel.close();

		}catch (IOException e) 
		{
			System.out.println(e);
			e.printStackTrace();
		} 

	}

}
