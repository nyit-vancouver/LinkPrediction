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
public class DBLPRandomizer {


	public static void main(String[] args) 
	{	 
		String currentLineString, currentLineString2;


		try{
			BufferedReader pred1file = new BufferedReader(new FileReader("DBLP/3IntervalsPrediction/dblp_3int_MetaDynaMix_PredictionFor_2010_2016.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("DBLP/3IntervalsPrediction/rand_dblp_3int_MetaDynaMix_PredictionFor_2010_2016.txt")));
			float a=0f;
			Random rand = new Random();


			while ((currentLineString2 = pred1file.readLine()) != null) {
				a = Float.parseFloat(currentLineString2);

				if(rand.nextFloat()>0.9){
					if (a<0.5) a+=0.1;
					else a-=0.1;
				}

				bw.write(Float.toString(a) + "\n");
			}



			bw.close();
			pred1file.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
