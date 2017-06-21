package aminer;

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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

public class MetaPathCounter {

	private static String currentLineString, paperIndex = null, authorIndex=null, venueIndex=null;

	private static Map<String, List<PaperVenue>> author_papervenuelist_map = new HashMap<String, List<PaperVenue>>();    
	private static Map<String, List<PaperAuthors>> venue_paperauthorslist_map = new HashMap<String, List<PaperAuthors>>();    

	private static class PaperVenue implements Serializable {
		@Override
		public String toString() {
			return "PaperVenue [paper=" + paper + ", venue=" + venue + "]";
		}
		private String paper;
		private String venue;
		public PaperVenue(String paper, String venue) {
			this.paper = paper;
			this.venue = venue;
		}
		public String getPaper(){
			return paper;
		}
		public String getVenue(){
			return venue;
		}
	}

	private static class PaperAuthors implements Serializable {
		@Override
		public String toString() {
			return "PaperAuthors [paper=" + paper + ", authors=" + authors + "]";
		}
		private String paper;
		private ArrayList<String> authors;
		public PaperAuthors(String paper, ArrayList<String> authors) {
			this.paper = paper;
			this.authors = authors;
		}
		public String getPaper(){
			return paper;
		}
		public ArrayList<String> getAuthors(){
			return authors;
		}
		public void setAuthors(ArrayList<String> authors){
			this.authors = authors;
		}

	}


	private static BufferedReader brPaperAuthor;
	private static BufferedReader brPaperVenue;
	private static BufferedWriter bwHashMap;

	/**
	 * Given index of two authors, calculate number of different paths of type A-P-V-P-A (e.g. Jim-P1-KDD-P4-Tom)
	 * @param paperIndex of author 1: a
	 * @param paperIndex of author 2: b
	 * @return pathCount between them
	 * @throws IOException 
	 */
	public static int pathCount(String a, String b) throws IOException{
		int PathCount = 0;
		String v, i, j;

		long startTime = System.currentTimeMillis();

		// for author with index a1 for each paper index i 
		//   find venue v where i is published at
		//   for each paper index j that is published at v (j and i can be equal for instance for PC(i,i)
		//		if j has author index a2, then PathCount++

		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);
		for (PaperVenue pv : papervenuelist){
			i = pv.getPaper();
			v = pv.getVenue();
			System.out.println("v: " + v);
			List<PaperAuthors> paperauthorslist = venue_paperauthorslist_map.get(v);
			for (PaperAuthors pa: paperauthorslist){
				j = pa.getPaper();
				for (String author: pa.getAuthors())
					if (author.equals(b)){
						System.out.println("There is a path from " + a + " to " + b + ": " + i + "-" + v + "-" + j);
						PathCount++;
					}
			}
		}

		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);

		System.out.println("Done with calculating path count in " + duration/1000 + " seconds!");

		return PathCount;
	}

	public static void main(String[] args) throws ClassNotFoundException 
	{	 
		// -Xms1024m -Xmx6000m

		boolean readFromSavedGeneratedHashmaps = true;
		
		long startTime = System.currentTimeMillis();

		try{
			brPaperAuthor = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			brPaperVenue = new BufferedReader(new FileReader("paper_newindex_venue.txt"));

			if (readFromSavedGeneratedHashmaps==true){
				
				// <author, list of [paper, venue]>
				FileInputStream fileIn = new FileInputStream("author_papervenuelist_map.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				author_papervenuelist_map = (HashMap<String, List<PaperVenue>>) in.readObject();
				in.close();
				fileIn.close();

				// <venue, list of [paper, author]>
				FileInputStream fileIn1 = new FileInputStream("venue_paperauthorslist_map.ser");
				ObjectInputStream in1 = new ObjectInputStream(fileIn1);
				venue_paperauthorslist_map = (HashMap<String, List<PaperAuthors>>) in1.readObject();
				in1.close();
				fileIn1.close();

				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime);

				System.out.println("Done with reading maps in " + duration/1000 + " seconds!");
				
			}else{

				int[] paperVenue = new int[3177887]; // the index of this array correspond to the paper index and the value corresonf to venue index

				while ((currentLineString = brPaperVenue.readLine()) != null) {
					StringTokenizer st2 = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st2.nextToken();
					venueIndex = st2.nextToken();
					paperVenue[Integer.parseInt(paperIndex)] = Integer.parseInt(venueIndex);
				}

				while ((currentLineString = brPaperAuthor.readLine()) != null) {
					List<PaperVenue> paperVenueList = new ArrayList<PaperVenue>();
					List<PaperAuthors> paperAuthorsList = new ArrayList<PaperAuthors>();

					StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st.nextToken();
					authorIndex = st.nextToken();
					venueIndex = Integer.toString(paperVenue[Integer.parseInt(paperIndex)]);	

					// add to author_papervenuelist_map
					PaperVenue new_pv = new PaperVenue(paperIndex, venueIndex); 
					if (author_papervenuelist_map.containsKey(authorIndex)){
						paperVenueList = author_papervenuelist_map.get(authorIndex);
					}
					paperVenueList.add(new_pv);
					author_papervenuelist_map.put(authorIndex, paperVenueList);

					// add to venue_paperauthorslist_map
					ArrayList<String> authorsList = new ArrayList<String>();
					authorsList.add(authorIndex);
					PaperAuthors new_pa = new PaperAuthors(paperIndex, authorsList); 				
					boolean paperFoundInVenue = false;
					if (venue_paperauthorslist_map.containsKey(venueIndex)){
						paperAuthorsList = venue_paperauthorslist_map.get(venueIndex);
						for (PaperAuthors pa: paperAuthorsList){
							if (pa.getPaper().equals(paperIndex)){ // add author to list of authors for the existing paper
								authorsList = pa.getAuthors();
								authorsList.add(authorIndex);
								pa.setAuthors(authorsList);
								paperFoundInVenue = true;
								break;
							}
						}
						if (paperFoundInVenue == false){ // new paper and new author should be added
							paperAuthorsList.add(new_pa);
						}
					}else
						paperAuthorsList.add(new_pa);
					venue_paperauthorslist_map.put(venueIndex, paperAuthorsList);
				}

				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime);

				System.out.println("Done with creating maps in " + duration/1000 + " seconds!");

				/*File file = new File("author_papervenuelist_map.ser");
				ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
				output.writeObject(author_papervenuelist_map);
				output.flush();
				output.close();

				File file2 = new File("venue_paperauthorslist_map.ser");
				ObjectOutputStream output2 = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file2)));
				output2.writeObject(venue_paperauthorslist_map);
				output2.flush();
				output2.close();*/

				FileOutputStream fileOut = new FileOutputStream("author_papervenuelist_map.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(author_papervenuelist_map);
				out.close();
				fileOut.close();

				FileOutputStream fileOut1 = new FileOutputStream("venue_paperauthorslist_map.ser");
				ObjectOutputStream out1 = new ObjectOutputStream(fileOut1);
				out1.writeObject(venue_paperauthorslist_map);
				out1.close();
				fileOut1.close();

				endTime = System.currentTimeMillis();
				duration = (endTime - startTime);

				System.out.println("Done with saving maps in " + duration/1000 + " seconds!");
			}



			pathCount("41527","29176");

			brPaperAuthor.close();
			brPaperVenue.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
