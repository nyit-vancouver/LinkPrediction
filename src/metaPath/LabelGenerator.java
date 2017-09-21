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

	
	// arguments : minPublication, fromYear1, toYear1, fromYear2, toYear1, output file name
	public static void main(String[] args) 
	{	 
		String currentLineString;
		int paperIndex, authorIndex;
		int year;
		int fromYear1 = 1996, toYear1 = 2002;  // these are to set intervals for the first chunk
		int fromYear2 = 2003, toYear2 = 2009;  // these are to set intervals for the second chunk (to find new links for +1 labels)
		
		int minPublication = 1; // (default should be 1) if an author has less than this min papers, will not be considered in the dataset

		/*minPublication = Integer.parseInt(args[0]);
		fromYear1 = Integer.parseInt(args[1]);
		toYear1 = Integer.parseInt(args[2]);  
		fromYear2 = Integer.parseInt(args[3]);
	    toYear2 = Integer.parseInt(args[4]);  
	    String lableFileName = args[5];
		*/
	    
		minPublication = 5;
		fromYear1 = 2002;
		toYear1 = 2004;  
		fromYear2 = 2005;
	    toYear2 = 2007;  // 5-7 8-10 11-13 14-16
	    String lableFileName = "labels_2002_2004_newLinkIn_2005_2007_min5paper.txt";//_min5paper

	    
		/*
		int fromYear1 = 2003, toYear1 = 2009;  // these are to set intervals for the first chunk
		int fromYear2 = 2010, toYear2 = 2016;  // these are to set intervals for the second chunk (to find new links for +1 labels)
		*/

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			BufferedReader br_year = new BufferedReader(new FileReader("paper_newindex_year.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(lableFileName)));

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


			for (int i=0; i<1752443; i++){
				TreeSet<Integer> coauthorsList = new TreeSet<Integer>();
				if (author_papers_map1.containsKey(i)){
					// disregard author who has less than min papers
					if (author_papers_map1.get(i).size() < minPublication)
						continue;
					for (Integer p: author_papers_map1.get(i)){
						for (Integer a: paper_authors_map1.get(p)){
							if (a != i) 
								coauthorsList.add(a);
						}
					}
					coauthors1.put(i, coauthorsList);
				}else{
					//System.out.println("Author " + i + " has no publication at time1!.");
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
					//System.out.println("coauthors1.get(" + i + "): " + firstIntervalCoAuthors);
					
					
					if (coauthors2.containsKey(i)){
						secondIntervalCoAuthors = coauthors2.get(i);
						//System.out.println("coauthors2.get(" + i + "): " + secondIntervalCoAuthors);
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
					
				}else{
					//firstIntervalCoAuthors.clear();
					//firstIntervalCoAuthors.add(-1);
					newConnections.add(0);
				}


			}



			TreeSet<Integer> twoHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> threeHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> cocoauthorsList = new TreeSet<Integer>();
			for (int i=0; i<1752443; i++){  // 1752443
			//for (int i=0; i<50; i++){  // 1752443

				if (coauthors1.containsKey(i)){
					TreeSet<Integer> coauthorsList = coauthors1.get(i);
					for (Integer j:coauthorsList){
						if (coauthors1.containsKey(j)){
							cocoauthorsList = coauthors1.get(j);
							twoHopCoauthors.addAll(coauthors1.get(j));
							for (Integer k:cocoauthorsList){
								if (coauthors1.containsKey(k))
									threeHopCoauthors.addAll(coauthors1.get(k));
							}					
						}
					}
					// finally decide to mergethem all!
					threeHopCoauthors.addAll(twoHopCoauthors);
					
					threeHopCoauthors.remove(i); // remove author himself
					threeHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors

					//System.out.println("1Hop(" + i + ")=" + coauthorsList);
					//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
					//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
					//System.out.println("newConnections.get(i): " + newConnections.get(i));
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
					}else{
						// add all 3hops
						for (Integer c: threeHopCoauthors){
							//System.out.println(i + ",c=" + c + ":0");
							bw.write(i + "," + c + ":0\n");
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
