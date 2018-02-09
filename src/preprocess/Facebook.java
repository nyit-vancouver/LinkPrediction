package preprocess;

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
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class Facebook {

	private static TreeMap<Integer, ArrayList<Integer>> links = new TreeMap<Integer, ArrayList<Integer>>();    
	private static ArrayList<Connection> connections = new ArrayList<Connection>();    

	public static void main(String[] args) 
	{	
		String currentLineString = "", index = "", temp;
		int index1, index2, timestamp, counter = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader("Facebook/facebook-links.txt"));  
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Facebook/facebook_all.txt"))); 
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("Facebook/facebook-links_sorted.txt"))); // 905565 links

			bw.write("63731\n");

			while ((currentLineString = br.readLine()) != null) {
				counter++;
				//if (counter==5000)
				//	break;

				//if (counter<5000)
				//	continue;
				//if (counter==10000)
				//	break;

				//if (counter<10000)
				//	continue;

				ArrayList<Integer> list1 = new ArrayList<Integer>();
				ArrayList<Integer> list2 = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				index1 = Integer.parseInt(st.nextToken())-1;
				index2 = Integer.parseInt(st.nextToken())-1;
				temp = st.nextToken();
				if (temp.equals("\\N")){ // ignore links without time
					//bw1.write(index1 + "\t" + index2 + "\t1\n");
					continue;
				}
				timestamp = Integer.parseInt(temp);

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

			for(Connection c: connections){
				bw1.write(c.index1 + "\t" + c.index2 + "\t" + c.timestamp + "\n");
			}

			
			for (int i=0; i<63731; i++){
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

			br.close();
			bw.close();
			bw1.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}

class Connection implements Comparable<Connection>{

	int index1, index2, timestamp;
	
	public Connection(int index1, int index2, int timestamp) {
		this.index1 = index1;
		this.index2 = index2;
		this.timestamp = timestamp;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public int compareTo(Connection compareFruit) {
		int compareQuantity = ((Connection) compareFruit).getTimestamp();
		//ascending order
		return this.timestamp - compareQuantity;
	}
}