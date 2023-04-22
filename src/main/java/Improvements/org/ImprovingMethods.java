package Improvements.org;

import Feasibility.org.Feasibility;
import Fitness.org.FitnessFunction;

import java.util.Arrays;
import java.util.Random;

public final class ImprovingMethods extends Feasibility {

    private double lambda;

    /**
     * a Constructor to create objects to use the improving method
     * @param adjMatrix the representation of the graph as matrix
     */
    public ImprovingMethods(int[][] adjMatrix) {
        super(adjMatrix);
    }

    /**
     * a Constructor to create objects to use the improving method
     * @param adjMatrix the representation of the graph as matrix
     * @param lambda refers to the value that can be used in the Lévy flight term
     */
    public ImprovingMethods(int[][] adjMatrix, double lambda) {
        super(adjMatrix);
        this.lambda = lambda;
    }

    /**
     * This method used to calculate the Lévy flight term
     * @param currentIteration referred to the current generation id
     * @return returns the result of Lévy flight function
     */
    public double Levy(int currentIteration) {
        return Math.pow(currentIteration + 1, - lambda);
    }

    /**
     * @param solutions The current set of solutions
     * @param FN The object from class FitnessFunction
     * @return The offspring that is produced by applying Xor operation on randomly selected two parents
     */
    public double[] DoXor(double[][] solutions, FitnessFunction FN) {
        var parent01 = Arrays.copyOf(solutions[new Random().nextInt(solutions.length)], solutions[0].length);
        var parent02 = Arrays.copyOf(solutions[new Random().nextInt(solutions.length)], solutions[0].length);

        var offspring = new double[parent01.length];

        for (var i = 0; i < parent01.length ; i++)
            offspring[i] = parent01[i] == parent02[i] ? 0.0 : 1.0;

        var FeasibleOffspring = GuaranteeFeasibility(offspring);
        var OffspringFit = FN.EvaluateSingleSolution(FeasibleOffspring);

        if (OffspringFit < FN.EvaluateSingleSolution(parent01))
            FeasibleOffspring = parent01;

        if (OffspringFit < FN.EvaluateSingleSolution(parent02))
            FeasibleOffspring = parent02;

        return FeasibleOffspring;
    }

    /**
     * @return the best offspring that is produced by applying single point crossover on randomly selected two parents
     */
    public double[] DoSinglePointCrossOver(double[][] Solutions, FitnessFunction FN) {
        var parent1 = new double[Solutions[0].length];
        System.arraycopy(Solutions[new Random().nextInt(Solutions.length)], 0, parent1, 0, parent1.length);
        var parent2 = new double[Solutions[0].length];
        System.arraycopy(Solutions[new Random().nextInt(Solutions.length)], 0, parent2, 0, parent2.length);

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

        var x1 = FN.EvaluateSingleSolution(feasible1) > FN.EvaluateSingleSolution(parent1) ? feasible1 : parent1;
        var x2 = FN.EvaluateSingleSolution(feasible2) > FN.EvaluateSingleSolution(parent2) ? feasible2 : parent2;

        return FN.EvaluateSingleSolution(x1) > FN.EvaluateSingleSolution(x2) ? x1 : x2;
    }
}
