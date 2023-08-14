package Fitness.org;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** <li>fitness function entity to evaluate the solutions</li> */

public final class FitnessFunction {

    private double[][] solutions;
    private int maxFitness;
    private int minFitness;
    private int avgFitness;
    private int bestSolutionId = -1;
    private int worstSolutionId = -1;
    private SingleGraph graphVisualizer;

    /**
     * Constructor of fitness with one parameter
     * @param solutions refer to N-solutions
     */
    public FitnessFunction(double[][] solutions) {
        this.solutions = solutions;
        System.setProperty("org.graphstream.ui", "swing");
        this.graphVisualizer = new SingleGraph("Graph");
    }

    /**
     * default constructor of fitness
     */
    public FitnessFunction() { }

    /**
     * This method used to evaluate all solutions at once
     */
    public FitnessFunction EvaluateAllSolution() {
        avgFitness = 0;
        List<Integer> FitnessHolder = new ArrayList<>();
        int FNSum = 0;
        for (double[] solution : solutions) {
            FNSum = 0;
            for (double ii : solution) {
                if (ii == 1)
                    FNSum++;
            }
            avgFitness += FNSum;
            FitnessHolder.add(FNSum);
        }
        maxFitness = Collections.max(FitnessHolder);
        minFitness = Collections.min(FitnessHolder);
        for (int i = 0; i < FitnessHolder.size(); i++) {
            if (FitnessHolder.get(i) == maxFitness)
                bestSolutionId = i;
            if (FitnessHolder.get(i) == minFitness)
                worstSolutionId = i;
        }
        return this;
    }

    /**
     * @return the worst solutions among all ones
     */
    public double[] getWorstSolution() {
        if (worstSolutionId == -1)
            throw new IllegalStateException("you have to evaluate all solutions first");
        else
            return solutions[worstSolutionId];
    }

    /**
     * @return the best solutions among all ones
     */
    public double[] getBestSolution() {
        if (bestSolutionId == -1)
            throw new IllegalStateException("you have to evaluate all solutions first");
        else
            return solutions[bestSolutionId];
    }

    /**
     * @return the average fitness score
     */
    public double getAVG() {
        return avgFitness;
    }

    /**
     * This method used to evaluate a specific solution
     * @param solution referred to a specific solution
     * @return the value of the fitness of the given solution
     */
    public int EvaluateSingleSolution(double[] solution) {
        int FNSum = 0;
        for (double i : solution)
            if (i == 1)
                FNSum++;
        return FNSum;
    }

    /**
     * This method print the performance summary of the CSA optimizer
     */
    public void ShowEvaluationSummary() {
        System.out.printf("Maximum fitness is : %s\nMinimum fitness is : %s\nAverage fitness is : %s",
                maxFitness, minFitness, avgFitness / (double) solutions.length);
        System.out.print("\nThe best current solution contains these vertices : ");
        for (int i = 0; i < solutions[bestSolutionId].length; i++)
            if (solutions[bestSolutionId][i] == 1)
                System.out.print(i + 1 + "\t");
        System.out.print("\nThe worst current solution contains these vertices : ");
        for (int i = 0; i < solutions[worstSolutionId].length; i++)
            if (solutions[worstSolutionId][i] == 1)
                System.out.print(i + 1 + "\t");
        System.out.println("\n");
    }

    /**
     * This method used to visualize the best solution achieved by the CSA optimizer
     */
    public void DisplayBestSubGraph() {
        int counter = 0;
        for (double i : solutions[bestSolutionId]) {
            if (i == 1)
                counter++;
        }
        if (counter < 200){
            double[] SubGraph = solutions[bestSolutionId];
            List<Integer> ExistedNodes = new ArrayList<>();
            for (int i = 0; i <= SubGraph.length - 1; i++)
                if (SubGraph[i] == 1) {
                    ExistedNodes.add(i + 1);
                    graphVisualizer.addNode(String.valueOf(i + 1));
                }
            for (int i = 0; i < ExistedNodes.size(); i++)
                for (int ii = i + 1; ii < ExistedNodes.size(); ii++)
                    graphVisualizer.addEdge(i + "" + ii + new Random().nextDouble(Double.MAX_VALUE), String.valueOf(ExistedNodes.get(i)), String.valueOf(ExistedNodes.get(ii)));
            graphVisualizer.display();
        } else
            System.out.println("The solution hase more that 200 nodes, it will not be displayed");
    }
}
