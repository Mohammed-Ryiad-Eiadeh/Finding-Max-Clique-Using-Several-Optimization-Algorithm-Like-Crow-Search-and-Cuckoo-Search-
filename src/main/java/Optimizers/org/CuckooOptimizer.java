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

public class CuckooOptimizer extends Feasibility implements CommonFunctions {

    private final double StepSizeScaling;
    private final double Lambda;
    private final double WorstNestProbability;
    private final double Delta;
    private final double[][] Solutions;
    private final TransferFunction TF;
    private final int MaxIteration;
    private FitnessFunction FN;
    private long OptimizationTime;
    private final double[] AvgFitness;

    /**
     * Default constructor of the feasibility
     *
     * @param AdjMatrix refers to the constructed adjacency matrix of the graph
     */
    public CuckooOptimizer(int[][] AdjMatrix, double[][] SetOfSolution, TransferFunction transferFunction, double SSScaling, double lambda, double worstNestProbability, double delta, int maxIter) {
        super(AdjMatrix);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        this.TF = transferFunction;
        this.StepSizeScaling = SSScaling;
        this.Lambda = lambda;
        this.WorstNestProbability = worstNestProbability;
        this.Delta = delta;
        this.MaxIteration = maxIter;
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
                var Counter = new AtomicInteger(iter);

                var EvolvedSolution = GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(ii -> discretion.DiscreteViaTransfer(TF, ii + StepSizeScaling * Math.pow(Counter.get() + 1, -Lambda))).toArray());
                var RandomIndexForRandomSolution = new Random().nextInt(Solutions.length);
                if (FN.EvaluateSingleSolution(EvolvedSolution) > FN.EvaluateSingleSolution(Solutions[RandomIndexForRandomSolution]))
                    System.arraycopy(EvolvedSolution, 0, Solutions[i], 0, EvolvedSolution.length);

                if (new Random().nextDouble() < WorstNestProbability) {
                    var R1 = new Random().nextInt(Solutions.length);
                    var R2 = new Random().nextInt(Solutions.length);
                    for (var j = 0; j < Solutions[i].length; j++)
                        EvolvedSolution[j] = discretion.DiscreteViaTransfer(TF, Solutions[i][j] + Delta * (Solutions[R1][j] - Solutions[R2][j]));
                    EvolvedSolution = GuaranteeFeasibility(EvolvedSolution);
                    if (FN.EvaluateSingleSolution(EvolvedSolution) > FN.EvaluateSingleSolution(Solutions[i]))
                        System.arraycopy(GuaranteeFeasibility(EvolvedSolution), 0, Solutions[i], 0, Solutions[i].length);
                }
            }
            FN = new FitnessFunction(Solutions);
            FN.EvaluateAllSolution();
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
     * This method used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    @Override
    public String GetOptimizationTime() {
        return ((double) OptimizationTime / 1000) + " s ";
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
        return Arrays.stream(AvgFitness).boxed().toArray(Double[]::new);
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
