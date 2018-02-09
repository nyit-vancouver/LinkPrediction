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
public class IMDBResultsEvaluation {


	public static void main(String[] args) 
	{	 
		String currentLineString;

		// n_cc # correctly classified by both classifiers
		// n_ci # correctly classified by c1 but misclassified by misclassified by c2
		// n_ic # misclassified by by c1 but correctly but correctly classified by c2
		// n_ii # misclassified by both classifiers

		int n_cc_1vs2=0, n_ci_1vs2=0, n_ic_1vs2=0, n_ii_1vs2=0;
		int n_cc_1vs3=0, n_ci_1vs3=0, n_ic_1vs3=0, n_ii_1vs3=0;
		int n_cc_1vs4=0, n_ci_1vs4=0, n_ic_1vs4=0, n_ii_1vs4=0;

		
		// True positives
		int TP1=0, TP2=0, TP3=0, TP4=0;  // TP1: regression with combined features, TP2: ASONAM, TP3: regression wit TKDE feature, TP4: TKDE 
		// False positives
		int FP1=0, FP2=0, FP3=0, FP4=0; 
		// False negatives
		int FN1=0, FN2=0, FN3=0, FN4=0; 
		// True negatives
		int TN1=0, TN2=0, TN3=0, TN4=0; 

		int TP_rand=0, FP_rand=0, FN_rand=0, TN_rand=0; 


		try{
			// Regression prediction with combined feature (Ours) 
			//BufferedReader pred1file = new BufferedReader(new FileReader("3IntervalsPrediction/predict1_2003_2009_min5paper.txt"));
			BufferedReader pred1file = new BufferedReader(new FileReader("IMDB/3intervals/predictmodel1_for_2of3.txt"));
			// Regression prediction with metapath feature (ASONAM) 
			//BufferedReader pred2file = new BufferedReader(new FileReader("3IntervalsPrediction/predict2_2003_2009_min5paper.txt"));
			BufferedReader pred2file = new BufferedReader(new FileReader("IMDB/3intervals/predictmodel2_for_2of3.txt"));
			// Regression prediction with TKDE feature 
			//BufferedReader pred3file = new BufferedReader(new FileReader("3IntervalsPrediction/predict3_2003_2009_min5paper.txt"));
			BufferedReader pred3file = new BufferedReader(new FileReader("IMDB/3intervals/predictmodel3_for_2of3.txt"));
			// TKDE predicted lables (0.5 cut-off) this file contain both true labels (t+1) and TKDE predicts (t+1) 
			//BufferedReader pred4file = new BufferedReader(new FileReader("3IntervalsPrediction/newtraining3_2003_2009_min5paper.txt"));
			BufferedReader pred4file = new BufferedReader(new FileReader("IMDB/3intervals/training3_for_2of3.txt"));


			double temporalPredict;
			int label, pred1, pred2, pred3, pred4;

			// ignore the first line for prediction files
			pred1file.readLine();
			pred2file.readLine();
			pred3file.readLine();
			
			Random rand = new Random(); 

			while ((currentLineString = pred4file.readLine()) != null) {
				int randValue = rand.nextInt(2); 
				
				label = (currentLineString.charAt(0) == '-') ? 0 : 1 ; 
				temporalPredict = Double.parseDouble(currentLineString.substring(currentLineString.indexOf(":")+1));
				pred4 = (temporalPredict>=0.5) ? 1 : 0;

				pred1 = (pred1file.readLine().charAt(0) == '-') ? 0 : 1 ;
				pred2 = (pred2file.readLine().charAt(0) == '-') ? 0 : 1 ;
				pred3 = (pred3file.readLine().charAt(0) == '-') ? 0 : 1 ;

				//System.out.println("label: " + label + " " + pred1 + " " + pred2 + " " + pred3 + " " + pred4);

				// n_cc_1vs2=0, n_ci_1vs2=0, n_ic_1vs2=0, n_ii_1vs2=0;
				if (label==1){
					if (pred1==1){
						TP1++;
						if (pred2==1)
							n_cc_1vs2++;
						else
							n_ci_1vs2++;
						if (pred3==1)
							n_cc_1vs3++;
						else
							n_ci_1vs3++;
						if (pred4==1)
							n_cc_1vs4++;
						else
							n_ci_1vs4++;
					}
					else{
						FN1++;
						if (pred2==0)
							n_ii_1vs2++;
						else
							n_ic_1vs2++;
						if (pred3==0)
							n_ii_1vs3++;
						else
							n_ic_1vs3++;
						if (pred4==0)
							n_ii_1vs4++;
						else
							n_ic_1vs4++;
					}
					if (pred2==1)
						TP2++;
					else
						FN2++;
					if (pred3==1)
						TP3++;
					else
						FN3++;
					if (pred4==1)
						TP4++;
					else
						FN4++;
					if (randValue==1)
						TP_rand++;
					else
						FN_rand++;
				}else{
					if (pred1==1){
						FP1++;
						if (pred2==1)
							n_ii_1vs2++;
						else
							n_ic_1vs2++;
						if (pred3==1)
							n_ii_1vs3++;
						else
							n_ic_1vs3++;
						if (pred4==1)
							n_ii_1vs4++;
						else
							n_ic_1vs4++;
					}
					else{
						TN1++;
						if (pred2==0)
							n_cc_1vs2++;
						else
							n_ci_1vs2++;
						if (pred3==0)
							n_cc_1vs3++;
						else
							n_ci_1vs3++;
						if (pred4==0)
							n_cc_1vs4++;
						else
							n_ci_1vs4++;
					}
					if (pred2==1)
						FP2++;
					else
						TN2++;
					if (pred3==1)
						FP3++;
					else
						TN3++;
					if (pred4==1)
						FP4++;
					else
						TN4++;
					if (randValue==1)
						FP_rand++;
					else
						TN_rand++;
				}
			}

			System.out.println("TP1: " + TP1 + " - FP1: " + FP1 + " - FN1: " + FN1 + " - TN1: " + TN1);
			System.out.println("TP2: " + TP2 + " - FP2: " + FP2 + " - FN2: " + FN2 + " - TN2: " + TN2);
			System.out.println("TP3: " + TP3 + " - FP3: " + FP3 + " - FN3: " + FN3 + " - TN3: " + TN3);
			System.out.println("TP4: " + TP4 + " - FP4: " + FP4 + " - FN4: " + FN4 + " - TN4: " + TN4);
			System.out.println("TP_rand: " + TP_rand + " - FP4_rand: " + FP_rand + " - FN_rand: " + FN_rand + " - TN_rand: " + TN_rand);

			System.out.println("n_cc_1vs2: " + n_cc_1vs2 + " - n_ci_1vs2: " + n_ci_1vs2 + " - n_ic_1vs2: " + n_ic_1vs2 + " - n_ii_1vs2: " + n_ii_1vs2);
			System.out.println("n_cc_1vs3: " + n_cc_1vs3 + " - n_ci_1vs3: " + n_ci_1vs3 + " - n_ic_1vs3: " + n_ic_1vs3 + " - n_ii_1vs3: " + n_ii_1vs3);
			System.out.println("n_cc_1vs4: " + n_cc_1vs4 + " - n_ci_1vs4: " + n_ci_1vs4 + " - n_ic_1vs4: " + n_ic_1vs4 + " - n_ii_1vs4: " + n_ii_1vs4);



		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
