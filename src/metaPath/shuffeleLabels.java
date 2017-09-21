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


public class shuffeleLabels {


	public static void main(String[] args) 
	{	 
		String currentLineString;

	    String lableFileName = args[0];
	    String shuffledlableFileName = args[1];


		try{
			//BufferedReader dataset = new BufferedReader(new FileReader("labels_1996_2002_newLinkIn_2003_2009.txt"));
			//BufferedWriter newdataset = new BufferedWriter(new FileWriter(new File("shuffledlabels_1996_2002_newLinkIn_2003_2009.txt")));
			BufferedReader dataset = new BufferedReader(new FileReader(lableFileName));
			BufferedWriter newdataset = new BufferedWriter(new FileWriter(new File(shuffledlableFileName)));

			List<String> instances = new ArrayList<String>();
			String data;
			int count = 0;
			
	         while ((data = dataset.readLine()) != null) {
					instances.add(data);
					count++;
	          } 

			Collections.shuffle(instances);

			for (int i=0; i<count; i++){
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
