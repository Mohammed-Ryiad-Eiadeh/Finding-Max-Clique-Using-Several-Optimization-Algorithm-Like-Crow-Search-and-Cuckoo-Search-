package Fitness.org;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** <li>fitness function entity to evaluate the solutions</li> */

public final class FitnessFunction {

    private double[][] Solutions;
    private int MaxFitness;
    private int MinFitness;
    private int AvgFitness;
    private int BestSolutionId = -1;
    private int WorstSolutionId = -1;
    private SingleGraph GraphVisualizer;

    /**
     * Constructor of fitness with one parameter
     * @param Solutions refer to N-solutions
     */
    public FitnessFunction(double[][] Solutions) {
        this.Solutions = Solutions;
        System.setProperty("org.graphstream.ui", "swing");
        this.GraphVisualizer = new SingleGraph("Graph");
    }

    /**
     * default constructor of fitness
     */
    public FitnessFunction() {}

    /**
     * This method used to evaluate all solutions at once
     */
    public FitnessFunction EvaluateAllSolution() {
        AvgFitness = 0;
        var FitnessHolder = new ArrayList<Integer>();
        var FNSum = 0;
        for (double[] solution : Solutions) {
            FNSum = 0;
            for (var ii : solution) {
                if (ii == 1)
                    FNSum++;
            }
            AvgFitness += FNSum;
            FitnessHolder.add(FNSum);
        }
        MaxFitness = Collections.max(FitnessHolder);
        MinFitness = Collections.min(FitnessHolder);
        for (var i = 0; i < FitnessHolder.size(); i++) {
            if (FitnessHolder.get(i) == MaxFitness)
                BestSolutionId = i;
            if (FitnessHolder.get(i) == MinFitness)
                WorstSolutionId = i;
        }
        return this;
    }

    /**
     * @return the worst solutions among all ones
     */
    public double[] getWorstSolution() {
        if (WorstSolutionId == -1)
            throw new IllegalStateException("you have to evaluate all solutions first");
        else
            return Solutions[WorstSolutionId];
    }

    /**
     * @return the best solutions among all ones
     */
    public double[] getBestSolution() {
        if (BestSolutionId == -1)
            throw new IllegalStateException("you have to evaluate all solutions first");
        else
            return Solutions[BestSolutionId];
    }

    /**
     * @return the average fitness score
     */
    public double getAVG() {
        return AvgFitness;
    }

    /**
     * This method used to evaluate a specific solution
     * @param Solution referred to a specific solution
     * @return the value of the fitness of the given solution
     */
    public int EvaluateSingleSolution(double[] Solution) {
        var FNSum = 0;
        for (var i : Solution)
            if (i == 1)
                FNSum++;
        return FNSum;
    }

    /**
     * This method print the performance summary of the CSA optimizer
     */
    public void ShowEvaluationSummary() {
        System.out.printf("Maximum fitness is : %s\nMinimum fitness is : %s\nAverage fitness is : %s",
                MaxFitness, MinFitness, AvgFitness / (double) Solutions.length);

        System.out.print("\nThe best current solution contains these vertices : ");
        for (var i = 0; i < Solutions[BestSolutionId].length; i++)
            if (Solutions[BestSolutionId][i] == 1)
                System.out.print(i + 1 + "\t");

        System.out.print("\nThe worst current solution contains these vertices : ");
        for (var i = 0; i < Solutions[WorstSolutionId].length; i++)
            if (Solutions[WorstSolutionId][i] == 1)
                System.out.print(i + 1 + "\t");

        System.out.println("\n");
    }

    /**
     * This method used to visualize the best solution achieved by the CSA optimizer
     */
    public void DisplayBestSubGraph() {
        var counter = 0;
        for (var i : Solutions[BestSolutionId]) {
            if (i == 1)
                counter++;
        }

        if (counter < 200){
            var SubGraph = Solutions[BestSolutionId];
            var ExistedNodes = new ArrayList<Integer>();
            for (var i = 0; i <= SubGraph.length - 1; i++)
                if (SubGraph[i] == 1) {
                    ExistedNodes.add(i + 1);
                    GraphVisualizer.addNode(String.valueOf(i + 1));
                }
            for (var i = 0; i < ExistedNodes.size(); i++)
                for (var ii = i + 1; ii < ExistedNodes.size(); ii++)
                    GraphVisualizer.addEdge(i + "" + ii + new Random().nextDouble(Double.MAX_VALUE), String.valueOf(ExistedNodes.get(i)), String.valueOf(ExistedNodes.get(ii)));
            GraphVisualizer.display();
        } else
            System.out.println("The solution hase more that 200 nodes, it will not be displayed");
    }
}
