package logRegression;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogisticRegressionUseSGD
{

    /** the learning rate */
    private double rate;

    /** the weight to learn */
    private double[] weights;

    /** the number of iterations */
    private int ITERATIONS = 1000;

    public LogisticRegressionUseSGD(int n)
    {
        this.rate = 0.001;
        weights = new double[n];
    }

    private double sigmoid(double z)
    {
        return 1 / (1 + Math.exp(-z));
    }

    public void train(List<Instance> instances)
    {
        for (int n = 0; n < ITERATIONS; n++)
        {
            double lik = 0.0;
            for (int i = 0; i < instances.size(); i++)
            {
                double[] x = instances.get(i).getX();
                double predicted = classify(x);
                int label = instances.get(i).getLabel();
                for (int j = 0; j < weights.length; j++)
                {
                    weights[j] = weights[j] + rate * (label - predicted) * x[j];
                }
            }
            //System.out.println("iteration: " + n + " " + Arrays.toString(weights) + " mle: " + lik);
        }
    }

    private double classify(double[] x)
    {
        double logit = .0;
        for (int i = 0; i < weights.length; i++)
        {
            logit += weights[i] * x[i];
        }
        return sigmoid(logit);
    }

    public static void main(String... args) throws IOException
    {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("regressionResult.txt")));
        List<Instance> instances = DataSet.readDataSet("dataset1.txt");
        Collections.shuffle(instances);
        
        LogisticRegressionUseSGD logistic = new LogisticRegressionUseSGD(4);  // LogisticRegressionUseSGD(n) where n is num of features +1
        logistic.train(instances);
        int flasecount = 0;
        double totalError = 0.0;
		double diff = 0.0;
        
        for (Instance test : instances)
        {

            //System.out.println(test.getLabel() + " " + test.getX()[0] + ", " + test.getX()[1] +  ", " + test.getX()[2]);
            double probx = logistic.classify(test.getX());
            double y = test.getLabel();
            boolean correct = true;
            
			diff = y - probx;
			diff*=diff;
			totalError+=diff;

			bw.write(test.getX()[3] + " " + test.getLabel() + " " + probx + "\n");

            
            if ((probx >= 0.5 && y == 0) ||  (probx < 0.5 && y == 1))
            {
                correct = false;
                flasecount++;
            }
            
            /*if(!correct)
            {
               System.out.println("prob(x) = " + probx + " and real is: "  + test.getLabel() +" and result is:"+correct);
            }
            else
            {
               System.out.println("prob(x) = " + probx + " and real is: "  + test.getLabel());  
            }*/
            
        }
        System.out.println("total line:"+instances.size()+", and incorrect count:"+flasecount);
		System.out.println("totalError: " + Math.sqrt(totalError));
		bw.close();
    }
}
