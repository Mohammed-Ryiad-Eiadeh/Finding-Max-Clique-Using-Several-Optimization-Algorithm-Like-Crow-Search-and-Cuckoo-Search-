package Optimizers.org.CommomMethods;

public interface CommonFunctions {
    String GetOptimizationTime();
    double[] getBestSolution();
    double[] getWorstSolution();
    Double[] getAvgFitnessSeries();
    void DisplayPerformanceSummary();
    void DisplayBestSolution();
}
