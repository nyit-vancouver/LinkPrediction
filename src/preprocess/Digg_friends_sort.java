package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import preprocess.Connection;;

public class Digg_friends_sort {

	private static ArrayList<Connection> connections = new ArrayList<Connection>();
	private static TreeMap<Integer, ArrayList<Integer>> links = new TreeMap<Integer, ArrayList<Integer>>();    
	private static HashMap<Integer, Integer> userIdrecode = new HashMap<Integer, Integer>();    

	private static HashSet<Integer> index1Set = new HashSet<Integer>();
	private static HashSet<Integer> index2Set = new HashSet<Integer>();

	public static void main(String[] args) 
	{	
		String currentLineString = "", index = "", temp;
		int index1, index2, timestamp, userIdCounter = 0;
		try{
		
			// Table digg_friends contains 1,731,658 friendship links of 71,367 distinct users. 
			// Table digg_votes contains 3,018,197 votes on 3553 stories made by 139,409 distinct users. 
			// Voters who do not appear in the digg_friends table did not specify any friends at the time data was collected. 
			// mutual (1/0), friendship_time_stamp, user_id, friend_id
			// 1,1214799565,336224,266641

			BufferedReader br = new BufferedReader(new FileReader("Digg/digg_friends.txt"));  
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Digg/digg_friends_all.txt"))); 
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("Digg/digg_friends_sorted.txt"))); // 905565 links
			// = 71,367 users (voters) + 208,264 (non-voter user) = 279,631 total users
			bw.write("279631\n");
			
			while ((currentLineString = br.readLine()) != null) {

				ArrayList<Integer> list1 = new ArrayList<Integer>();
				ArrayList<Integer> list2 = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,",");  
				st.nextToken(); // ignoring mutual (1/0) indicator
				timestamp = Integer.parseInt(st.nextToken());
				index1 = Integer.parseInt(st.nextToken());
				index2 = Integer.parseInt(st.nextToken());
				
				// recoding
				if (userIdrecode.containsKey(index1)){
					index1 = userIdrecode.get(index1);
				}else{
					userIdrecode.put(index1, userIdCounter);
					index1 = userIdCounter;
					userIdCounter++;
				}
				
				if (userIdrecode.containsKey(index2)){
					index2 = userIdrecode.get(index2);
				}else{
					userIdrecode.put(index2, userIdCounter);
					index2 = userIdCounter;
					userIdCounter++;
				}
				
				Connection c = new Connection(index1,index2,timestamp);
				connections.add(c);

				
				if (links.containsKey(index1)){
					list1 = links.get(index1);
				}
				if (!list1.contains(index2))
					list1.add(index2);
				links.put(index1, list1);

				if (links.containsKey(index2)){
					list2 = links.get(index2);
				}
				if (!list2.contains(index1))
					list2.add(index1);
				links.put(index2, list2);

				
			}


			Collections.sort(connections);
			
			System.out.println("connections.size(): " + connections.size());
			System.out.println("userIdrecode.size(): " + userIdrecode.size());
			System.out.println("userIdCounter: " + userIdCounter);


			for(Connection c: connections){
				bw1.write(c.index1 + "\t" + c.index2 + "\t" + c.timestamp + "\n");
			}
			
			
			for (int i=0; i<279631; i++){
				if (!links.containsKey(i)){
					//System.out.println("No data for " + i);
					bw.write(i + ",0\n");
					continue;
				}
				bw.write(i + "," + links.get(i).size());
				ArrayList<Integer> list = links.get(i);
				Collections.sort(list);
				for (Integer j: list)
					bw.write(":" + j + ",1");
				bw.write("\n");
			}

			FileOutputStream fileOut1 = new FileOutputStream("digg_allUsersIdRecode_map.ser");
			ObjectOutputStream out1 = new ObjectOutputStream(fileOut1);
			out1.writeObject(userIdrecode);
			out1.close();
			fileOut1.close();
			
			br.close();
			bw.close();
			bw1.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}

