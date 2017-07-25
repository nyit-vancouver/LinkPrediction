package logRegression;


/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 6/22/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Instance {
    public int label;
    public double[] x;

    public Instance(int label, double[] x) {
        this.label = label;
        this.x = x;
    }

    public int getLabel() {
        return label;
    }

    public double[] getX() {
        return x;
    }
}
