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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to generate list of pairs and labels based on new coauthorship relation
 *		Output file example
 *			6 
 *			0,1:1
 *			0,2:1
 *			1,0:1
 *			1,2:0
 *			2,0:1
 *			2,1:0
 * 
 * @author aminmf
 */
public class LabelGenerator {

	private static TreeMap<Integer, ArrayList<Integer>> author_papers_map1 = new TreeMap<Integer, ArrayList<Integer>>();    
	private static TreeMap<Integer, ArrayList<Integer>> paper_authors_map1 = new TreeMap<Integer, ArrayList<Integer>>();    

	private static TreeMap<Integer, ArrayList<Integer>> author_papers_map2 = new TreeMap<Integer, ArrayList<Integer>>();    
	private static TreeMap<Integer, ArrayList<Integer>> paper_authors_map2 = new TreeMap<Integer, ArrayList<Integer>>();    

	private static TreeMap<Integer, Integer> paper_year_map = new TreeMap<Integer, Integer>();

	private static TreeMap<Integer, TreeSet<Integer>> coauthors1 = new TreeMap<Integer, TreeSet<Integer>>();    // coauthors in the first interval
	private static TreeMap<Integer, TreeSet<Integer>> coauthors2 = new TreeMap<Integer, TreeSet<Integer>>();    // coauthors in the second interval

	private static ArrayList<Integer> newConnections = new ArrayList<Integer>();    // coauthors in the first interval

	public static void main(String[] args) 
	{	 
		String currentLineString;
		int paperIndex, authorIndex;
		int year;
		int fromYear1 = 1996, toYear1 = 2002;  // these are to set intervals for the first chunk
		int fromYear2 = 2003, toYear2 = 2009;  // these are to set intervals for the second chunk

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			BufferedReader br_year = new BufferedReader(new FileReader("paper_newindex_year.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("labels.txt")));

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
				if (year >= fromYear1 && year <= toYear1){

					if (author_papers_map1.containsKey(authorIndex)){
						papersList = author_papers_map1.get(authorIndex);
					}
					papersList.add(paperIndex);
					author_papers_map1.put(authorIndex, papersList);

					if (paper_authors_map1.containsKey(paperIndex)){
						authorsList = paper_authors_map1.get(paperIndex);
					}
					authorsList.add(authorIndex);
					paper_authors_map1.put(paperIndex, authorsList);

				}else if (year >= fromYear2 && year <= toYear2){

					if (author_papers_map2.containsKey(authorIndex)){
						papersList = author_papers_map2.get(authorIndex);
					}
					papersList.add(paperIndex);
					author_papers_map2.put(authorIndex, papersList);

					if (paper_authors_map2.containsKey(paperIndex)){
						authorsList = paper_authors_map2.get(paperIndex);
					}
					authorsList.add(authorIndex);
					paper_authors_map2.put(paperIndex, authorsList);

				}


			}

			// file format example for the temporal inference paper (TKDE16)
			//410 
			//0,5:1,1:4,1:5,1:49,1:50,1
			//1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1

			// add number of authors in the first line


			for (int i=0; i<1752443; i++){
				TreeSet<Integer> coauthorsList = new TreeSet<Integer>();
				if (author_papers_map1.containsKey(i)){
					for (Integer p: author_papers_map1.get(i)){
						for (Integer a: paper_authors_map1.get(p)){
							if (a != i) 
								coauthorsList.add(a);
						}
					}
					coauthors1.put(i, coauthorsList);
				}

				TreeSet<Integer> coauthorsList2 = new TreeSet<Integer>();
				if (author_papers_map2.containsKey(i)){
					for (Integer p: author_papers_map2.get(i)){
						for (Integer a: paper_authors_map2.get(p)){
							if (a != i) 
								coauthorsList2.add(a);
						}
					}
					coauthors2.put(i, coauthorsList2);
				}
			}

			TreeSet<Integer> firstIntervalCoAuthors = new TreeSet<Integer>();
			TreeSet<Integer> secondIntervalCoAuthors = new TreeSet<Integer>();

			for (int i=0; i<1752443; i++){

				//firstIntervalCoAuthors.clear();
				//secondIntervalCoAuthors.clear();

				if (coauthors1.containsKey(i)){
					firstIntervalCoAuthors = coauthors1.get(i);
					//System.out.println("coauthors1.get(i): " + firstIntervalCoAuthors);
				}else{
					//firstIntervalCoAuthors.clear();
					//firstIntervalCoAuthors.add(-1);
				}

				if (coauthors2.containsKey(i)){
					secondIntervalCoAuthors = coauthors2.get(i);
					//System.out.println("coauthors2.get(i): " + secondIntervalCoAuthors);
				}else{
					newConnections.add(0);
					//System.out.println("No new connections for " + i);
					continue;
				}

				int count = 0;
				for (Integer c: secondIntervalCoAuthors){
					if (!firstIntervalCoAuthors.contains(c)){
						count++;
						//System.out.println(c + " is a new connection for " + i);
						//System.out.println(i + "," +c + ":1");
						bw.write(i + "," + c + ":1\n");
					}
				}
				newConnections.add(count);


			}



			TreeSet<Integer> twoHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> threeHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> cocoauthorsList = new TreeSet<Integer>();
			for (int i=0; i<1752443; i++){  // 1752443

				if (coauthors1.containsKey(i)){
					TreeSet<Integer> coauthorsList = coauthors1.get(i);
					for (Integer j:coauthorsList){
						if (coauthors1.containsKey(j)){
							cocoauthorsList = coauthors1.get(j);
							twoHopCoauthors.addAll(coauthors1.get(j));
							for (Integer k:cocoauthorsList){
								threeHopCoauthors.addAll(coauthors1.get(k));
							}					
						}
					}
					threeHopCoauthors.remove(i); // remove author himself
					threeHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors

					//System.out.println(newConnections.get(i));
					// choose n = newConnections.get(i) random for 0 label from threeHopCoauthors
					if (threeHopCoauthors.size() >= newConnections.get(i)){
						ArrayList<Integer> candidatesForNegativeLabel = new ArrayList<Integer>();   
						for (Integer c: threeHopCoauthors){
							candidatesForNegativeLabel.add(c);
						}
						Collections.shuffle(candidatesForNegativeLabel);
						for (int j=0;j<newConnections.get(i);j++){
							//System.out.println(i + "," +candidatesForNegativeLabel.get(j)+ ":0");
							bw.write(i + "," +candidatesForNegativeLabel.get(j)+ ":0\n");
						}
					}

					threeHopCoauthors.removeAll(twoHopCoauthors); // remove two hop coauthors

					twoHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors
					twoHopCoauthors.remove(i); // remove author himself

					//System.out.println("1Hop(" + i + ")=" + coauthorsList);
					//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
					//System.out.println("Size of 1Hop(" + i + ")=" + coauthorsList.size());
					//System.out.println("Size of 2Hop(" + i + ")=" + twoHopCoauthors.size() + "\tSize of 3Hop(" + i + ")=" + threeHopCoauthors.size());

					/*if (i==2){
						System.out.println(newConnections.get(i));
						System.out.println("1Hop(" + i + ")=" + coauthorsList);
						System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
						System.out.println("3Hop(" + i + ")=" + threeHopCoauthors);
					}*/

					twoHopCoauthors.clear();
					threeHopCoauthors.clear();


				}

			}



			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
