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
public class DBLPNewDataSetGenerator {


	public static void main(String[] args) 
	{	 
		String currentLineString;


		try{
			BufferedReader apvpa = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/APVPA_1996_1998_min5paper.txt"));
			BufferedReader apapa = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/APAPA_1996_1998_min5paper.txt"));
			BufferedReader apppa = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/APPPA_1996_1998_min5paper.txt"));
			BufferedReader predfile = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/temporalPredictionFor_1999_2001_min5paper.txt"));
			BufferedReader lfile = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/labels_1996_1998_newLinkIn_1999_2001_min5paper.txt"));
			BufferedWriter dataset1 = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/newtraining1_1of7.txt")));
			BufferedWriter dataset2 = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/newtraining2_1of7.txt")));
			BufferedWriter dataset3 = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/newtraining3_1of7.txt")));

			dataset1.write("f0,f1,f2,f4,label\n");
			dataset2.write("f0,f1,f2,label\n");
			dataset3.write("f4,label\n");
			
			String f0, f1, f2, f3, label;

			//for (int i=0; i<8645734; i++){
			while ((currentLineString = lfile.readLine()) != null) {
			//for (int i=0; i<2888731; i++){

				//currentLineString = lfile.readLine();
				label = currentLineString.substring(currentLineString.indexOf(":")+1);

				f0 = apppa.readLine();
				f1 = apvpa.readLine();
				f2 = apapa.readLine();
				f3 = predfile.readLine();


				if (f0.contains("-1") || f1.contains("-1") || f2.contains("-1"))
					continue;
				if (f0.contains("-1"))
					f0 = "0.0";
				if (f1.contains("-1"))
					f1 = "0.0";
				if (f2.contains("-1"))
					f2 = "0.0";


				//dataset1.write(f1 + "\t" + f2 + "\t" + f3 + "\t" + label +"\n");
				//dataset2.write(f1 + "\t" + f2 + "\t" + label +"\n");
				//dataset3.write(f3 + "\t" + label +"\n");



				// For SVM setting
				if (label.equals("1"))
					label = "1";
				else
					label = "0";

				//+1 1:0 2:0.661 3:0.037 4:0.500 5:0.199 6:0.006 7:0.000 8:0.015 9:0.100

				//dataset1.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + " 4:" + f3 + "\n");
				//dataset2.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + "\n");
				//dataset3.write(label + " 1:" + f3 + "\n");
				dataset1.write(f0 + "," + f1 + "," + f2 + "," + f3 + "," + label +"\n");
				dataset2.write(f0 + "," + f1 + "," + f2 + "," + label+ "\n");
				dataset3.write(f3	 + "," + label + "\n");

			}


			//apvpa.close();
			//apapa.close();
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
