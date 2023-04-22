package Optimizers.org.CommomMethods;

/**
 * This interface includes the common functions among all optimizers
 */
public interface CommonFunctions {
    String GetOptimizationTime();
    double[] getBestSolution();
    double[] getWorstSolution();
    Double[] getAvgFitnessSeries();
    void DisplayPerformanceSummary();
    void DisplayBestSolution();
}
