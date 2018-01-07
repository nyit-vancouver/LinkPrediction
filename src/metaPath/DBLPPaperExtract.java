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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class DBLPPaperExtract {
	public static void main(String[] args) 
	{	 
		int i, p, venueIndex = 0, currentVenuewIndex = 0,authorIndex = 0;
		String currentLineString, paperIndex = null, citedByIndex = null, paperTitle = null, venue = null, year=null;

		ArrayList<String> citedby = new ArrayList<String>();
		ArrayList<Integer> authorIndexList = new ArrayList<Integer>();
		Map<String, Integer> venues = new TreeMap<String, Integer>();
		Map<String, Integer> authors = new TreeMap<String, Integer>();
		Map<String, Integer> wordCounts = new TreeMap<String, Integer>();

		try{
			BufferedReader br = new BufferedReader(new FileReader("dblp.txt"));

			BufferedWriter bwCitedBy = new BufferedWriter(new FileWriter(new File("citedby.txt")));
			BufferedWriter bwPaperIndex = new BufferedWriter(new FileWriter(new File("paper_index.txt")));
			BufferedWriter bwVenueIndex = new BufferedWriter(new FileWriter(new File("venue_index.txt")));
			BufferedWriter bwAuthorIndex = new BufferedWriter(new FileWriter(new File("author_index.txt")));
			BufferedWriter bwPaperVenue = new BufferedWriter(new FileWriter(new File("paper_venue.txt")));
			BufferedWriter bwPaperAuthor = new BufferedWriter(new FileWriter(new File("paper_author.txt")));
			BufferedWriter bwPaperYear = new BufferedWriter(new FileWriter(new File("paper_year.txt")));
			BufferedWriter bwTermIndex = new BufferedWriter(new FileWriter(new File("term_index.txt")));

			while ((currentLineString = br.readLine()) != null) {

				//System.out.println (currentLineString);

				if (currentLineString.equals("")){
					// writing cited-by tuples
					bwPaperIndex.write(paperIndex + "\t" + paperTitle + "\n");
					if (citedByIndex!=null){
						for (String s: citedby)
							bwCitedBy.write(paperIndex + "\t" + s + "\n");
						citedByIndex = null;
						citedby.clear();
					}

					for (Integer aIndex: authorIndexList){
						bwPaperAuthor.write(paperIndex + "\t" + aIndex + "\n");
					}
					authorIndexList.clear();

					continue;
				}

				if (currentLineString.toLowerCase().contains("#*")){
					//title
					paperTitle = currentLineString.substring(currentLineString.indexOf("#*")+2);
					//System.out.println(paperTitle);

					// term extraction from title
					StringTokenizer st = new StringTokenizer(paperTitle,", ");  
					while (st.hasMoreTokens()) {  
						String term = st.nextToken();
						// replace any punctuation char but apostrophes and dashes with a space
						term = term.replaceAll("[\\p{Punct}&&[^'-]]+", "");
						term = term.replaceAll("-", "");
						// replace most common English contractions
						term = term.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");
						List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", 
								"is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "us",
								"to", "was", "will", "with", "from", "form", "within", "when", "what", "where", "without", "vs", "very", "via", "use", "un",
								"up", "down", "left", "right", "under", "one", "two", "top", "over", "less", "more", "la", "its", "high", "low", "do", "de",
								"c", "all", "you", "me", "they");
						// get rid of empty ones, stop words, and numeric
						if (term.equals("") || stopWords.contains(term) || term.matches("[-+]?\\d*\\.?\\d+"))
							continue;
						if (!wordCounts.containsKey(term)) {
							wordCounts.put(term, 1);
						} else {
							wordCounts.put(term, wordCounts.get(term) + 1);
						}
					}  

					
				}
				if (currentLineString.toLowerCase().contains("#@")){
					//authors
					String authorList = currentLineString.substring(currentLineString.indexOf("#@")+2);
					StringTokenizer st = new StringTokenizer(authorList,",");  
					while (st.hasMoreTokens()) {  
						String author = st.nextToken();
						if (author.charAt(0)==' ') // removing space after ,
							author = author.substring(1);
						if (!authors.containsKey(author)){
							authorIndexList.add(authorIndex);
							authors.put(author, authorIndex++);
						}else{
							authorIndexList.add(authors.get(author));
						}

						//System.out.println(author);
					}  

				}
				if (currentLineString.toLowerCase().contains("#t")){
					//year
					year = currentLineString.substring(currentLineString.indexOf("#t")+2);
					//System.out.println(year);
				}
				if (currentLineString.toLowerCase().contains("#c")){
					//venue
					venue = currentLineString.substring(currentLineString.indexOf("#c")+2);
					// remove what is between parentheses, spaces, #, and string after :
					venue = venue.replaceAll("\\(.*\\)", "");
					venue = venue.replaceAll("\"", "");
					venue = venue.replaceAll(" ", "");
					venue = venue.replaceAll("#", "");
					int pos = venue.indexOf(":");
					if (pos>=0)
						venue = venue.substring(0,venue.indexOf(":"));
					pos = venue.indexOf("*");
					if (pos==0)
						venue = venue.substring(1);
					
					pos = venue.lastIndexOf(".");
					if (pos==venue.length()-1)
						venue = venue.substring(0,venue.indexOf("."));
					
					if (!venues.containsKey(venue)){
						currentVenuewIndex = venueIndex;
						venues.put(venue, venueIndex++);
					}
					else
						currentVenuewIndex = venues.get(venue);
 
					//System.out.println(venue);
				}
				if (currentLineString.toLowerCase().contains("#index")){
					//index
					paperIndex = currentLineString.substring(currentLineString.indexOf("#index")+6);  
					//System.out.println(paperIndex);
					bwPaperVenue.write(paperIndex + "\t" + currentVenuewIndex + "\n");
					bwPaperYear.write(paperIndex + "\t" + year + "\n");
				}
				if (currentLineString.toLowerCase().contains("#%")){
					//citation index
					citedByIndex = currentLineString.substring(currentLineString.indexOf("#%")+2);  
					citedby.add(citedByIndex);
					//System.out.println(citedByIndex);
				}


			}

			for (String v : venues.keySet()) {
				int count = venues.get(v);
				//System.out.println(count + "\t" + v);
				bwVenueIndex.write(count + "\t" + v + "\n");
			}

			for (String a : authors.keySet()) {
				int count = authors.get(a);
				//System.out.println(count + "\t" + a);
				bwAuthorIndex.write(count + "\t" + a + "\n");
			}

			
			for (String word : wordCounts.keySet()) {
				int count = wordCounts.get(word);
				if (count >= 50)
					bwTermIndex.write(count + "\t" + word + "\n");
			}


			br.close();
			bwCitedBy.close();
			bwPaperIndex.close();
			bwVenueIndex.close();
			bwAuthorIndex.close();
			bwPaperVenue.close();
			bwPaperAuthor.close();
			bwPaperYear.close();
			bwTermIndex.close();
			
		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
