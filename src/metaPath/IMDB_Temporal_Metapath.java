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
public class IMDB_Temporal_Metapath {

	public static void main(String[] args) throws ClassNotFoundException 
	{	 

		String currentLineString1, currentLineString2, actor;
		int from, to, movieIndex, userIndex, userIdCounter = 0, movieIdCounter = 2113;
		ArrayList<TreeSet<Integer>> UMUM_list = new ArrayList<TreeSet<Integer>>();

		try{
			BufferedReader labels = new BufferedReader(new FileReader("IMDB/3intervals/labels_for_2of3_newMovies_in_3of3.txt"));
			BufferedReader UMUM = new BufferedReader(new FileReader("IMDB/3intervals/UMUM_2of3.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("IMDB/3intervals/UMUM_relation_2of3.txt")));

			for (int i=0;i<12222;i++)
				UMUM_list.add(new TreeSet<Integer>());
			
			
			// file format example
			//0,1:1
			//...
			//2,3:0
			long startTime = System.currentTimeMillis();
			long endTime, duration;

			int sourceNode, destNode, counter = 0;
			while ((currentLineString1 = labels.readLine()) != null){
				counter++;
				from = 0;
				to = currentLineString1.indexOf(",", from);
				sourceNode = Integer.parseInt(currentLineString1.substring(from,to));
				from = to+1;
				to = currentLineString1.indexOf(":", from);
				destNode = Integer.parseInt(currentLineString1.substring(from,to));

				currentLineString2 = UMUM.readLine();
				
				if (Integer.parseInt(currentLineString2)!=0){
					UMUM_list.get(sourceNode).add(destNode);
					UMUM_list.get(destNode).add(sourceNode);
					//System.out.println(sourceNode + " " + destNode);
				}

			}

		//	System.out.println(UMUM_list.get(0));
			
			
			// file format example for the temporal inference movie (TKDE16)
			//410 
			//0,5:1,1:4,1:5,1:49,1:50,1
			//1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1

			// add number of nodes in the first line (2113 users + 10109 rated movies = 12222)
			bw.write("12222\n");
			// users-movies list
			for (int i=0; i<12222; i++){
				if (UMUM_list.get(i).size()>0){
					bw.write(i + "," + UMUM_list.get(i).size());
					for (Integer m: UMUM_list.get(i)){
						bw.write(":" + m + ",1");
					}
					bw.write("\n");
				}else{
					bw.write(i + ",0\n");
				}
			}

			
			UMUM.close();
			labels.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
