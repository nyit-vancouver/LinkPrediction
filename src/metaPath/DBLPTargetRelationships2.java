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
 * 		papers in 2 intervals: before 2009 and after 2009
 * 
 * 		This is to create coauthorship.txt to be used as input for the temporal inference work (TKDE16)
 *		file format example
 *			410 
 *			0,5:1,1:4,1:5,1:49,1:50,1
 *			1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1
 * 
 * @author aminmf
 */
public class DBLPTargetRelationships2 {

	private static TreeMap<Integer, ArrayList<Integer>> author_papers_map = new TreeMap<Integer, ArrayList<Integer>>();    
	private static TreeMap<Integer, ArrayList<Integer>> paper_authors_map = new TreeMap<Integer, ArrayList<Integer>>();    

	private static TreeMap<Integer, Integer> paper_year_map = new TreeMap<Integer, Integer>();    
	private static TreeMap<Integer, TreeSet<Integer>> coauthors = new TreeMap<Integer, TreeSet<Integer>>();    

	public static void main(String[] args) 
	{	 
		String currentLineString;
		int paperIndex, authorIndex;
		int year;
		int fromYear = 1930, toYear = 1996;

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			BufferedReader br_year = new BufferedReader(new FileReader("paper_newindex_year.txt"));
			//BufferedWriter bw = new BufferedWriter(new FileWriter(new File("coauthorship_int1_1936_2009.txt")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("coauthorship_1of7_1996_1998.txt")));

			while ((currentLineString = br_year.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = Integer.parseInt(st.nextToken());
				year = Integer.parseInt(st.nextToken());
				paper_year_map.put(paperIndex, year);
			}


			while ((currentLineString = br.readLine()) != null) {
				ArrayList<Integer> papersList = new ArrayList<Integer>();
				ArrayList<Integer> authorsList = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = Integer.parseInt(st.nextToken());
				authorIndex = Integer.parseInt(st.nextToken());

				year = paper_year_map.get(paperIndex);
				if (year >= fromYear && year <= toYear){

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
			}

			// file format example for the temporal inference paper (TKDE16)
			//410 
			//0,5:1,1:4,1:5,1:49,1:50,1
			//1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1

			// add number of authors in the first line

			//bw.write(author_papers_map.size() + "\n");
			bw.write("1752443\n");

			for (int i=0; i<1752443; i++){
				TreeSet<Integer> coauthorsList = new TreeSet<Integer>();

				if (author_papers_map.containsKey(i)){

					for (Integer p: author_papers_map.get(i)){
						for (Integer a: paper_authors_map.get(p)){
							if (a != i) 
								coauthorsList.add(a);
						}
					}

					coauthors.put(i, coauthorsList);

					bw.write(i + "," + coauthorsList.size());
					for (Integer c: coauthorsList){
						bw.write(":" + c + ",1");
					}
					bw.write("\n");

				}else{
					bw.write(i + ",0\n");
				}
			}

			
			/*TreeSet<Integer> twoHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> threeHopCoauthors = new TreeSet<Integer>();
			for (int i=0; i<100; i++){  // 1752443
				TreeSet<Integer> coauthorsList = coauthors.get(i);
				for (Integer j:coauthorsList){
					TreeSet<Integer> cocoauthorsList = coauthors.get(j);
					for (Integer k:cocoauthorsList){
						threeHopCoauthors.addAll(coauthors.get(k));
					}					
					twoHopCoauthors.addAll(coauthors.get(j));
				}
				threeHopCoauthors.removeAll(twoHopCoauthors); // remove two hop coauthors

				twoHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors
				twoHopCoauthors.remove(i); // remove author himself
				
				//System.out.println("1Hop(" + i + ")=" + coauthorsList);
				//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
				//System.out.println("Size of 1Hop(" + i + ")=" + coauthorsList.size());
				System.out.println("Size of 2Hop(" + i + ")=" + twoHopCoauthors.size() + "\tSize of 3Hop(" + i + ")=" + threeHopCoauthors.size());
				
				twoHopCoauthors.clear();
				threeHopCoauthors.clear();
				
			}*/



			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
