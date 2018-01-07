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

public class DBLPMetaPath_APPPA {

	private static String currentLineString, paperIndex = null, authorIndex=null, citedByPaperIndex=null;

	private static Map<String, List<String>> author_paperslist_map = new HashMap<String, List<String>>();    
	private static Map<String, List<String>> paper_authorslist_map = new HashMap<String, List<String>>();    
	private static TreeMap<String, Integer> paper_year_map = new TreeMap<String, Integer>();    
	private static Map<String, List<String>> paper_citedbypapers_map = new HashMap<String, List<String>>();    
	private static Map<String, List<String>> paper_citedpapers_map = new HashMap<String, List<String>>();    

	private static int fromYear;
	private static int toYear;

	private static BufferedReader brPaperAuthor;
	private static BufferedReader brPaperCitedBy;
	private static BufferedReader brPaperYear;
	private static BufferedReader labels;

	/**
	 * Given index of two authors, calculate number of different meta-path of type A-P->P<-P-A (e.g. Jim-P1->P5<-P4-Tom) that is both citing a same paper
	 * @param authorIndex a
	 * @param authorIndex b
	 * @return pathCount between them
	 */
	public static int pathCount(String a, String b){    //a-i->c<-j-b
		int PathCount = 0, year;

		// for author with index a for each paper index i 
		//   find paper c where i cited it
		//   for each paper index j that cites c (j and i can be equal for instance for PC(i,i)
		//		if j has author index b, then PathCount++

		List<String> paperslist = author_paperslist_map.get(a);
		for (String i : paperslist){
			// ignore papers out of target interval
			year = paper_year_map.get(i);
			if (year < fromYear || year > toYear)
				continue;
			List<String> citedpaperslist = paper_citedpapers_map.get(i); 
			if (citedpaperslist==null)
				continue;
			for (String c : citedpaperslist){
				List<String> citedbypaperslist = paper_citedbypapers_map.get(c);
				for (String j : citedbypaperslist){
					// ignore papers out of target interval
					year = paper_year_map.get(j);
					if (year < fromYear || year > toYear)
						continue;
					List<String> authorslist = paper_authorslist_map.get(j);
					if (authorslist==null)
						continue;
					for (String author: authorslist)
						if (author.equals(b)){
							//System.out.println("There is a path from " + a + " to " + b + ": " + i + "-" + c + "-" + j);
							PathCount++;
						}
				}
			}
		}

		return PathCount;
	}



	/**
	 * Given index of two authors, calculate their pathSim [VLDB'11] of considering meta-path A-P->P<-P-A (e.g. Jim-P1->P5<-P4-Tom) that is both citing a same paper
	 * @param authorIndex a
	 * @param authorIndex b
	 * @return pathSim between them
	 */
	public static float pathSim(String a, String b){
		float ps = (float) 0.0;
		int a_a = pathCount(a,a); 
		int b_b = pathCount(b,b); 

		// ignore less productive
		if (a_a<0 || b_b<0)
			return -10;

		// check if the source or destination author actually published in that interval to avoid NaN for 0.0/0.0
		if (a_a + b_b == 0)
			return -1;

		ps = (float)2*pathCount(a,b) / (float)(a_a + b_b);
		if (ps > 1)
			System.out.println(pathCount(a,b) + " " + pathCount(a,a) + " " + pathCount(b,b));
		return ps;
	}



	public static void main(String[] args) throws ClassNotFoundException 
	{	 
		// -Xms1024m -Xmx6000m

		fromYear = 1996;
		toYear = 2002;
		String APPPA_file_name = "3IntervalsPrediction/APPPA_1996_2002.txt";
		String labels_file_name = "3IntervalsPrediction/labels_1996_2002_newLinkIn_2003_2009.txt";

		fromYear = Integer.parseInt(args[0]);
		toYear = Integer.parseInt(args[1]);
		APPPA_file_name = args[2];
		labels_file_name = args[3];
		

		long startTime = System.currentTimeMillis();

		try{
			brPaperAuthor = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			brPaperCitedBy = new BufferedReader(new FileReader("paper_newindex_citedby.txt"));
			brPaperYear = new BufferedReader(new FileReader("paper_newindex_year.txt"));

			while ((currentLineString = brPaperCitedBy.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				citedByPaperIndex = st.nextToken();

				List<String> citedbypapers = new ArrayList<String>();
				if (paper_citedbypapers_map.containsKey(paperIndex)){
					citedbypapers  = paper_citedbypapers_map.get(paperIndex);
				}
				citedbypapers.add(citedByPaperIndex);
				paper_citedbypapers_map.put(paperIndex, citedbypapers);				

				List<String> citedpapers = new ArrayList<String>();
				if (paper_citedpapers_map.containsKey(citedByPaperIndex)){
					citedpapers  = paper_citedpapers_map.get(citedByPaperIndex);
				}
				citedpapers.add(paperIndex);
				paper_citedpapers_map.put(citedByPaperIndex, citedpapers);				

			}

			int year;
			while ((currentLineString = brPaperYear.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				year = Integer.parseInt(st.nextToken());
				paper_year_map.put(paperIndex, year);
			}


			while ((currentLineString = brPaperAuthor.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				authorIndex = st.nextToken();

				List<String> papersList = new ArrayList<String>();
				if (author_paperslist_map.containsKey(authorIndex)){
					papersList = author_paperslist_map.get(authorIndex);
				}
				papersList.add(paperIndex);
				author_paperslist_map.put(authorIndex, papersList);

				List<String> authorsList = new ArrayList<String>();
				if (paper_authorslist_map.containsKey(paperIndex)){
					authorsList = paper_authorslist_map.get(paperIndex);
				}
				authorsList.add(authorIndex);
				paper_authorslist_map.put(paperIndex, authorsList);
			}

			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime);

			System.out.println("Done with creating maps in " + duration/1000 + " seconds!");


			
			try{

				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(APPPA_file_name)));

				labels = new BufferedReader(new FileReader(labels_file_name));
				// file format example
				//0,1:1
				//...
				//2,3:0
				int counter = 0;
				int from = 0, to = 0, sourceNode, destNode;
				while ((currentLineString = labels.readLine()) != null){
					counter++;

					from = 0;
					to = currentLineString.indexOf(",", from);
					sourceNode = Integer.parseInt(currentLineString.substring(from,to));
					//if (sourceNode>20)
					//	break;
					from = to+1;
					to = currentLineString.indexOf(":", from);
					destNode = Integer.parseInt(currentLineString.substring(from,to));
					bw.write(pathSim(Integer.toString(sourceNode), Integer.toString(destNode))+"\n");
					if (counter%100000==0){
						System.out.println(counter);
						endTime = System.currentTimeMillis();
						duration = (endTime - startTime);
						System.out.println("Time passed " + duration/1000 + " seconds!");
					}
					//System.out.println("-pathSim(" + sourceNode + "," + destNode+ ") = " + pathSim(Integer.toString(sourceNode), Integer.toString(destNode)) );
				}

				bw.close();

			}catch (IOException e) 
			{
				System.out.println(e);
				e.printStackTrace();
			} 

			brPaperAuthor.close();
			brPaperCitedBy.close();
			brPaperYear.close();
			brPaperAuthor.close();
			brPaperCitedBy.close();
			labels.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
