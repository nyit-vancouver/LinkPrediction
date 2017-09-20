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


public class shuffeleDataSet {


	public static void main(String[] args) 
	{	 
		String currentLineString;


		try{
			BufferedReader dataset = new BufferedReader(new FileReader("svmdataset1.txt"));
			BufferedWriter newdataset = new BufferedWriter(new FileWriter(new File("shuffledsvmdataset1.txt")));

			List<String> instances = new ArrayList<String>();

			for (int i=0; i<5408146; i++){
				String data = dataset.readLine();
				instances.add(data);
			}

			Collections.shuffle(instances);

			for (int i=0; i<5408146; i++){
				newdataset.write(instances.get(i) + "\n");
			}

			dataset.close();
			newdataset.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
