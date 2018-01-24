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
 * 		This is to generate dataset for logistic regression using labels and features in different text files.
 * 
 * @author aminmf
 */
public class IMDBNewDataSetGenerator {


	public static void main(String[] args) 
	{	 
		String currentLineString;


		try{
			// taget relation is user-movie (UM), meta paths are: UMUM, UMGM, UMDM, UMAM
			BufferedReader UMUM = new BufferedReader(new FileReader("IMDB/3intervals/UMUM_2of3.txt")); // same user
			BufferedReader UMGM = new BufferedReader(new FileReader("IMDB/3intervals/UMGM_2of3.txt")); // same genre
			//BufferedReader UMDM = new BufferedReader(new FileReader("IMDB/3intervals/UMDM_1of3.txt")); // same director
			//BufferedReader UMAM = new BufferedReader(new FileReader("IMDB/3intervals/UMAM_1of3.txt")); // same actors
			BufferedReader predfile = new BufferedReader(new FileReader("IMDB/3intervals/temporalPredictionFor_3of3.txt")); // for next time interval
			BufferedReader lfile = new BufferedReader(new FileReader("IMDB/3intervals/labels_for_2of3_newMovies_in_3of3.txt"));
			BufferedWriter dataset1 = new BufferedWriter(new FileWriter(new File("IMDB/3intervals/newtraining1_for_2of3.txt"))); // for this interval
			BufferedWriter dataset2 = new BufferedWriter(new FileWriter(new File("IMDB/3intervals/newtraining2_for_2of3.txt")));
			BufferedWriter dataset3 = new BufferedWriter(new FileWriter(new File("IMDB/3intervals/newtraining3_for_2of3.txt")));

	
			dataset1.write("f0,f1,f4,label\n");
			dataset2.write("f0,f1,label\n");
			dataset3.write("f4,label\n");

			
			String f0="0", f1="0", f2="0", f3="0", f4, label;

			while ((currentLineString = lfile.readLine()) != null) {
			//for (int i=0; i<10; i++){
				//currentLineString = lfile.readLine();

				label = currentLineString.substring(currentLineString.indexOf(":")+1);

				f0 = UMUM.readLine();
				f1 = UMGM.readLine();
				//f2 = UMDM.readLine();
				//f3 = UMAM.readLine();
				f4 = predfile.readLine();


				//System.out.println("f0: " + f0);
				//System.out.println("f1: " + f1);
				
				if (f0.contains("-1") || f1.contains("-1") || f2.contains("-1") || f3.contains("-1"))
					continue;
				/*if (f0.contains("-1"))
					f0 = "0.0";
				if (f1.contains("-1"))
					f1 = "0.0";
				if (f2.contains("-1"))
					f2 = "0.0";
				*/


				//dataset1.write(f1 + "\t" + f2 + "\t" + f3 + "\t" + label +"\n");
				//dataset2.write(f1 + "\t" + f2 + "\t" + label +"\n");
				//dataset3.write(f3 + "\t" + label +"\n");


				// For SVM and regression library format
				if (label.equals("1"))
					label = "1";
				else
					label = "0";

				//+1 1:0 2:0.661 3:0.033 4:0.500 5:0.199 6:0.006 7:0.000 8:0.015 9:0.100

				//dataset1.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + " 4:" + f3 + " 5:" + f4 +"\n");
				//dataset2.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + " 4:" + f3 + "\n");
				dataset1.write(f0 + "," + f1 + "," + f4 + "," + label +"\n");
				dataset2.write(f0 + "," + f1 + "," + label+ "\n");
				dataset3.write(f4 + "," + label + "\n");

			}


			predfile.close();
			lfile.close();
			dataset1.close();
			dataset2.close();
			dataset3.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
