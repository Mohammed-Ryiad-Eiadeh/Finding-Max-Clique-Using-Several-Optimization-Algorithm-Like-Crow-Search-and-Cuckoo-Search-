package Optimizers.org;

import Discreet.org.TransferFunction;
import Discreet.org.discretion;
import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;
import Optimizers.org.CommomMethods.CommonFunctions;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Cuckoo optimizer
 */
public class CuckooOptimizer extends Feasibility
        implements CommonFunctions {

    private final double stepSizeScaling;
    private final double lambda;
    private final double worstNestProbability;
    private final double delta;
    private final double[][] solutions;
    private final TransferFunction TF;
    private final int maxIteration;
    private FitnessFunction FN;
    private long optimizationTime;
    private final double[] avgFitness;

    public CuckooOptimizer(int[][] adjMatrix, double[][] setOfSolution, TransferFunction transferFunction, double stepSizeScaling, double lambda, double worstNestProbability, double delta, int maxIter) {
        super(adjMatrix);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        this.TF = transferFunction;
        this.stepSizeScaling = stepSizeScaling;
        this.lambda = lambda;
        this.worstNestProbability = worstNestProbability;
        this.delta = delta;
        this.maxIteration = maxIter;
        FN = new FitnessFunction();
        avgFitness = new double[maxIteration];
    }

    /**
     * This method used to start the searching and optimization method for the current population
     */
    public void StartOptimization() {
        long startTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            AtomicInteger counter = new AtomicInteger(iter);
            for (double[] doubles : solutions) {
                double[] evolvedSolution = GuaranteeFeasibility(Arrays.stream(doubles).map(ii -> discretion.DiscreteViaTransfer(TF, ii + stepSizeScaling * Math.pow(counter.get() + 1, -lambda))).toArray());
                int randomIndexForRandomSolution = new Random().nextInt(solutions.length);
                if (FN.EvaluateSingleSolution(evolvedSolution) > FN.EvaluateSingleSolution(solutions[randomIndexForRandomSolution]))
                    System.arraycopy(evolvedSolution, 0, doubles, 0, evolvedSolution.length);
                if (new Random().nextDouble() < worstNestProbability) {
                    for (var j = 0; j < doubles.length; j++)
                        evolvedSolution[j] = discretion.DiscreteViaTransfer(TF, doubles[j] + delta * (solutions[new Random().nextInt(solutions.length)][j] - solutions[new Random().nextInt(solutions.length)][j]));
                    evolvedSolution = GuaranteeFeasibility(evolvedSolution);
                    if (FN.EvaluateSingleSolution(evolvedSolution) > FN.EvaluateSingleSolution(doubles))
                        System.arraycopy(GuaranteeFeasibility(evolvedSolution), 0, doubles, 0, doubles.length);
                }
            }
            FN = new FitnessFunction(solutions);
            FN.EvaluateAllSolution();
            avgFitness[iter] = FN.getAVG() / solutions.length;
        }
        long endTime = System.currentTimeMillis();
        optimizationTime = endTime - startTime;
    }

    /**
     * @param newSolutions referred to new generation
     */
    public void setSolutions(double[][] newSolutions) {
        IntStream.range(0, newSolutions.length / 2).forEach(i -> System.arraycopy(newSolutions[i], 0, solutions[i], 0, solutions[i].length));
        IntStream.range(newSolutions.length / 2 + 1, newSolutions.length).forEach(i -> solutions[i] = GuaranteeFeasibility(IntStream.range(0, solutions[i].length).mapToDouble(ii -> ThreadLocalRandom.current().nextInt(2)).toArray()));
    }

    /**
     * This method used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    @Override
    public String GetOptimizationTime() {
        return ((double) optimizationTime / 1000) + " s ";
    }

    /**
     * @return the worst solutions among all ones
     */
    @Override
    public double[] getWorstSolution() {
        return FN.getWorstSolution();
    }

    /**
     * @return the best solutions among all ones
     */
    @Override
    public double[] getBestSolution() {
        return FN.getBestSolution();
    }

    /**
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
