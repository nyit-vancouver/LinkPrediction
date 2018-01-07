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

import logRegression.DataSet;
import logRegression.Instance;


public class DBLPMergeLabels {


	public static void main(String[] args) 
	{	 
		//String lableFileName = args[0];
		//String shuffledlableFileName = args[1];

		int numPosLabels = 0;//Integer.parseInt(args[0]);//153110;
		int numNegLabels = 0;//Integer.parseInt(args[2]);//987620;
		String posLableFileName = args[0]+"_posLabels.txt";//"1996_1998_newLinkIn_1999_2001_min5paper_posLabels.txt";		
		String negLableFileName = args[0]+"_negLabels.txt";//"1996_1998_newLinkIn_1999_2001_min5paper_negLabels.txt";
		//System.out.println(step);
		String mergedLablesFileName = args[1];//"labels_1996_1998_newLinkIn_1999_2001_min5paper.txt";


		try{
			BufferedReader posLabelsFile = new BufferedReader(new FileReader(posLableFileName));
			BufferedReader negLabelsFile = new BufferedReader(new FileReader(negLableFileName));

			String data;
			while ((data = posLabelsFile.readLine()) != null) {
				numPosLabels++;
			}
			while ((data = negLabelsFile.readLine()) != null) {
				numNegLabels++;
			}

			posLabelsFile.close();
			negLabelsFile.close();
			System.out.println(numPosLabels);
			System.out.println(numNegLabels);

		}catch (IOException e){
			e.printStackTrace();
		} 

		try{
			int step = numNegLabels/numPosLabels;

			BufferedReader posLabelsFile = new BufferedReader(new FileReader(posLableFileName));
			BufferedReader negLabelsFile = new BufferedReader(new FileReader(negLableFileName));
			BufferedWriter mergedLabelsFile = new BufferedWriter(new FileWriter(new File(mergedLablesFileName)));

			List<String> dataPoint = new ArrayList<String>();
			String data;

			while ((data = posLabelsFile.readLine()) != null) {
				dataPoint.add(data);
			} 

			// read from negative samples with step size proportional to neg candidate samples
			int count=0;
			while ((data = negLabelsFile.readLine()) != null) {
				count++;
				if (count%step==0)
					dataPoint.add(data);
			} 

			int size = dataPoint.size();

			Collections.shuffle(dataPoint);

			for (int i=0; i<size; i++){
				mergedLabelsFile.write(dataPoint.get(i) + "\n");
			}

			posLabelsFile.close();
			negLabelsFile.close();
			mergedLabelsFile.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
