package Optimizers.org;

import Discreet.org.TransferFunction;
import Discreet.org.discretion;
import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;
import Improvements.org.ImprovingMethods;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Enhanced crow optimizer
 */
public class CrowOptimizer extends Feasibility {

    private final double[][] Solutions;
    private final double[][] FolkMemory;
    private final TransferFunction TF;
    private final double FL;
    private final double AP;
    private double Alpha;
    private final int MaxIteration;
    private FitnessFunction FN;
    private long OptimizationTime;
    private ImprovingMethods improvingMethod;
    private final double[] AvgFitness;
    private JayaOptimizer JayaOptimizer;
    private CuckooOptimizer CuckooSearch;

    /**
     * @param AdjMatrix referred to a given graph as adjacency matrix
     * @param SetOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param MaxIter MaxIter
     */
    public CrowOptimizer(int[][] AdjMatrix, double[][] SetOfSolution, TransferFunction TF, double FL, double AP, int MaxIter) {
        super(AdjMatrix);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.FolkMemory = new double[Solutions.length][Solutions[0].length];
        IntStream.range(0, FolkMemory.length).forEach(i -> FolkMemory[i] = Arrays.copyOf(Solutions[i], Solutions[0].length));
        this.MaxIteration = MaxIter;
        FN = new FitnessFunction();
        AvgFitness = new double[MaxIteration];
    }

    /**
     * This contractor is used to instantiate Crow Search Optimizer
     * @param AdjMatrix referred to a given graph as adjacency matrix
     * @param SetOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param Alpha referred to alpha
     * @param Lambda referred to lambda
     * @param MaxIter referred to the maximum iteration
     */
    public CrowOptimizer(int[][] AdjMatrix, double[][] SetOfSolution, TransferFunction TF, double FL, double AP, double Alpha, double Lambda, int MaxIter) {
        super(AdjMatrix);
        this.improvingMethod = new ImprovingMethods(AdjMatrix, Lambda);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.Alpha = Alpha;
        this.FolkMemory = new double[Solutions.length][Solutions[0].length];
        IntStream.range(0, FolkMemory.length).forEach(i -> FolkMemory[i] = Arrays.copyOf(Solutions[i], Solutions[0].length));
        this.MaxIteration = MaxIter;
        FN = new FitnessFunction();
        AvgFitness = new double[MaxIteration];
    }

    /**
     * This contractor is used to instantiate Crow Search Optimizer with Jaya optimizer
     * @param AdjMatrix referred to a given graph as adjacency matrix
     * @param SetOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param Alpha referred to alpha
     * @param Lambda referred to lambda
     * @param jayaOptimizer a constructor referred to instantiate Jaya optimizer
     * @param MaxIter referred to the maximum iteration
     */
    public CrowOptimizer(int[][] AdjMatrix, double[][] SetOfSolution,TransferFunction TF, double FL, double AP, double Alpha, double Lambda, JayaOptimizer jayaOptimizer, int MaxIter) {
        super(AdjMatrix);
        this.improvingMethod = new ImprovingMethods(AdjMatrix, Lambda);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        this.JayaOptimizer = jayaOptimizer;
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.Alpha = Alpha;
        this.FolkMemory = new double[Solutions.length][Solutions[0].length];
        IntStream.range(0, FolkMemory.length).forEach(i -> FolkMemory[i] = Arrays.copyOf(Solutions[i], Solutions[0].length));
        this.MaxIteration = MaxIter;
        FN = new FitnessFunction();
        AvgFitness = new double[MaxIteration];
    }

    /**
     * This contractor is used to instantiate Crow Search Optimizer with cuckoo optimizer
     * @param AdjMatrix referred to a given graph as adjacency matrix
     * @param SetOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param Alpha referred to alpha
     * @param cuckooOptimizer a constructor referred to instantiate Cuckoo optimizer
     * @param MaxIter referred to the maximum iteration
     */
    public CrowOptimizer(int[][] AdjMatrix, double[][] SetOfSolution,TransferFunction TF, double FL, double AP, double Alpha, CuckooOptimizer cuckooOptimizer, int MaxIter) {
        super(AdjMatrix);
        this.improvingMethod = new ImprovingMethods(AdjMatrix);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        this.CuckooSearch = cuckooOptimizer;
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.Alpha = Alpha;
        this.FolkMemory = new double[Solutions.length][Solutions[0].length];
        IntStream.range(0, FolkMemory.length).forEach(i -> FolkMemory[i] = Arrays.copyOf(Solutions[i], Solutions[0].length));
        this.MaxIteration = MaxIter;
        FN = new FitnessFunction();
        AvgFitness = new double[MaxIteration];
    }

    /**
     * This method used to start the searching and optimization method for Binary Crow Search Algorithm
     */
    public void BCSAStartOptimization() {
        var StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < MaxIteration; iter++) {
            for (var i = 0; i < Solutions.length; i++) {
                var Rv = new Random().nextDouble();
                if (Rv >= AP) {
                    var uniformDistribution = new Random().nextDouble();
                    var randomCrow = new Random().nextInt(FolkMemory.length);
                    var ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    Solutions[i] = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(V -> discretion.DiscreteViaTransfer(TF, V + uniformDistribution + FL * (FolkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else
                    // for CSA generate random solution
                    Solutions[i] = super.GuaranteeFeasibility(IntStream.range(0, Solutions[0].length).map(j -> ThreadLocalRandom.current().nextInt(2)).asDoubleStream().toArray());
                }
            // define variable to store better solution fitness scores
            var sumScores = 0.0;

            // update folk memory
            for (var i = 0; i < Solutions.length; i++) {
                var newSolution = FN.EvaluateSingleSolution(Solutions[i]);
                var oldSolution = FN.EvaluateSingleSolution(FolkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(Solutions[i], 0, FolkMemory[i], 0, FolkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            // calculate the average fitness
            AvgFitness[iter] = sumScores / Solutions.length;
        }
        var EndTime = System.currentTimeMillis();
        OptimizationTime = EndTime - StartTime;
        FN = new FitnessFunction(FolkMemory);
    }

    /**
     * This method used to start the searching and optimization method for Improved Binary Search Algorithm
     */
    public void IBCSAStartOptimization() {
        // The mutation rate
        var mr = 1 / Solutions[0].length;

        var StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < MaxIteration; iter++) {
            for (var i = 0; i < Solutions.length; i++) {
                var Rv = new Random().nextDouble();
                if (Rv >= AP) {
                    var uniformDistribution = new Random().nextDouble();
                    var randomCrow = new Random().nextInt(FolkMemory.length);
                    var ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    Solutions[i] = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(V -> discretion.DiscreteViaTransfer(TF, V + uniformDistribution + FL * (FolkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else
                    // for IBCSA
                    if (new Random().nextDouble() >= 0.5) {
                        // the following loop refers to the mutation operation
                        Solutions[i] = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(V -> V = new Random().nextDouble() < mr ? 1 - V : V).toArray());
                    } else {
                        // the following loop refers to the lev'y flight term operation
                        var iteration = new AtomicInteger(iter);
                        Solutions[i] = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(V -> discretion.DiscreteViaTransfer(TF, V + Alpha * improvingMethod.Levy(iteration.get()))).toArray());
                    }
                    // apply XOR and Single point crossover and select the best solution
                    var x1 = improvingMethod.DoXor(Solutions, FN);
                    var x2 = improvingMethod.DoSinglePointCrossOver(Solutions, FN);

                    var Evolved = FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? x1 : x2;
                    Solutions[i] = FN.EvaluateSingleSolution(Solutions[i]) > FN.EvaluateSingleSolution(Evolved) ? Solutions[i] : Evolved;
            }
            // define variable to store better solution fitness scores
            var sumScores = 0.0;

            // update folk memory
            for (var i = 0; i < Solutions.length; i++) {
                var newSolution = FN.EvaluateSingleSolution(Solutions[i]);
                var oldSolution = FN.EvaluateSingleSolution(FolkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(Solutions[i], 0, FolkMemory[i], 0, FolkMemory[0].length);
                }
                else sumScores += oldSolution;
            }

            // calculate the average fitness
            AvgFitness[iter] = sumScores / Solutions.length;
        }
        var EndTime = System.currentTimeMillis();
        OptimizationTime = EndTime - StartTime;
        FN = new FitnessFunction(FolkMemory);
    }

    /**
     * This method used to start the searching and optimization method for IBCSA-BJO
     */
    public void IBCSA_BJOStartOptimization() {
        var StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < MaxIteration; iter++) {
            for (var i = 0; i < Solutions.length; i++) {
                var Rv = new Random().nextDouble();
                if (Rv >= AP) {
                    var uniformDistribution = new Random().nextDouble();
                    var randomCrow = new Random().nextInt(FolkMemory.length);
                    var ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    Solutions[i] = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(V -> discretion.DiscreteViaTransfer(TF, V + uniformDistribution + FL * (FolkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else {
                    // run Jaya optimizer to improve the exploration phase
                    if (iter % 100 == 0) {
                        JayaOptimizer.StartOptimization();
                        var jayaSolution = JayaOptimizer.getBestSolution();
                        if (FN.EvaluateSingleSolution(jayaSolution) > FN.EvaluateSingleSolution(Solutions[i]))
                            System.arraycopy(jayaSolution, 0, Solutions[i], 0, jayaSolution.length);
                    }
                }
                // apply XOR and Single point crossover and select the best solution
                    var x1 = improvingMethod.DoXor(Solutions, FN);
                    var x2 = improvingMethod.DoSinglePointCrossOver(Solutions, FN);

                    var Evolved = FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? x1 : x2;
                    Solutions[i] = FN.EvaluateSingleSolution(Solutions[i]) > FN.EvaluateSingleSolution(Evolved) ? Solutions[i] : Evolved;
            }
            // define variable to store better solution fitness scores
            var sumScores = 0.0;

            // update folk memory
            for (var i = 0; i < Solutions.length; i++) {
                var newSolution = FN.EvaluateSingleSolution(Solutions[i]);
                var oldSolution = FN.EvaluateSingleSolution(FolkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(Solutions[i], 0, FolkMemory[i], 0, FolkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            if (iter % 100 == 0)
                JayaOptimizer.setSolutions(Solutions);

            // calculate the average fitness
            AvgFitness[iter] = sumScores / Solutions.length;
        }
        var EndTime = System.currentTimeMillis();
        OptimizationTime = EndTime - StartTime;
        FN = new FitnessFunction(FolkMemory);
    }

    /**
     * This method used to start the searching and optimization method for IBCSA-BCS
     */
    public void IBCSA_BCSStartOptimization() {
        // The mutation rate
        var mr = 1 / Solutions[0].length;

        var StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < MaxIteration; iter++) {
            for (var i = 0; i < Solutions.length; i++) {
                var Rv = new Random().nextDouble();
                if (Rv >= AP) {
                    var uniformDistribution = new Random().nextDouble();
                    var randomCrow = new Random().nextInt(FolkMemory.length);
                    var ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    Solutions[i] = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(V -> discretion.DiscreteViaTransfer(TF, V + uniformDistribution + FL * (FolkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else {
                    // run Jaya optimizer to improve the exploration phase
                    if (iter % 100 == 0) {
                        CuckooSearch.StartOptimization();
                        var cuckooSolution = CuckooSearch.getBestSolution();
                        if (FN.EvaluateSingleSolution(cuckooSolution) > FN.EvaluateSingleSolution(Solutions[i]))
                            System.arraycopy(cuckooSolution, 0, Solutions[i], 0, cuckooSolution.length);
                    }
                }
                // apply mutation, XOR and Single point crossover and select the best solution
                var x3 = GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(ii -> ThreadLocalRandom.current().nextDouble() < mr ? 1 - ii : ii).toArray());
                var x1 = improvingMethod.DoXor(Solutions, FN);
                var x2 = improvingMethod.DoSinglePointCrossOver(Solutions, FN);

                var Evolved = FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x3) ? x1 : x3 : x2;

                Solutions[i] = FN.EvaluateSingleSolution(Solutions[i]) > FN.EvaluateSingleSolution(Evolved) ? Solutions[i] : Evolved;

            }
            // define variable to store better solution fitness scores
            var sumScores = 0.0;

            // update folk memory
            for (var i = 0; i < Solutions.length; i++) {
                var newSolution = FN.EvaluateSingleSolution(Solutions[i]);
                var oldSolution = FN.EvaluateSingleSolution(FolkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(Solutions[i], 0, FolkMemory[i], 0, FolkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            if (iter % 100 == 0)
                CuckooSearch.setSolutions(Solutions);

            // calculate the average fitness
            AvgFitness[iter] = sumScores / Solutions.length;
        }
        var EndTime = System.currentTimeMillis();
        OptimizationTime = EndTime - StartTime;
        FN = new FitnessFunction(FolkMemory);
    }

    /**
     * This method used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    public String  GetOptimizationTime() {
        return ((double) OptimizationTime / 1000) + " s ";
    }

    /**
     * @return the worst solutions among all ones
     */
    public double[] getWorstSolution() {
        return FN.getWorstSolution();
    }

    /**
     * @return the best solutions among all ones
     */
    public double[] getBestSolution() {
        return FN.getBestSolution();
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
