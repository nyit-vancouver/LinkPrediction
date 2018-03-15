package preprocess;

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
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import preprocess.Connection;;

public class Digg_votes_sort {

	private static TreeMap<Integer, ArrayList<Integer>> links = new TreeMap<Integer, ArrayList<Integer>>();    
	private static HashMap<Integer, Integer> userIdrecode = new HashMap<Integer, Integer>();    
	private static HashMap<Integer, Integer> storyIdrecode = new HashMap<Integer, Integer>();    
	private static ArrayList<Connection> connections = new ArrayList<Connection>();    

	public static void main(String[] args) 
	{	
		String currentLineString = "", index = "", temp;
		// userIdCounter = 279631 because we have 279631 users (voter and non-voter) from digg_friends
		// storyIdCounter = 336225 because in total we have 336225 users (both from digg_vote and digg_friends)
		int index1, index2, timestamp, userIdCounter = 279631, storyIdCounter = 336225;  
		try{
			// Table digg_votes contains 3,018,197 votes on 3553 popular stories made by 139,409 distinct users. 
			// vote_time_stamp, voter_id, story_id
			BufferedReader br = new BufferedReader(new FileReader("Digg/digg_votes.txt"));  
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Digg/digg_votes_all.txt"))); 
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("Digg/digg_votes_sorted.txt"))); // 905565 links
			// =336225+3553=339778
			bw.write("339778\n");

			FileInputStream fileIn1 = new FileInputStream("digg_allUsersIdRecode_map.ser");
			ObjectInputStream in1 = new ObjectInputStream(fileIn1);
			userIdrecode = (HashMap<Integer, Integer>) in1.readObject();
			in1.close();
			fileIn1.close();


			while ((currentLineString = br.readLine()) != null) {

				ArrayList<Integer> list1 = new ArrayList<Integer>();
				ArrayList<Integer> list2 = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,",");  
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
				
				if (storyIdrecode.containsKey(index2)){
					index2 = storyIdrecode.get(index2);
				}else{
					storyIdrecode.put(index2, storyIdCounter);
					index2 = storyIdCounter;
					storyIdCounter++;
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
			System.out.println("links.size(): " + links.size());
			System.out.println("userIdrecode.size(): " + userIdrecode.size());
			System.out.println("storyIdrecode.size(): " + storyIdrecode.size());
			System.out.println("userIdCounter: " + userIdCounter);
			System.out.println("storyIdCounter: " + storyIdCounter);


			for(Connection c: connections){
				bw1.write(c.index1 + "\t" + c.index2 + "\t" + c.timestamp + "\n");
			}


			for (int i=0; i<339778; i++){
				if (!links.containsKey(i)){
					System.out.println("No data for " + i);
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



			FileOutputStream fileOut2 = new FileOutputStream("digg_storyIdrecode_map.ser");
			ObjectOutputStream out2 = new ObjectOutputStream(fileOut2);
			out2.writeObject(storyIdrecode);
			out2.close();
			fileOut2.close();


			br.close();
			bw.close();
			bw1.close();

		}catch (IOException | ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 

	}

}

