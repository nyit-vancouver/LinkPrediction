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
public class DBLPMin5paper {


	public static void main(String[] args) 
	{	 
		Map<String, ArrayList<String>> source_dest_label_to_features = new HashMap<String, ArrayList<String>>();

		try{
			BufferedReader all_labels_file = new BufferedReader(new FileReader("shuffledlabels_1996_2002_newLinkIn_2003_2009.txt"));
			BufferedReader apvpa_all = new BufferedReader(new FileReader("APVPA_1996_2002.txt"));
			BufferedReader apapa_all = new BufferedReader(new FileReader("APAPA_1996_2002.txt"));

			BufferedReader min5paper_labels_file = new BufferedReader(new FileReader("shuffledlabels_1996_2002_newLinkIn_2003_2009_min5paper.txt"));
			BufferedWriter apvpa_min5paper = new BufferedWriter(new FileWriter(new File("APVPA_1996_2002_min5paper.txt")));
			BufferedWriter apapa_min5paper = new BufferedWriter(new FileWriter(new File("APAPA_1996_2002_min5paper.txt")));


			String apvpa, apapa, source_dest_label;

			for (int i=0; i<5720663; i++){
				ArrayList<String> features = new ArrayList<String>();

				source_dest_label = all_labels_file.readLine();
				apvpa = apvpa_all.readLine();
				apapa = apapa_all.readLine();

				if (apvpa.contains("-1") || apapa.contains("-1"))
					continue;

				features.add(apvpa);
				features.add(apapa);
				source_dest_label_to_features.put(source_dest_label, features);

			}

			// find features value in the map for each pair in the min5paper file
			while ((source_dest_label = min5paper_labels_file.readLine()) != null) {
				ArrayList<String> features = source_dest_label_to_features.get(source_dest_label);
				//System.out.println(source_dest_label + "=>" +features);
				if (features!=null){
					apvpa_min5paper.write(features.get(0) + "\n");
					apapa_min5paper.write(features.get(1) + "\n");
				}else{
					apvpa_min5paper.write("-1\n");
					apapa_min5paper.write("-1\n");
				}
			}


			all_labels_file.close();
			apvpa_all.close();
			apapa_all.close();
			min5paper_labels_file.close();
			apvpa_min5paper.close();
			apapa_min5paper.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
