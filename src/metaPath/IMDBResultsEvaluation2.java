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
 * 
 * @author aminmf
 */
public class IMDBResultsEvaluation2 {


	public static void main(String[] args) 
	{	 
		String currentLineString, currentLineString2;


		try{
			BufferedReader pred1file = new BufferedReader(new FileReader("IMDB/7intervals/1predict3_imdb_for_1of7.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("IMDB/7intervals/7int_LRHLP_PredictionFor_1of7.txt")));
			int pred1;
			// ignore the first line for prediction files
			pred1file.readLine();

			while ((currentLineString2 = pred1file.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(currentLineString2," ");  
				st.nextToken();
				float pred = Float.parseFloat(st.nextToken());
				bw.write(pred + "\n");
			}

		
		
			bw.close();
			pred1file.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 
		
	}


}
