package MainClass.org;

import Curves.org.Plot;
import Data.org.DataSet;
import Data.org.Graph;
import Discreet.org.TransferFunction;
import Fitness.org.FitnessFunction;
import Optimizers.org.CrowOptimizer;
import Optimizers.org.CuckooOptimizer;
import Optimizers.org.GeneticOptimizer;
import Optimizers.org.JayaOptimizer;
import Population.org.Population;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.out;

/**
 * <li>the main class</li>
 * */
public class Main {
    public static void main(String[] args) {
        // read the dataset, display the graph,and construct the graph
        var graph = new Graph(DataSet.GetDataSet(1));
        graph.ShowGraph();
        var HoldGraph = graph.GetGraph();

        out.println(Arrays.deepToString(HoldGraph));

        // generate the initial population with opposition based learning,
        // and convert infeasible solution to feasible one
        var population = new Population(HoldGraph, 100);
        var InitialSolutions = population.OppositionBasedLearning().GeneratePopulation();

        // evaluate the initial population and display the summary
        out.println("The evaluation summary of the initial population :");
        var FN = new FitnessFunction(InitialSolutions);
        FN.EvaluateAllSolution().ShowEvaluationSummary();

        // define the maximum iteration
        var MaxIteration = 1000;

        var GeneticOptimizer = new GeneticOptimizer(HoldGraph,
                InitialSolutions,
                0.8,
                0.7,
                0.2,
                MaxIteration);

        // Start the optimization process
        GeneticOptimizer.StartOptimization();

        // display the performance of Genetic algorithm
        out.println("The evaluation summary of the genetic optimizer :" + "\n" +
                "Optimization process took : " + GeneticOptimizer.GetOptimizationTime());
        GeneticOptimizer.DisplayPerformanceSummary();
        // GeneticOptimizer.DisplayBestSolution();


        // use the cuckoo optimizer to find the maximum clique
        var CuckooOptimizer = new CuckooOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                2,
                2,
                0.1,
                1.5,
                MaxIteration);

        // Start the optimization process
        CuckooOptimizer.StartOptimization();

        // display the performance of Cuckoo algorithm
        out.println("The evaluation summary of the cuckoo optimizer :" + "\n" +
                "Optimization process took : " + CuckooOptimizer.GetOptimizationTime());
        CuckooOptimizer.DisplayPerformanceSummary();
        // CuckooOptimizer.DisplayBestSolution();


        // use the crow optimizer to find the maximum clique
        var CrowOptimizer = new CrowOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                2,
                0.1,
                MaxIteration);

        // Start the optimization process
        CrowOptimizer.BCSAStartOptimization();

        // display the performance of CSA algorithm
        out.println("The evaluation summary of the crow optimizer :" + "\n" +
                "Optimization process took : " + CrowOptimizer.GetOptimizationTime());
        CrowOptimizer.DisplayPerformanceSummary();
        // CrowOptimizer.DisplayBestSolution();


        // use the Jaya optimizer to find the maximum clique
        var JayaOptimizer = new JayaOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                FN.getWorstSolution(),
                FN.getBestSolution(),
                MaxIteration);

        // Start the optimization process
        JayaOptimizer.StartOptimization();

        // display the performance of Jaya algorithm
        out.println("The evaluation summary of the jaya optimizer :" + "\n" +
                "Optimization process took : " + JayaOptimizer.GetOptimizationTime());
        JayaOptimizer.DisplayPerformanceSummary();
        // JayaOptimizer.DisplayBestSolution();


        // use the enhanced crow optimizer to find the maximum clique
        var ImprovedCrowOptimizer = new CrowOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                2,
                0.1,
                1.5,
                2,
                MaxIteration);

        // Start the optimization process
        ImprovedCrowOptimizer.IBCSAStartOptimization();

        // display the performance of IBCSA algorithm
        out.println("The evaluation summary of the improved crow optimizer :" + "\n" +
                "Optimization process took : " + ImprovedCrowOptimizer.GetOptimizationTime());
        ImprovedCrowOptimizer.DisplayPerformanceSummary();
        // ImprovedCrowOptimizer.DisplayBestSolution();


        // use the Hybrid IBCSA-JAYA optimizer to find the maximum clique
        var CrowJayaOptimizer = new CrowOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                2,
                0.1,
                2,
                2,
                new JayaOptimizer(HoldGraph,
                        InitialSolutions,
                        TransferFunction.V2,
                        FN.getWorstSolution(),
                        FN.getBestSolution(),
                        100),
                MaxIteration);

        // Start the optimization process
        CrowJayaOptimizer.IBCSA_BJOStartOptimization();

        // display the performance of IBCSA-BJO algorithm
        out.println("The evaluation summary of the IBCSA-BJAYA optimizer :" + "\n" +
                "Optimization process took : " + CrowJayaOptimizer.GetOptimizationTime());
        CrowJayaOptimizer.DisplayPerformanceSummary();
        // CrowJayaOptimizer.DisplayBestSolution();


        // use the Hybrid IBCSA-BCS optimizer
        var CrowCuckooOptimizer = new CrowOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                2,
                0.1,
                2,
                new CuckooOptimizer(HoldGraph,
                        InitialSolutions,
                        TransferFunction.V2,
                        2,
                        2,
                        0.1,
                        1.5,
                        100),
                MaxIteration);

        // Start the optimization process
        CrowCuckooOptimizer.IBCSA_BCSStartOptimization();

        // display the performance of IBCSA-BCS algorithm
        out.println("The evaluation summary of the IBCSA-BCA optimizer :" + "\n" +
                "Optimization process took : " + CrowCuckooOptimizer.GetOptimizationTime());
        CrowCuckooOptimizer.DisplayPerformanceSummary();
        // display the solution based the IBCSA-BCS algorithm
        // CrowCuckooOptimizer.DisplayBestSolution();


        // use the Hybrid IBCSA-BGA optimizer
        var CrowGeneticOptimizer = new CrowOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                2,
                0.1,
                2,
                new GeneticOptimizer(HoldGraph,
                        InitialSolutions,
                        0.8,
                        0.7,
                        0.2,
                        100),
                MaxIteration);

        // Start the optimization process
        CrowGeneticOptimizer.IBCSA_BGAStartOptimization();

        // display the performance of IBCSA-BGA algorithm
        out.println("The evaluation summary of the IBCSA-BGA optimizer :" + "\n" +
                "Optimization process took : " + CrowGeneticOptimizer.GetOptimizationTime());
        CrowGeneticOptimizer.DisplayPerformanceSummary();
        // display the solution based the IBCSA-BGA algorithm
        // CrowGeneticOptimizer.DisplayBestSolution();
        System.gc();

        // Plot the IBCrow performance
        var ConvergenceCurves = new ArrayList<Double[]>() {{
            add(CuckooOptimizer.getAvgFitnessSeries());
            add(CrowOptimizer.getAvgFitnessSeries());
            add(ImprovedCrowOptimizer.getAvgFitnessSeries());
            add(JayaOptimizer.getAvgFitnessSeries());
            add(CrowJayaOptimizer.getAvgFitnessSeries());
            add(CrowCuckooOptimizer.getAvgFitnessSeries());
            add(GeneticOptimizer.getAvgFitnessSeries());
            add(CrowGeneticOptimizer.getAvgFitnessSeries());
        }};
        new Plot(MaxIteration, ConvergenceCurves, FN.getAVG() / population.Size()).show();
    }
}
