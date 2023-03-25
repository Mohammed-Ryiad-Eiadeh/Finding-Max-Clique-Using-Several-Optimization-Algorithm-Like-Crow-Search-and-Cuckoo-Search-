package Optimizers.org;

import Discreet.org.TransferFunction;
import Discreet.org.discretion;
import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class JayaOptimizer extends Feasibility {

    private final double[][] Solutions;
    private double[] worstSolution;
    private double[] bestSolution;
    private final TransferFunction TF;
    private final int MaxIteration;
    private FitnessFunction FN;
    private long OptimizationTime;
    private final double[] AvgFitness;

    /**
     * @param AdjMatrix referred to a given graph as adjacency matrix
     * @param SetOfSolution referred to the generated population
     * @param WorstSolution referred to the worst solution
     * @param BestSolution referred to the best solution
     * @param TF referred to the transfer function
     * @param MaxIter MaxIter
     */
    public JayaOptimizer(int[][] AdjMatrix, double[][] SetOfSolution, TransferFunction TF, double[] WorstSolution, double[] BestSolution, int MaxIter) {
        super(AdjMatrix);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        this.worstSolution = WorstSolution;
        this.bestSolution = BestSolution;
        this.TF = TF;
        this.MaxIteration = MaxIter;
        FN = new FitnessFunction();
        AvgFitness = new double[MaxIteration];
    }

    /**
     * This method used to start the searching and optimization method for the current population
     */
    public void StartOptimization() {
        var StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < MaxIteration; iter++) {
            for (var i = 0; i < Solutions.length; i++) {
                // apply JAYA operator on each solution
                var EvolvedSolution = new double[Solutions[i].length];
                for (var j = 0; j < Solutions[i].length; j++) {
                    EvolvedSolution[j] = discretion.DiscreteViaTransfer(TF, Solutions[i][j] + new Random().nextDouble() * (bestSolution[j] - Solutions[i][j]) - new Random().nextDouble() * (worstSolution[j] - Solutions[i][j]));
                }
                EvolvedSolution = super.GuaranteeFeasibility(EvolvedSolution);

                // replace old solution by the new one if it is better
                if (FN.EvaluateSingleSolution(EvolvedSolution) > FN.EvaluateSingleSolution(Solutions[i]))
                    System.arraycopy(EvolvedSolution, 0, Solutions[i], 0, EvolvedSolution.length);
            }
            FN = new FitnessFunction(Solutions);
            FN.EvaluateAllSolution();
            worstSolution = FN.getWorstSolution();
            bestSolution = FN.getBestSolution();
            AvgFitness[iter] = FN.getAVG() / Solutions.length;
        }
        var EndTime = System.currentTimeMillis();
        OptimizationTime = EndTime - StartTime;
    }

    /**
     * @param newSolutions referred to new generation
     */
    public void setSolutions(double[][] newSolutions) {
        IntStream.range(0, newSolutions.length / 2).forEach(i -> System.arraycopy(newSolutions[i], 0, Solutions[i], 0, Solutions[i].length));
        IntStream.range(newSolutions.length / 2 + 1, newSolutions.length).forEach(i -> Solutions[i] = GuaranteeFeasibility(IntStream.range(0, Solutions[i].length).mapToDouble(ii -> ThreadLocalRandom.current().nextInt(2)).toArray()));
    }

    /**
     * @return the best solution among all ones
     */
    public double[] getBestSolution() {
        return bestSolution;
    }


    /**
     * @return the best solution among all ones
     */
    public double[] getWorstSolution() {
        return worstSolution;
    }

    /**
     * This method used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    public String  GetOptimizationTime() {
        return ((double) OptimizationTime / 1000) + " s ";
    }

    /**
     * @return the average fitness score of the current generation
     */
    public Double[] getAvgFitnessSeries() {
        return Arrays.stream(AvgFitness).boxed().toArray(Double[]::new);
    }

    /**
     * This method used to print the performance of the CSA optimizer
     */
    public void DisplayPerformanceSummary() {
        FN.EvaluateAllSolution().ShowEvaluationSummary();
    }

    /**
     * This methode used to visualize the best solution
     */
    public void DisplayBestSolution() {
        FN.DisplayBestSubGraph();
    }
}
