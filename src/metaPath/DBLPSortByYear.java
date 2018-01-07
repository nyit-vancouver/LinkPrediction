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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class DBLPSortByYear {
	
	public static class YearPaperInex {
        public String year;
        public String index;
        public YearPaperInex(String y, String i) {
        	year = y;
            index = i;
        }
    }
	
    public static void sortByYear(YearPaperInex[] paper_year_array) {
        Arrays.sort(paper_year_array, new Comparator<YearPaperInex>() {
            public int compare(YearPaperInex p1, YearPaperInex p2) {
                return p1.year.compareTo(p2.year);
            }
        });
    }
	
	public static void main(String[] args) 
	{	 
		String currentLineString, paperIndex = null, year=null;

		ArrayList<YearPaperInex> paper_year = new ArrayList<YearPaperInex>();

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_year.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("paper_year_sorted.txt")));

			while ((currentLineString = br.readLine()) != null) {
				//System.out.println (currentLineString);
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				year = st.nextToken();
				paper_year.add(new YearPaperInex(year, paperIndex));
			}

			
	    	YearPaperInex[] paper_year_array = new YearPaperInex[paper_year.size()];
	    	paper_year_array = paper_year.toArray(paper_year_array);
	    	
			sortByYear(paper_year_array);
			int count = 0;
	        for (YearPaperInex p : paper_year_array) {
	        	count++;
	        	if (count%(3272992/2)==0)
	        		System.out.println(p.year);
	        	bw.write(p.year + "\t" + p.index + "\n");
	        }


			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
