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

import logRegression.Instance;

/**
 * 		This is to generate dataset for logistic regression using labels and features in different text files.
 * 
 * @author aminmf
 */
public class IMDB_TKDE_error {


	public static void main(String[] args) 
	{	 
        int flasecount = 0, lines = 0;
        double totalError = 0.0;
		double diff = 0.0;

		try{

	        Scanner scanner = new Scanner(new File("IMDB/3intervals/training3_for_1of3.txt"));
	        while(scanner.hasNextLine()) {
	        	lines++;
	            String line = scanner.nextLine();
	            String[] columns = line.split("\\t");
	            double probx  = Double.parseDouble(columns[0]);
	            int label = Integer.parseInt(columns[1]);

				diff = label - probx;
				diff*=diff;
				totalError+=diff;
	            
	            if ((probx >= 0.5 && label == 0) ||  (probx < 0.5 && label == 1))
	            {
	                flasecount++;
	            }

	            
	        }
		}catch (IOException e) 
		{
			e.printStackTrace();
		}
			
        System.out.println("total line:"+lines+", and incorrect count:"+flasecount);
		System.out.println("totalError: " + Math.sqrt(totalError));

	}


}
