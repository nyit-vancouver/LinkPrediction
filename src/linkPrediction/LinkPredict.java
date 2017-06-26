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
 * 		This is to predict the probability of a link between two nodes a and be by calculating the product of 
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

class LatentPosWeigh{
	@Override
	public String toString() {
		return "LatentPosWeigh [latentPosIndex=" + latentPosIndex + ", weight=" + weight + "]";
	}
	int latentPosIndex;
	double weight;
	public LatentPosWeigh(int latentPosIndex, double weight) {
		this.latentPosIndex = latentPosIndex;
		this.weight = weight;
	}
}

public class LinkPredict {

	public static void main(String[] args) 
	{	
		ArrayList <ArrayList<LinkPredict>> latentSpace = new ArrayList <ArrayList<LinkPredict>>(); 
		String currentLineString, nodeIndex = null, numOfNonZero=null, weight=null;
		int latentPosIndex=0;
		
		try{
			BufferedReader br = new BufferedReader(new FileReader("Zmatrix.txt"));
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
				System.out.println("nodeIndex:" + currentLineString.substring(from,to));
				from = to+1;
				to = currentLineString.indexOf(":", from);
				numOfNonZero = currentLineString.substring(from,to);
				System.out.println("numOfNonZero:" + numOfNonZero);
				for (int j=0; j < Integer.parseInt(numOfNonZero) ; j++){
					from = to+1;
					to = currentLineString.indexOf(",", from);
					latentPosIndex = Integer.parseInt(currentLineString.substring(from,to));
					
					from = to+1;
					to = from+10;
					weight = currentLineString.substring(from, to);
					System.out.println("latentPosIndex:" + latentPosIndex + ", weight:" + weight);
					LatentPosWeigh LPW = new LatentPosWeigh(latentPosIndex, Double.parseDouble(weight));
					System.out.println(LPW);

				}
			}

			br.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
