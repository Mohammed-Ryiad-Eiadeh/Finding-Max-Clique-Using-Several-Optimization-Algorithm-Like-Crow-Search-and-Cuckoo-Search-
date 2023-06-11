package Optimizers.org;

import Discreet.org.TransferFunction;
import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;
import Improvements.org.ImprovingMethods;
import Optimizers.org.CommomMethods.CommonFunctions;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Enhanced crow optimizer
 */
public class CrowOptimizer extends Feasibility implements CommonFunctions {
    private final double[][] solutions;
    private final double[][] folkMemory;
    private final TransferFunction TF;
    private final double FL;
    private final double AP;
    private double alpha;
    private final int maxIteration;
    private FitnessFunction FN;
    private long optimizationTime;
    private ImprovingMethods improvingMethod;
    private final double[] avgFitness;
    private JayaOptimizer jayaOptimizer;
    private CuckooOptimizer cuckooSearch;

    /**
     * @param adjMatrix referred to a given graph as adjacency matrix
     * @param setOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param maxIter MaxIter
     */
    public CrowOptimizer(int[][] adjMatrix, double[][] setOfSolution, TransferFunction TF, double FL, double AP, int maxIter) {
        super(adjMatrix);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.folkMemory = new double[solutions.length][solutions[0].length];
        IntStream.range(0, folkMemory.length).forEach(i -> folkMemory[i] = Arrays.copyOf(solutions[i], solutions[0].length));
        this.maxIteration = maxIter;
        FN = new FitnessFunction();
        avgFitness = new double[maxIteration];
    }

    /**
     * This contractor is used to instantiate Crow Search Optimizer
     * @param adjMatrix referred to a given graph as adjacency matrix
     * @param setOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param alpha referred to alpha
     * @param lambda referred to lambda
     * @param maxIter referred to the maximum iteration
     */
    public CrowOptimizer(int[][] adjMatrix, double[][] setOfSolution, TransferFunction TF, double FL, double AP, double alpha, double lambda, int maxIter) {
        super(adjMatrix);
        this.improvingMethod = new ImprovingMethods(adjMatrix, lambda);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.alpha = alpha;
        this.folkMemory = new double[solutions.length][solutions[0].length];
        IntStream.range(0, folkMemory.length).forEach(i -> folkMemory[i] = Arrays.copyOf(solutions[i], solutions[0].length));
        this.maxIteration = maxIter;
        FN = new FitnessFunction();
        avgFitness = new double[maxIteration];
    }

    /**
     * This contractor is used to instantiate Crow Search Optimizer with Jaya optimizer
     * @param adjMatrix referred to a given graph as adjacency matrix
     * @param setOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param alpha referred to alpha
     * @param lambda referred to lambda
     * @param jayaOptimizer a constructor referred to instantiate Jaya optimizer
     * @param MaxIter referred to the maximum iteration
     */
    public CrowOptimizer(int[][] adjMatrix, double[][] setOfSolution, TransferFunction TF, double FL, double AP, double alpha, double lambda, JayaOptimizer jayaOptimizer, int MaxIter) {
        super(adjMatrix);
        this.improvingMethod = new ImprovingMethods(adjMatrix, lambda);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        this.jayaOptimizer = jayaOptimizer;
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.alpha = alpha;
        this.folkMemory = new double[solutions.length][solutions[0].length];
        IntStream.range(0, folkMemory.length).forEach(i -> folkMemory[i] = Arrays.copyOf(solutions[i], solutions[0].length));
        this.maxIteration = MaxIter;
        FN = new FitnessFunction();
        avgFitness = new double[maxIteration];
    }

    /**
     * This contractor is used to instantiate Crow Search Optimizer with cuckoo optimizer
     * @param adjMatrix referred to a given graph as adjacency matrix
     * @param setOfSolution referred to the generated population
     * @param TF referred to the transfer function
     * @param FL referred to the flight length
     * @param AP referred to the awareness probability of the crow
     * @param alpha referred to alpha
     * @param cuckooOptimizer a constructor referred to instantiate Cuckoo optimizer
     * @param maxIter referred to the maximum iteration
     */
    public CrowOptimizer(int[][] adjMatrix, double[][] setOfSolution, TransferFunction TF, double FL, double AP, double alpha, CuckooOptimizer cuckooOptimizer, int maxIter) {
        super(adjMatrix);
        this.improvingMethod = new ImprovingMethods(adjMatrix);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        this.cuckooSearch = cuckooOptimizer;
        this.TF = TF;
        this.FL = FL;
        this.AP = AP;
        this.alpha = alpha;
        this.folkMemory = new double[solutions.length][solutions[0].length];
        IntStream.range(0, folkMemory.length).forEach(i -> folkMemory[i] = Arrays.copyOf(solutions[i], solutions[0].length));
        this.maxIteration = maxIter;
        FN = new FitnessFunction();
        avgFitness = new double[maxIteration];
    }

    /**
     * This method used to start the searching and optimization method for Binary Crow Search Algorithm
     */
    public void BCSAStartOptimization() {
        var StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            for (int solution = 0; solution < solutions.length; solution++) {
                if (new Random().nextDouble() >= AP) {
                    double uniformDistribution = new Random().nextDouble();
                    int randomCrow = new Random().nextInt(folkMemory.length);
                    AtomicInteger ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    solutions[solution] = super.GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(V -> TF.applyAsDouble(V + uniformDistribution + FL * (folkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else
                    // for CSA generate random solution
                    solutions[solution] = super.GuaranteeFeasibility(IntStream.range(0, solutions[0].length).map(j -> ThreadLocalRandom.current().nextInt(2)).asDoubleStream().toArray());
            }
            // define variable to store better solution fitness scores
            double sumScores = 0d;
            // update folk memory
            for (int i = 0; i < solutions.length; i++) {
                double newSolution = FN.EvaluateSingleSolution(solutions[i]);
                double oldSolution = FN.EvaluateSingleSolution(folkMemory[i]);

                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(solutions[i], 0, folkMemory[i], 0, folkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            // calculate the average fitness
            avgFitness[iter] = sumScores / solutions.length;
        }
        long EndTime = System.currentTimeMillis();
        optimizationTime = EndTime - StartTime;
        FN = new FitnessFunction(folkMemory);
    }

    /**
     * This method used to start the searching and optimization method for Improved Binary Search Algorithm
     */
    public void IBCSAStartOptimization() {
        // The mutation rate
        double mr = (double) 1 / solutions[0].length;
        long StartTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            for (int solution = 0 ; solution < solutions.length; solution++) {
                if (new Random().nextDouble() >= AP) {
                    double uniformDistribution = new Random().nextDouble();
                    int randomCrow = new Random().nextInt(folkMemory.length);
                    AtomicInteger ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    solutions[solution] = super.GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(V -> TF.applyAsDouble(V + uniformDistribution + FL * (folkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else
                    // for IBCSA
                    if (new Random().nextDouble() >= 0.5) {
                        // the following loop refers to the mutation operation
                        solutions[solution] = super.GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(V -> V = new Random().nextDouble() < mr ? 1 - V : V).toArray());
                    } else {
                        // the following loop refers to the lev'y flight term operation
                        AtomicInteger iteration = new AtomicInteger(iter);
                        solutions[solution] = super.GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(V -> TF.applyAsDouble(V + alpha * improvingMethod.Levy(iteration.get()))).toArray());
                    }
                // apply XOR and Single point crossover and select the best solution
                double[] x1 = improvingMethod.DoXor(solutions, FN);
                double[] x2 = improvingMethod.DoSinglePointCrossOver(solutions, FN);
                double[] Evolved = FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? x1 : x2;
                solutions[solution] = FN.EvaluateSingleSolution(solutions[solution]) > FN.EvaluateSingleSolution(Evolved) ? solutions[solution] : Evolved;
            }
            // define variable to store better solution fitness scores
            double sumScores = 0d;
            // update folk memory
            for (int i = 0; i < solutions.length; i++) {
                int newSolution = FN.EvaluateSingleSolution(solutions[i]);
                int oldSolution = FN.EvaluateSingleSolution(folkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(solutions[i], 0, folkMemory[i], 0, folkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            // calculate the average fitness
            avgFitness[iter] = sumScores / solutions.length;
        }
        var EndTime = System.currentTimeMillis();
        optimizationTime = EndTime - StartTime;
        FN = new FitnessFunction(folkMemory);
    }

    /**
     * This method used to start the searching and optimization method for IBCSA-BJO
     */
    public void IBCSA_BJOStartOptimization() {
        long startTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            for (int solution = 0; solution < solutions.length; solution++) {
                double Rv = new Random().nextDouble();
                if (Rv >= AP) {
                    double uniformDistribution = new Random().nextDouble();
                    int randomCrow = new Random().nextInt(folkMemory.length);
                    AtomicInteger ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    solutions[solution] = super.GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(V -> TF.applyAsDouble(V + uniformDistribution + FL * (folkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else {
                    // run Jaya optimizer to improve the exploration phase
                    if (iter % 100 == 0) {
                        jayaOptimizer.StartOptimization();
                        double[] jayaSolution = jayaOptimizer.getBestSolution();
                        if (FN.EvaluateSingleSolution(jayaSolution) > FN.EvaluateSingleSolution(solutions[solution]))
                            System.arraycopy(jayaSolution, 0, solutions[solution], 0, jayaSolution.length);
                    }
                }
                // apply XOR and Single point crossover and select the best solution
                    double[] x1 = improvingMethod.DoXor(solutions, FN);
                    double[] x2 = improvingMethod.DoSinglePointCrossOver(solutions, FN);
                    double[] Evolved = FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? x1 : x2;
                    solutions[solution] = FN.EvaluateSingleSolution(solutions[solution]) > FN.EvaluateSingleSolution(Evolved) ? solutions[solution] : Evolved;
            }
            // define variable to store better solution fitness scores
            double sumScores = 0D;
            // update folk memory
            for (int i = 0; i < solutions.length; i++) {
                int newSolution = FN.EvaluateSingleSolution(solutions[i]);
                int oldSolution = FN.EvaluateSingleSolution(folkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(solutions[i], 0, folkMemory[i], 0, folkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            if (iter % 100 == 0)
                jayaOptimizer.setSolutions(solutions);
            // calculate the average fitness
            avgFitness[iter] = sumScores / solutions.length;
        }
        long endTime = System.currentTimeMillis();
        optimizationTime = endTime - startTime;
        FN = new FitnessFunction(folkMemory);
    }

    /**
     * This method used to start the searching and optimization method for IBCSA-BCS
     */
    public void IBCSA_BCSStartOptimization() {
        // The mutation rate
        double mr = (double) 1 / solutions[0].length;
        long startTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            for (int solution = 0; solution < solutions.length; solution++) {
                double Rv = new Random().nextDouble();
                if (Rv >= AP) {
                    double uniformDistribution = new Random().nextDouble();
                    int randomCrow = new Random().nextInt(folkMemory.length);
                    AtomicInteger ii = new AtomicInteger(0);
                    // update each solution according to crow search algorithm
                    solutions[solution] = super.GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(V -> TF.applyAsDouble(V + uniformDistribution + FL * (folkMemory[randomCrow][ii.getAndAdd(1)]) - V)).toArray());
                } else {
                    // run Jaya optimizer to improve the exploration phase
                    if (iter % 100 == 0) {
                        cuckooSearch.StartOptimization();
                        double[] cuckooSolution = cuckooSearch.getBestSolution();
                        if (FN.EvaluateSingleSolution(cuckooSolution) > FN.EvaluateSingleSolution(solutions[solution]))
                            System.arraycopy(cuckooSolution, 0, solutions[solution], 0, cuckooSolution.length);
                    }
                }
                // apply mutation, XOR and Single point crossover and select the best solution
                double[] x3 = GuaranteeFeasibility(Arrays.stream(solutions[solution]).map(ii -> ThreadLocalRandom.current().nextDouble() < mr ? 1 - ii : ii).toArray());
                double[] x1 = improvingMethod.DoXor(solutions, FN);
                double[] x2 = improvingMethod.DoSinglePointCrossOver(solutions, FN);
                double[] Evolved = FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x3) ? x1 : x3 : x2;
                solutions[solution] = FN.EvaluateSingleSolution(solutions[solution]) > FN.EvaluateSingleSolution(Evolved) ? solutions[solution] : Evolved;
            }
            // define variable to store better solution fitness scores
            double sumScores = 0d;
            // update folk memory
            for (int i = 0; i < solutions.length; i++) {
                int newSolution = FN.EvaluateSingleSolution(solutions[i]);
                int oldSolution = FN.EvaluateSingleSolution(folkMemory[i]);
                if (newSolution > oldSolution) {
                    sumScores += newSolution;
                    System.arraycopy(solutions[i], 0, folkMemory[i], 0, folkMemory[0].length);
                }
                else sumScores += oldSolution;
            }
            if (iter % 100 == 0)
                cuckooSearch.setSolutions(solutions);
            // calculate the average fitness
            avgFitness[iter] = sumScores / solutions.length;
        }
        long endTime = System.currentTimeMillis();
        optimizationTime = endTime - startTime;
        FN = new FitnessFunction(folkMemory);
    }

    /**
     * This method used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    @Override
    public String  GetOptimizationTime() {
        return ((double) optimizationTime / 1000) + " s ";
    }

    /**
     * This method is used to get the worst current solution
     * @return the worst solutions among all ones
     */
    @Override
    public double[] getWorstSolution() {
        return FN.getWorstSolution();
    }

    /**
     * This method is used to get the worst current solution
     * @return the best solutions among all ones
     */
    @Override
    public double[] getBestSolution() {
        return FN.getBestSolution();
    }

    /**
     * This method is used to get the average fitness
     * @return the average fitness score of the current generation
     */
    @Override
    public Double[] getAvgFitnessSeries() {
        return Arrays.stream(avgFitness).boxed().toArray(Double[]::new);
    }

    /**
     * This method is used to display the performance summary
     * This method used to print the performance of the CSA optimizer
     */
    @Override
    public void DisplayPerformanceSummary() {
        FN.EvaluateAllSolution().ShowEvaluationSummary();
    }

    /**
     * This method is used to visualize the best current solution
     * This methode used to visualize the best solution
     */
    @Override
    public void DisplayBestSolution() {
        FN.DisplayBestSubGraph();
    }
}
