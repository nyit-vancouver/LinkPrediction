package logRegression;


import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class LogisticRegressionUseMLE
{



	/** the learning rate */
	private double rate;

	/** the epsilon threshold */
	private double eps;

	/** the C parameter */
	private double C;

	/** the weight to learn */
	private double[] weights;

	public LogisticRegressionUseMLE(int n) {
		this.rate = 0.01;
		this.eps = 0.01;
		this.C = 0.01;
		weights = new double[n];
	}

	private double sigmoid(double z) {
		return 1 / (1 + Math.exp(-z));
	}

	public void train(List<Instance> instances) {
		double likelihood_old = 0.0;
		double likelihood_diff = 1000;
		int n = 1;

		while (likelihood_diff > eps) {
			double likelihood = 0.0;

			for (int i = 0; i < instances.size(); i++) {
				double[] x = instances.get(i).getX();
				double predicted = classify(x);
				int label = instances.get(i).getLabel();
				for (int j = 0; j < weights.length; j++) {
					weights[j] = weights[j] + rate * (label - predicted) * x[j];
				}
				likelihood += label * Math.log(classify(x)) + (1 - label)
						* Math.log(1 - classify(x));
			}

			// parameters normalization
			double regSum = 0;
			for (int k = 1; k < weights.length; k++) {
				regSum += (weights[k] * weights[k]);
			}
			regSum = (0.5 * C) * regSum;
			likelihood += regSum;

			likelihood_diff = Math.abs(likelihood - likelihood_old);
			likelihood_old = likelihood;
			System.out.println("iteration: " + n + " " + Arrays.toString(weights) + " mle: "
					+ likelihood);
			n++;
		}
	}

	private double classify(double[] x) {
		double logit = .0;
		for (int i = 0; i < weights.length; i++) {
			logit += weights[i] * x[i];
		}
		return sigmoid(logit);
	}

	public static void main(String... args) throws FileNotFoundException {
		List<Instance> trainset = DataSet.readDataSet("dblpdataset1");
		List<Instance> testset = DataSet.readDataSet("dblpdataset1");
		LogisticRegressionUseMLE logistic = new LogisticRegressionUseMLE(4);
		logistic.train(trainset);
		int flasecount = 0;
		double diff = 0.0;
        double totalError = 0.0;
        		
		for (Instance test : testset) {

			double probx = logistic.classify(test.getX());
			double y = test.getLabel();
			boolean correct = true;
			
			diff = y - probx;
			diff*=diff;
			totalError+=diff;
			
			if ((probx >= 0.5 && y == 0) || (probx < 0.5 && y == 1)) {
				correct = false;
				flasecount++;
			}
			/*if (!correct) {
				System.out.println("prob(x) = " + probx + " and real is: " + test.getLabel()
						+ " and result is:" + correct);
			} else {
				System.out.println("prob(x) = " + probx + " and real is: " + test.getLabel());
			}*/

		}
		System.out.println("Total instances:" + testset.size() + ", and incorrect count:" + flasecount);
		System.out.println("totalError: " + Math.sqrt(totalError));

	}
}
