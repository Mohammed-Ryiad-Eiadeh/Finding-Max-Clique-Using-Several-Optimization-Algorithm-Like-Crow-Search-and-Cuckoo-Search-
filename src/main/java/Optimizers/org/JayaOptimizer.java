package Optimizers.org;

import Discreet.org.TransferFunction;
import Discreet.org.discretion;
import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;
import Optimizers.org.CommomMethods.CommonFunctions;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class JayaOptimizer extends Feasibility
        implements CommonFunctions {

    private final double[][] solutions;
    private double[] worstSolution;
    private double[] bestSolution;
    private final TransferFunction TF;
    private final int maxIteration;
    private FitnessFunction FN;
    private long optimizationTime;
    private final double[] avgFitness;

    /**
     * @param adjMatrix referred to a given graph as adjacency matrix
     * @param setOfSolution referred to the generated population
     * @param worstSolution referred to the worst solution
     * @param bestSolution referred to the best solution
     * @param TF referred to the transfer function
     * @param maxIter MaxIter
     */
    public JayaOptimizer(int[][] adjMatrix, double[][] setOfSolution, TransferFunction TF, double[] worstSolution, double[] bestSolution, int maxIter) {
        super(adjMatrix);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        this.worstSolution = worstSolution;
        this.bestSolution = bestSolution;
        this.TF = TF;
        this.maxIteration = maxIter;
        FN = new FitnessFunction();
        avgFitness = new double[maxIteration];
    }

    /**
     * This method used to start the searching and optimization method for the current population
     */
    public void StartOptimization() {
        var startTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            for (double[] solution : solutions) {
                // apply JAYA operator on each solution
                double[] EvolvedSolution = new double[solution.length];
                for (int j = 0; j < solution.length; j++)
                    EvolvedSolution[j] = discretion.DiscreteViaTransfer(TF, solution[j] + new Random().nextDouble() * (bestSolution[j] - solution[j]) - new Random().nextDouble() * (worstSolution[j] - solution[j]));
                EvolvedSolution = super.GuaranteeFeasibility(EvolvedSolution);
                // replace old solution by the new one if it is better
                if (FN.EvaluateSingleSolution(EvolvedSolution) > FN.EvaluateSingleSolution(solution))
                    System.arraycopy(EvolvedSolution, 0, solution, 0, EvolvedSolution.length);
            }
            FN = new FitnessFunction(solutions);
            FN.EvaluateAllSolution();
            worstSolution = FN.getWorstSolution();
            bestSolution = FN.getBestSolution();
            avgFitness[iter] = FN.getAVG() / solutions.length;
        }
        var endTime = System.currentTimeMillis();
        optimizationTime = endTime - startTime;
    }

    /**
     * this method is used to set a set of solutions
     * @param newSolutions referred to new generation
     */
    public void setSolutions(double[][] newSolutions) {
        IntStream.range(0, newSolutions.length / 2).forEach(i -> System.arraycopy(newSolutions[i], 0, solutions[i], 0, solutions[i].length));
        IntStream.range(newSolutions.length / 2 + 1, newSolutions.length).forEach(i -> solutions[i] = GuaranteeFeasibility(IntStream.range(0, solutions[i].length).mapToDouble(ii -> ThreadLocalRandom.current().nextInt(2)).toArray()));
    }

    /**
     * This method is used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    @Override
    public String GetOptimizationTime() {
        return ((double) optimizationTime / 1000) + " s ";
    }

    /**
     * This method is used to return the best current solution
     * @return the best solution among all ones
     */
    @Override
    public double[] getBestSolution() {
        return bestSolution;
    }

    /**
     * This method is used to return the worst current solution
     * @return the worst solution among all ones
     */
    @Override
    public double[] getWorstSolution() {
        return worstSolution;
    }

    /**
     * This method is used to return the average fitness score of the current generation
     * @return the average fitness score of the current generation
     */
    @Override
    public Double[] getAvgFitnessSeries() {
        return Arrays.stream(avgFitness).boxed().toArray(Double[]::new);
    }

    /**
     * This method used to print the performance of the CSA optimizer
     */
    @Override
    public void DisplayPerformanceSummary() {
        FN.EvaluateAllSolution().ShowEvaluationSummary();
    }

    /**
     * This methode used to visualize the best solution
     */
    @Override
    public void DisplayBestSolution() {
        FN.DisplayBestSubGraph();
    }
}
