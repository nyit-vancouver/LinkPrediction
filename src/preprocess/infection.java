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


public class infection {

	private static TreeMap<Integer, ArrayList<Integer>> links = new TreeMap<Integer, ArrayList<Integer>>();    

	public static void main(String[] args) 
	{	
		String currentLineString = "", index = "";
		int index1, index2, counter = 0;;
		try{
			BufferedReader br = new BufferedReader(new FileReader("Infection/infection.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Infection/infection_links_3of3.txt"))); // 17298 links
			bw.write("410\n");

			while ((currentLineString = br.readLine()) != null) {
				counter++;
				//if (counter==5000)
				//	break;
				
				//if (counter<5000)
				//	continue;
				//if (counter==10000)
				//	break;

				if (counter<10000)
					continue;
				
				ArrayList<Integer> list1 = new ArrayList<Integer>();
				ArrayList<Integer> list2 = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				index1 = Integer.parseInt(st.nextToken())-1;
				index2 = Integer.parseInt(st.nextToken())-1;

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


			for (int i=0; i<410; i++){
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

			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
