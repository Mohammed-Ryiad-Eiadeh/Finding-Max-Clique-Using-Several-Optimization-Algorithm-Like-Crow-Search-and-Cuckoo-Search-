package Population.org;

import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**<li>generate population with OBL</li>*/

public final class Population extends Feasibility {

    private final int popSize;
    private final double[][] population;
    private final FitnessFunction FN;

    /**
     * The constructor of the population
     * @param adjMatrix refers to the adjacency matrix
     * @param noSolution refers to the id of a solution
     */
    public Population(int[][] adjMatrix, int noSolution) {
        super(adjMatrix);
        this.popSize = noSolution;
        this.population = new double[popSize][adjMatrix[0].length];
        this.FN = new FitnessFunction();
    }

    /**
     * Constructs the initial population
     * @param adjMatrix refers to the adjacency matrix
     */
    public Population(int[][] adjMatrix) {
        super(adjMatrix);
        // set the number of the initial set of solutions to 50
        this.popSize = 50;
        this.population = new double[popSize][adjMatrix[0].length];
        this.FN = new FitnessFunction();
    }

    /**
     * This method used to generate the initial solutions of the problem
     */
    public double[][] GeneratePopulation() {
        for (double[] ints : population) {
            double[] CurrentSolution = new Random().ints(population[0].length, 0, 2).asDoubleStream().parallel().toArray();
            double[] Feasible = GuaranteeFeasibility(CurrentSolution);
            System.arraycopy(Feasible, 0, ints, 0, population[0].length);
        }
        return population;
    }

    /** This method used to improve the initial solutions by Opposition Based Learning (OBL) mechanism
     * @return returns the improved population by OBL
     */
    public Population OppositionBasedLearning() {
            double[] Opposite;
            double[] feasible;
            for (double[] doubles : population) {
                Opposite = Arrays.stream(doubles).map(ii -> 1 - ii).parallel().toArray();
                feasible = GuaranteeFeasibility(Opposite);
                if (FN.EvaluateSingleSolution(feasible) > FN.EvaluateSingleSolution(doubles))
                    System.arraycopy(feasible, 0, doubles, 0, feasible.length);
            }
        return this;
    }

    /**
     * @return The size of the initial population
     */
    public int Size() {
        return popSize;
    }

    /**
     * This method used to print the population
     */
    public void ShowPopulation() {
        System.out.println("\nThe initial population is :");
        for (int i = 0; i < population.length; i++) {
            System.out.print("X" + i + "\t\t\t");
            System.out.println(Arrays.stream(population[i]).mapToObj(String::valueOf).parallel().collect(Collectors.joining("\t\t", "[ ", " ]\n")));
        }
        System.out.println();
    }
}
