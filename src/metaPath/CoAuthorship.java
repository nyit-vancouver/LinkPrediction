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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to create coauthorship.txt to be used as input for the temporal inference work (TKDE16)
 *		file format example
 *			410 
 *			0,5:1,1:4,1:5,1:49,1:50,1
 *			1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1
 * 
 * @author aminmf
 */
public class CoAuthorship {

	private static TreeMap<String, ArrayList<String>> author_papers_map = new TreeMap<String, ArrayList<String>>();    
	private static TreeMap<String, ArrayList<String>> paper_authors_map = new TreeMap<String, ArrayList<String>>();    

	public static void main(String[] args) 
	{	 
		String currentLineString, paperIndex = null, authorIndex=null;

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("coauthorship.txt")));

			while ((currentLineString = br.readLine()) != null) {
				ArrayList<String> papersList = new ArrayList<String>();
				ArrayList<String> authorsList = new ArrayList<String>();

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				authorIndex = st.nextToken();

				if (author_papers_map.containsKey(authorIndex)){
					papersList = author_papers_map.get(authorIndex);
				}
				papersList.add(paperIndex);
				author_papers_map.put(authorIndex, papersList);

				if (paper_authors_map.containsKey(paperIndex)){
					authorsList = paper_authors_map.get(paperIndex);
				}
				authorsList.add(authorIndex);
				paper_authors_map.put(paperIndex, authorsList);
			}


			// file format example for the temporal inference paper (TKDE16)
			//410 
			//0,5:1,1:4,1:5,1:49,1:50,1
			//1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1

			// add number of authors in the first line
			bw.write("1752443\n");

			for (int i=0; i<1752443; i++){
				TreeSet<String> coauthorsList = new TreeSet<String>();
				for (String p: author_papers_map.get(Integer.toString(i))){
					for (String a: paper_authors_map.get(p)){
						if (!a.equals(Integer.toString(i))) 
							coauthorsList.add(a);
					}
				}
				bw.write(i + "," + coauthorsList.size());
				for (String c: coauthorsList){
					bw.write(":" + c + ",1");
				}
				bw.write("\n");
				coauthorsList.clear();
			}

			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
