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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;


public class DBLPCoAuther {

	public static void main(String[] args) 
	{	
		String currentLineString = "", index = "";
		int year = 0, minYear =2017, maxYear = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader("dblp_coauthor.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("new_dblp_coauthor.txt")));

			while ((currentLineString = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString," ");  
				index = st.nextToken();
				bw.write(index + " ");
				index = st.nextToken();
				bw.write(index + " ");
				st.nextToken();
				year = (Integer.parseInt(st.nextToken())/31556926)+1970;
				if (year > maxYear) maxYear = year;
				if (year < minYear) minYear = year;
				bw.write(year + "\n");
			}
			
			System.out.println(minYear);
			System.out.println(maxYear);

			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
