package Population.org;

import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**<li>generate population with OBL</li>*/

public final class Population extends Feasibility {

    private final int PopSize;
    private final double[][] Population;
    private final FitnessFunction FN;

    /**
     * default constructor of the population
     * @param AdjMatrix refers to the adjacency matrix
     * @param NoSolution refers to the id of a solution
     */
    public Population(int[][] AdjMatrix, int NoSolution) {
        super(AdjMatrix);
        this.PopSize = NoSolution;
        this.Population = new double[PopSize][AdjMatrix[0].length];
        this.FN = new FitnessFunction();
    }

    /**
     * @param AdjMatrix refers to the adjacency matrix
     */
    public Population(int[][] AdjMatrix) {
        super(AdjMatrix);
        // set the number of the initial set of solutions to 50
        this.PopSize = 50;
        this.Population = new double[PopSize][AdjMatrix[0].length];
        this.FN = new FitnessFunction();
    }

    /**
     * This method used to generate the initial solutions of the problem
     */
    public double[][] GeneratePopulation() {
        for (double[] ints : Population) {
            var CurrentSolution = new Random().ints(Population[0].length, 0, 2).asDoubleStream().parallel().toArray();
            var Feasible = GuaranteeFeasibility(CurrentSolution);
            System.arraycopy(Feasible, 0, ints, 0, Population[0].length);
        }
        return Population;
    }

    /** This method used to improve the initial solutions by Opposition Based Learning (OBL) mechanism
     * @return returns the improved population by OBL
     */
    public Population OppositionBasedLearning() {
            var Opposite = new double[Population[0].length];
            var feasible = new double[Population[0].length];
            for (var doubles : Population) {
                Opposite = Arrays.stream(doubles).map(ii -> 1 - ii).parallel().toArray();
                feasible = GuaranteeFeasibility(Opposite);
                if (FN.EvaluateSingleSolution(feasible) > FN.EvaluateSingleSolution(doubles))
                    System.arraycopy(feasible, 0, doubles, 0, feasible.length);
            }
        return this;
    }

    public int Size() {
        return PopSize;
    }

    /**
     * This method used to print the population
     */
    public void ShowPopulation() {
        System.out.println("\nThe initial population is :");
        for (var i = 0; i < Population.length; i++) {
            System.out.print("X" + i + "\t\t\t");
            System.out.println(Arrays.stream(Population[i]).mapToObj(String::valueOf).parallel().collect(Collectors.joining("\t\t", "[ ", " ]\n")));
        }
        System.out.println();
    }
}
