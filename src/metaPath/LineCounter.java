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


public class LineCounter {


	public static void main(String[] args) 
	{	 
		String fileName = args[0];

		//String fileName = "labels_1996_1998_newLinkIn_1999_2001_min5paper.txt";

		try{
			BufferedReader file = new BufferedReader(new FileReader(fileName));

			String data;
			int count =0;
			while ((data = file.readLine()) != null) {
				count++;
			} 
			System.out.println(count);

			file.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
