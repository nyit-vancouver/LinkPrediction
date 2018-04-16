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
public class DiggDataSetGenerator_friends {


	public static void main(String[] args) 
	{	 
		String currentLineString;
		String currentInterval = "2"; 	// e.g. is interval=2
		String intervals = "3"; 		// e.g. is intervals=7


		try{
			// taget relation is user-movie (UM), meta paths are: UMUM, UMGM, UMDM, UMAM
			BufferedReader USU = new BufferedReader(new FileReader("Digg/" + intervals + "intervals_friends/USU_" + currentInterval + "of" + intervals + ".txt")); // same user
			BufferedReader UUU = new BufferedReader(new FileReader("Digg/" + intervals + "intervals_friends/UUU_" + currentInterval + "of" + intervals + ".txt")); // same user
			//BufferedReader UMDM = new BufferedReader(new FileReader("Digg/3intervals_friends/UMDM_1of7.txt")); // same director
			//BufferedReader UMAM = new BufferedReader(new FileReader("Digg/3intervals_friends/UMAM_1of7.txt")); // same actors
			BufferedReader predfile = new BufferedReader(new FileReader("Digg/" + intervals + "intervals_friends/temporalPredictionFor_" + Integer.toString((Integer.parseInt(currentInterval)+1)) + "of" + intervals + ".txt")); // for next time interval
			BufferedReader lfile = new BufferedReader(new FileReader("Digg/" + intervals + "intervals_friends/labels_for_" + currentInterval + "of" + intervals + "_newLinks_in_" + Integer.toString((Integer.parseInt(currentInterval)+1)) + "of" + intervals + ".txt"));
			BufferedWriter dataset1 = new BufferedWriter(new FileWriter(new File("Digg/" + intervals + "intervals_friends/rand_training1_for_" + currentInterval + "of" + intervals + ".txt"))); // for this interval
			BufferedWriter dataset2 = new BufferedWriter(new FileWriter(new File("Digg/" + intervals + "intervals_friends/training2_for_" + currentInterval + "of" + intervals + ".txt")));
			BufferedWriter dataset3 = new BufferedWriter(new FileWriter(new File("Digg/" + intervals + "intervals_friends/training3_for_" + currentInterval + "of" + intervals + ".txt")));

			String f0="0", f1="0", f2="0", f3="0", f4, label;

			while ((currentLineString = lfile.readLine()) != null) {

				label = currentLineString.substring(currentLineString.indexOf(":")+1);

				f0 = USU.readLine();
				f1 = UUU.readLine();
				//f2 = UMDM.readLine();
				//f3 = UMAM.readLine();
				f4 = predfile.readLine();


				//System.out.println("f0: " + f0);
				//System.out.println("f1: " + f1);
				
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
					label = "+1";
				else
					label = "-1";

				//+1 1:0 2:0.661 3:0.033 4:0.500 5:0.199 6:0.006 7:0.000 8:0.015 9:0.100
				Random rand = new Random();
				
				//dataset1.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + " 4:" + f3 + " 5:" + f4 +"\n");
				//dataset2.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + " 4:" + f3 + "\n");
				dataset1.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + rand.nextFloat() +"\n");
				dataset2.write(label + " 1:" + f0 + " 2:" + f1 + "\n");
				dataset3.write(label + " 1:" + rand.nextFloat() + "\n");

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
