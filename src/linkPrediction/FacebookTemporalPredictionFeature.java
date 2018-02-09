package linkPrediction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to generate ZZ^T (TKDE16) for each link in the labels.txt file
 * 
 * @author aminmf
 */

public class FacebookTemporalPredictionFeature {

	public static void main(String[] args) 
	{	
		// inputs
		int numberOfNodes = 63731, numOfDimensions = 20;

		double[][] z = new double[numberOfNodes][numOfDimensions];
		ArrayList<ArrayList<Integer>> neighbors = new ArrayList<ArrayList<Integer>>();

		ArrayList <ArrayList<FacebookTemporalPredictionFeature>> latentSpace = new ArrayList <ArrayList<FacebookTemporalPredictionFeature>>(); 
		String currentLineString, numOfNonZero=null;
		int latentPosIndex=0, nodeIndex = 0, neighborIndex = 0;
		double weight=0.0;

		String ZmatrixFileName = "Facebook/Zmatrix7.txt"; // previous Z e.g. for t=3 IMDB/Zmatrix/Zmatrix_2of3.txt
		String labelFileName = "Facebook/labels_for_6of7_newLinks_in_7of7.txt";
		String temporalPredictionFileName = "Facebook/newtemporalPredictionFor_7of7_local.txt";
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(ZmatrixFileName)); 
			// file format example (3 nodes and k=5 dimensions) node that others dimension values are zero
			//3
			//0,1:2,1.00000000
			//1,3:1,0.00000058:2,0.00000116:5,0.00000116
			//2,2:2,0.00000058:5,1.00000000
			currentLineString = br.readLine();
			int numOfNodes = Integer.parseInt(currentLineString);
			int from = 0, to = 0;
			for (int i=0; i < numOfNodes; i++){
				currentLineString = br.readLine();
				from = 0;
				to = currentLineString.indexOf(",", from);
				nodeIndex = Integer.parseInt(currentLineString.substring(from,to));
				//System.out.println("nodeIndex:" + nodeIndex);
				from = to+1;
				to = currentLineString.indexOf(":", from);
				numOfNonZero = currentLineString.substring(from,to);
				//System.out.println("numOfNonZero:" + numOfNonZero);
				for (int j=0; j < Integer.parseInt(numOfNonZero) ; j++){
					from = to+1;
					to = currentLineString.indexOf(",", from);
					latentPosIndex = Integer.parseInt(currentLineString.substring(from,to));

					from = to+1;
					to = from+10;
					weight = Double.parseDouble(currentLineString.substring(from, to));
					//System.out.println("latentPosIndex:" + latentPosIndex + ", weight:" + weight);
					//LatentPosWeigh LPW = new LatentPosWeigh(latentPosIndex, weight);
					//System.out.println(LPW);
					z[nodeIndex][latentPosIndex] = weight;

				}
			}

			System.out.println("Loaded matrix z in memory.");
			br.close();

			/*for (int i = 0; i < numberOfNodes; i++){
				for (int j = 0; j < numOfDimensions; j++)
					System.out.print(z[i][j] + "\t");
				System.out.println();
			}*/


		}catch (IOException e) 
		{
			e.printStackTrace();
		} 


		// reading original coauthorship file

		int from = 0, to = 0, sourceNode = 0, destNode = 0;
		try{
			BufferedReader labels = new BufferedReader(new FileReader(labelFileName));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(temporalPredictionFileName)));
			// file format example
			//0,1:1
			//...
			//2,3:0
			int counter = 0;
			while ((currentLineString = labels.readLine()) != null){
				counter++;
				from = 0;
				to = currentLineString.indexOf(",", from);
				sourceNode = Integer.parseInt(currentLineString.substring(from,to));
				//if (sourceNode>20)
				//	break;
				from = to+1;
				to = currentLineString.indexOf(":", from);
				destNode = Integer.parseInt(currentLineString.substring(from,to));

				if (counter%1000000==0)
					System.out.println(counter);

				Double predictionProbability = 0.0;

				//for (int k=0; k<numOfDimensions; k++)
				//	predictionProbability += z[sourceNode][k]*z[destNode][k];

				
				for (int k=0; k<numOfDimensions; k++)
					predictionProbability += z[sourceNode][k]*z[destNode][k];

				bw.write( String.format("%.6f",predictionProbability) +"\n");

			}

			bw.close();
			labels.close();

		}catch (Exception e) 
		{
			System.out.println(sourceNode);
			System.out.println(destNode);
			e.printStackTrace();
		} 

	}

}
