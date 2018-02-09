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
 * 		This is to predict the probability of a link between two nodes a and b by calculating the product of 
 * 		their sparse encoding of latent positions in Z by calculating ZZ^T (TKDE16)
 * 		input file format example
 *
 *			number of nodes
 *			node_id,number_non-zero:index1,weight1:index2,weight2:...index_d,weightd
 *
 *		Each index gives the non-zero index of each dimension, and each weight gives the non-zero position for that dimension.
 *		Note that the node_id is within the range [0,n-1], where n is number of nodes, and the indexes are sorted in ascending order too.
 *
 *		given an input of nodes a and b, the output is Z(a).Z(b)
 * 
 * @author aminmf
 */


public class InfectionLinkPredict {

	public static void main(String[] args) 
	{	
		// inputs
		int numberOfNodes = 410, numOfDimensions = 20;

		double[][] z = new double[numberOfNodes][numOfDimensions];
		ArrayList<ArrayList<Integer>> neighbors = new ArrayList<ArrayList<Integer>>();

		ArrayList <ArrayList<InfectionLinkPredict>> latentSpace = new ArrayList <ArrayList<InfectionLinkPredict>>(); 
		String currentLineString, numOfNonZero=null;
		int latentPosIndex=0, nodeIndex = 0, neighborIndex = 0;
		double weight=0.0;

		try{
			BufferedReader br = new BufferedReader(new FileReader("Infection/Zmatrix8.txt"));
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
		try{
			BufferedReader br2 = new BufferedReader(new FileReader("Infection/inf8_links.txt"));
			// file format example (3 nodes)
			//3
			//0,0
			//1,3:2,1:3,1:4,1
			//...
			int total_size = 0;

			currentLineString = br2.readLine();
			int numOfNodes = Integer.parseInt(currentLineString);
			int from = 0;
			int to = 0;
			for (int i=0; i < numOfNodes; i++){

				currentLineString = br2.readLine();
				from = 0;
				to = currentLineString.indexOf(",", from);
				nodeIndex = Integer.parseInt(currentLineString.substring(from,to));
				//System.out.println("nodeIndex:" + nodeIndex);
				from = to+1;
				to = currentLineString.indexOf(":", from);
				if (to<=0) to = currentLineString.length();
				numOfNonZero = currentLineString.substring(from,to);
				//System.out.println("numOfNonZero:" + numOfNonZero);

				ArrayList<Integer> n = new ArrayList<Integer>();

				for (int j=0; j < Integer.parseInt(numOfNonZero) ; j++){
					from = to+1;
					to = currentLineString.indexOf(",", from);
					neighborIndex = Integer.parseInt(currentLineString.substring(from,to));
					// ignoring weight
					from = to+1;  to = from+1;
					//System.out.println("neighborIndex:" + neighborIndex);
					n.add(neighborIndex);
				}
				neighbors.add(n);
				total_size += n.size();
			}

			System.out.println("Loaded coauthorship matrix in memory.");
			System.out.println("Total links: " + total_size);
			//System.out.println(neighbors);
			br2.close();


			Double totalError = 0.0;
			Double diff = 0.0;
			Double predictionProbability = 0.0;
			//int sourceNodeID = 11785;

			for(int sourceNodeID = 0; sourceNodeID <410; sourceNodeID++){ 
				for (int destNodeID: neighbors.get(sourceNodeID)){
					//System.out.println(destNodeID);

					predictionProbability = 0.0;
					for (int k=0; k<numOfDimensions; k++){
						predictionProbability += z[sourceNodeID][k]*z[destNodeID][k];
					}
					//predictionProbability = (predictionProbability>=0.5) ? 1.0 : 0.0;
					//System.out.println(predictionProbability);
					diff = 1 - predictionProbability;
					diff*=diff;
					totalError+=diff;
				}
			}
			System.out.println("totalError: " + Math.sqrt(totalError));


		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
