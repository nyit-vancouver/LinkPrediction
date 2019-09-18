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
public class DBLPResultsEvaluation2 {


	public static void main(String[] args) 
	{	 
		String currentLineString, currentLineString2;


		try{
			//BufferedReader pred1file = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/dblp/predict1_1999_2001_min5paper.txt"));
			//BufferedWriter bw = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/dblp/dblp_7int_MetaDynaMix_PredictionFor_1999_2001_min5paper.txt")));
			BufferedReader pred1file = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/k20Predict.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/k20predction.txt")));
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
