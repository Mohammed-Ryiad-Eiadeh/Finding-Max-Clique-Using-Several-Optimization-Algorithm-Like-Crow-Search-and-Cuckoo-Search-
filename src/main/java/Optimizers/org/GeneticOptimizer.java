package Optimizers.org;

import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;

import java.util.*;
import java.util.stream.IntStream;

public class GeneticOptimizer extends Feasibility {

    private final int SolutionsSizeForSelecting;
    private final double matingProbability;
    private final double mutationRate;
    private final double[][] Solutions;
    private final int MaxIteration;
    private FitnessFunction FN;
    private double[] worstSolution;
    private double[] bestSolution;
    private long OptimizationTime;
    private final double[] AvgFitness;
    private final List<chromosome_fitness_Container> listOfSolutionsAndFitness;

    /**
     * This contractor is used to instantiate Crow Search Optimizer with cuckoo optimizer
     * @param AdjMatrix referred to a given graph as adjacency matrix
     * @param SetOfSolution referred to the generated population
     * @param SelectionRatio the ratio of how many solutions to be selected for next iteration
     * @param MatingProbability the ratio of chromosome for crossover and reproduction
     * @param mutationProbability the ratio of whether mutate genes in chromosome or not
     * @param MaxIter referred to the maximum iteration
     */
    public GeneticOptimizer(int[][] AdjMatrix, double[][] SetOfSolution, double SelectionRatio, double MatingProbability, double mutationProbability, int MaxIter) {
        super(AdjMatrix);
        this.Solutions = new double[SetOfSolution.length][SetOfSolution[0].length];
        IntStream.range(0, SetOfSolution.length).forEach(i -> Solutions[i] = Arrays.copyOf(SetOfSolution[i], SetOfSolution[0].length));
        FN = new FitnessFunction();
        listOfSolutionsAndFitness = new ArrayList<>();
        if (SelectionRatio < 0) throw new IllegalStateException("selection ratio can not be negative : " + SelectionRatio);
        this.SolutionsSizeForSelecting = (int) (SelectionRatio * Solutions.length);
        this.matingProbability = MatingProbability;
        this.mutationRate = mutationProbability;
        this.MaxIteration = MaxIter;
        this.AvgFitness = new double[MaxIter];
    }

    /**
     * This method used to start the searching and optimization method for Genetic Algorithm
     */
    public void StartOptimization() {
        var StartTime = System.currentTimeMillis();
        for (var iter = 0; iter < MaxIteration; iter++) {
            // apply selection
            Selectin_based_roulette_wheel();
            // apply crossover
            Crossover_1Point_Based();
            // apply mutation
            Mutation();

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
     * apply roulette wheel method in order to select the best amount of solutions based on some proportion
     */
    private void Selectin_based_roulette_wheel() {
        for (var Solution : Solutions)
            listOfSolutionsAndFitness.add(new chromosome_fitness_Container(Solution, FN.EvaluateSingleSolution(Solution)));

        listOfSolutionsAndFitness.sort(Comparator.comparing(chromosome_fitness_Container::FitnessScore).reversed());

        for (var i = 0; i < SolutionsSizeForSelecting; i++)
            System.arraycopy(listOfSolutionsAndFitness.get(i).Solution, 0, Solutions[i], 0, Solutions[0].length);

        for (var i = SolutionsSizeForSelecting + 1; i < Solutions.length; i++)
            System.arraycopy(listOfSolutionsAndFitness.get(new Random().nextInt(SolutionsSizeForSelecting)).Solution, 0, Solutions[i], 0, Solutions[0].length);

        listOfSolutionsAndFitness.clear();
    }

    /**
     * apply 1-point crossover for each pair of solutions as matting one of the buttom woth one from the top
     */
    private void Crossover_1Point_Based() {
        for (var i = 0; i < Solutions.length / 2; i++) {
            if (new Random().nextDouble() > matingProbability) {
                var parent1 = Arrays.copyOf(Solutions[Solutions.length - i - 1], Solutions[0].length);
                var parent2 = Arrays.copyOf(Solutions[i], Solutions[0].length);

                var offspring1 = new double[parent1.length];
                var offspring2 = new double[parent2.length];

                var CrossPoint = new Random().nextInt(parent1.length);
                var holder1 = new double[CrossPoint];
                var holder2 = new double[parent1.length - holder1.length];

                System.arraycopy(parent1, 0, holder1, 0, holder1.length);
                System.arraycopy(parent2, 0, offspring1, 0, holder1.length);
                System.arraycopy(holder1, 0, offspring2, 0, holder1.length);
                System.arraycopy(parent1, holder1.length, holder2, 0, holder2.length);
                System.arraycopy(parent2, holder1.length, offspring2, 0, holder2.length);
                System.arraycopy(holder2, 0, offspring1, holder1.length, holder2.length);

                var feasible1 = GuaranteeFeasibility(offspring1);
                var feasible2 = GuaranteeFeasibility(offspring2);

                Solutions[Solutions.length - i - 1] = FN.EvaluateSingleSolution(feasible1) > FN.EvaluateSingleSolution(parent1) ? feasible1 : parent1;
                Solutions[i] = FN.EvaluateSingleSolution(feasible2) > FN.EvaluateSingleSolution(parent2) ? feasible2 : parent2;
            }
        }
    }

    /**
     * apply mutation for each chromosome on each gene based on a certain probability
     */
    private void Mutation() {
        for (var i = 0; i < Solutions.length; i++) {
            var MutedSolution = super.GuaranteeFeasibility(Arrays.stream(Solutions[i]).map(x -> new Random().nextDouble() < mutationRate ? 1 - x : x).toArray());
            if (FN.EvaluateSingleSolution(MutedSolution) > FN.EvaluateSingleSolution(Solutions[i]))
                Solutions[i] = MutedSolution;
        }
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

    record chromosome_fitness_Container(double[] Solution, double FitnessScore) { }
}

