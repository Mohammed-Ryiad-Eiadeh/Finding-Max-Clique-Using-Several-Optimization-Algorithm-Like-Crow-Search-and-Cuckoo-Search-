package Optimizers.org;

import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;
import Optimizers.org.CommomMethods.CommonFunctions;

import java.util.*;
import java.util.stream.IntStream;

public class GeneticOptimizer extends Feasibility
        implements CommonFunctions {

    private final int solutionsSizeForSelecting;
    private final double matingProbability;
    private final double mutationRate;
    private final double[][] solutions;
    private final int maxIteration;
    private FitnessFunction FN;
    private double[] worstSolution;
    private double[] bestSolution;
    private long optimizationTime;
    private final double[] avgFitness;
    private final List<chromosome_fitness_Container> listOfSolutionsAndFitness;

    /**
     * This contractor is used to instantiate Crow Search Optimizer with cuckoo optimizer
     * @param adjMatrix referred to a given graph as adjacency matrix
     * @param setOfSolution referred to the generated population
     * @param selectionRatio the ratio of how many solutions to be selected for next iteration
     * @param matingProbability the ratio of chromosome for crossover and reproduction
     * @param mutationProbability the ratio of whether mutate genes in chromosome or not
     * @param maxIter referred to the maximum iteration
     */
    public GeneticOptimizer(int[][] adjMatrix, double[][] setOfSolution, double selectionRatio, double matingProbability, double mutationProbability, int maxIter) {
        super(adjMatrix);
        this.solutions = new double[setOfSolution.length][setOfSolution[0].length];
        IntStream.range(0, setOfSolution.length).forEach(i -> solutions[i] = Arrays.copyOf(setOfSolution[i], setOfSolution[0].length));
        FN = new FitnessFunction();
        listOfSolutionsAndFitness = new ArrayList<>();
        if (selectionRatio < 0) throw new IllegalStateException("selection ratio can not be negative : " + selectionRatio);
        this.solutionsSizeForSelecting = (int) (selectionRatio * solutions.length);
        this.matingProbability = matingProbability;
        this.mutationRate = mutationProbability;
        this.maxIteration = maxIter;
        this.avgFitness = new double[maxIter];
    }

    /**
     * This method is used to start the searching and optimization method for Genetic Algorithm
     */
    public void StartOptimization() {
        long startTime = System.currentTimeMillis();
        for (int iter = 0; iter < maxIteration; iter++) {
            // apply selection
            Selectin_based_roulette_wheel();
            // apply crossover
            Crossover_1Point_Based();
            // apply mutation
            Mutation();

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
     * Applys roulette wheel method in order to select the best amount of solutions based on some proportion
     */
    private void Selectin_based_roulette_wheel() {
        for (double[] solution : solutions)
            listOfSolutionsAndFitness.add(new chromosome_fitness_Container(solution, FN.EvaluateSingleSolution(solution)));

        listOfSolutionsAndFitness.sort(Comparator.comparing(chromosome_fitness_Container::FitnessScore).reversed());

        for (var i = 0; i < solutionsSizeForSelecting; i++)
            System.arraycopy(listOfSolutionsAndFitness.get(i).Solution, 0, solutions[i], 0, solutions[0].length);

        for (var i = solutionsSizeForSelecting + 1; i < solutions.length; i++)
            System.arraycopy(listOfSolutionsAndFitness.get(new Random().nextInt(solutionsSizeForSelecting)).Solution, 0, solutions[i], 0, solutions[0].length);

        listOfSolutionsAndFitness.clear();
    }

    /**
     * Applys 1-point crossover for each pair of solutions as matting one of the buttom woth one from the top
     */
    private void Crossover_1Point_Based() {
        for (int i = 0; i < solutions.length / 2; i++) {
            if (new Random().nextDouble() > matingProbability) {
                double[] parent1 = Arrays.copyOf(solutions[solutions.length - i - 1], solutions[0].length);
                double[] parent2 = Arrays.copyOf(solutions[i], solutions[0].length);

                double[] offspring1 = new double[parent1.length];
                double[] offspring2 = new double[parent2.length];

                int crossPoint = new Random().nextInt(parent1.length);
                double[] holder1 = new double[crossPoint];
                double[] holder2 = new double[parent1.length - holder1.length];

                System.arraycopy(parent1, 0, holder1, 0, holder1.length);
                System.arraycopy(parent2, 0, offspring1, 0, holder1.length);
                System.arraycopy(holder1, 0, offspring2, 0, holder1.length);
                System.arraycopy(parent1, holder1.length, holder2, 0, holder2.length);
                System.arraycopy(parent2, holder1.length, offspring2, 0, holder2.length);
                System.arraycopy(holder2, 0, offspring1, holder1.length, holder2.length);

                double[] feasible1 = GuaranteeFeasibility(offspring1);
                double[] feasible2 = GuaranteeFeasibility(offspring2);

                solutions[solutions.length - i - 1] = FN.EvaluateSingleSolution(feasible1) > FN.EvaluateSingleSolution(parent1) ? feasible1 : parent1;
                solutions[i] = FN.EvaluateSingleSolution(feasible2) > FN.EvaluateSingleSolution(parent2) ? feasible2 : parent2;
            }
        }
    }

    /**
     * Applys mutation for each chromosome on each gene based on a certain probability
     */
    private void Mutation() {
        for (int i = 0; i < solutions.length; i++) {
            double[] MutedSolution = super.GuaranteeFeasibility(Arrays.stream(solutions[i]).map(x -> new Random().nextDouble() < mutationRate ? 1 - x : x).toArray());

            if (FN.EvaluateSingleSolution(MutedSolution) > FN.EvaluateSingleSolution(solutions[i]))
                solutions[i] = MutedSolution;
        }
    }

    /**
     * This method is used to return the spent time during optimization process
     * @return return the duration time of the optimization process
     */
    @Override
    public String  GetOptimizationTime() {
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
     * This method is used to return the average fitness score of current generation
     * @return the average fitness score of the current generation
     */
    @Override
    public Double[] getAvgFitnessSeries() {
        return Arrays.stream(avgFitness).boxed().toArray(Double[]::new);
    }

    /**
     * This method is used to print the performance of the CSA optimizer
     */
    @Override
    public void DisplayPerformanceSummary() {
        FN.EvaluateAllSolution().ShowEvaluationSummary();
    }

    /**
     * This methode is used to visualize the best solution
     */
    @Override
    public void DisplayBestSolution() {
        FN.DisplayBestSubGraph();
    }


    /**
     * This class is used as a container for vectors seolutions and their fitness scores
     */
    record chromosome_fitness_Container(double[] Solution, double FitnessScore) { }
}

