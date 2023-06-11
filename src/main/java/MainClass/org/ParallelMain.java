package MainClass.org;

import Data.org.DataSet;
import Data.org.Graph;
import Discreet.org.TransferFunction;
import Fitness.org.FitnessFunction;
import Optimizers.org.CrowOptimizer;
import Optimizers.org.CuckooOptimizer;
import Optimizers.org.GeneticOptimizer;
import Optimizers.org.JayaOptimizer;
import Population.org.Population;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.out;

public class ParallelMain {
    public static void main(String ...a) throws InterruptedException, ExecutionException {
        // read the dataset, display the graph,and construct the graph
        var graph = new Graph(DataSet.GetDataSet(19));
        graph.ShowGraph();
        var HoldGraph = graph.GetGraph();

        // generate the initial population with opposition based learning,
        // and convert infeasible solution to feasible one
        var InitialSolutions = new Population(HoldGraph, 100)
                .OppositionBasedLearning()
                .GeneratePopulation();

        // evaluate the initial population and display the summary
        out.println("The evaluation summary of the initial population :");
        var FN = new FitnessFunction(InitialSolutions);
        FN.EvaluateAllSolution();
        FN.ShowEvaluationSummary();

        // define the maximum iteration
        var MaxIter = 1000;

        Callable<String> C1 = () -> {
            // use the GA optimizer to find the maximum clique
            var GA = new GeneticOptimizer(HoldGraph,
                    InitialSolutions,
                    0.8,
                    0.7,
                    0.2,
                    MaxIter);

            // Start the optimization process
            GA.StartOptimization();

            // display the performance of Genetic algorithm
            out.println("The evaluation summary of the genetic optimizer :" + "\n" +
                    "Optimization process took : " + GA.GetOptimizationTime());
            GA.DisplayPerformanceSummary();

            return "GA is done";
        };

        Callable<String> C2 = () -> {
            // use the CS optimizer to find the maximum clique
            var CuckooOptimizer = new CuckooOptimizer(HoldGraph,
                    InitialSolutions,
                    TransferFunction.V2,
                    2,
                    2,
                    0.1,
                    1.5,
                    MaxIter);

            // Start the optimization process
            CuckooOptimizer.StartOptimization();

            // display the performance of Cuckoo algorithm
            out.println("The evaluation summary of the cuckoo optimizer :" + "\n" +
                    "Optimization process took : " + CuckooOptimizer.GetOptimizationTime());
            CuckooOptimizer.DisplayPerformanceSummary();

            return "Cuckoo Search is done";
        };

        Callable<String> C3 = () -> {
            // use the CSA optimizer to find the maximum clique
            var CrowOptimizer = new CrowOptimizer(HoldGraph,
                    InitialSolutions,
                    TransferFunction.V2,
                    2,
                    0.1,
                    MaxIter);

            // Start the optimization process
            CrowOptimizer.BCSAStartOptimization();

            // display the performance of CSA algorithm
            out.println("The evaluation summary of the crow optimizer :" + "\n" +
                    "Optimization process took : " + CrowOptimizer.GetOptimizationTime());
            CrowOptimizer.DisplayPerformanceSummary();

            return "CSA is done";
        };

        Callable<String> C4 = () -> {
                // use the Jaya optimizer to find the maximum clique
                var JayaOptimizer = new JayaOptimizer(HoldGraph,
                InitialSolutions,
                TransferFunction.V2,
                FN.getWorstSolution(),
                FN.getBestSolution(),
                MaxIter);

        // Start the optimization process
        JayaOptimizer.StartOptimization();

        // display the performance of Jaya algorithm
        out.println("The evaluation summary of the jaya optimizer :" + "\n" +
                "Optimization process took : " + JayaOptimizer.GetOptimizationTime());
        JayaOptimizer.DisplayPerformanceSummary();

        return "Jaya is done";
        };

        Callable<String> C5 = () -> {
            // use the enhanced crow optimizer to find the maximum clique
            var ImprovedCrowOptimizer = new CrowOptimizer(HoldGraph,
                    InitialSolutions,
                    TransferFunction.V2,
                    2,
                    0.1,
                    1.5,
                    2,
                    MaxIter);

            // Start the optimization process
            ImprovedCrowOptimizer.IBCSAStartOptimization();

            // display the performance of IBCSA algorithm
            out.println("The evaluation summary of the improved crow optimizer :" + "\n" +
                    "Optimization process took : " + ImprovedCrowOptimizer.GetOptimizationTime());
            ImprovedCrowOptimizer.DisplayPerformanceSummary();

            return "ICSA is done";
        };

        Callable<String> C6 = () -> {
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
                    MaxIter);

            // Start the optimization process
            CrowJayaOptimizer.IBCSA_BJOStartOptimization();

            // display the performance of IBCSA-BJO algorithm
            out.println("The evaluation summary of the IBCSA-BJAYA optimizer :" + "\n" +
                    "Optimization process took : " + CrowJayaOptimizer.GetOptimizationTime());
            CrowJayaOptimizer.DisplayPerformanceSummary();

            return "IBCSA-JAYA is done";
        };

        Callable<String> C7 = () -> {
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
                    MaxIter);

            // Start the optimization process
            CrowCuckooOptimizer.IBCSA_BCSStartOptimization();

            // display the performance of IBCSA-BCS algorithm
            out.println("The evaluation summary of the IBCSA-BCS optimizer :" + "\n" +
                    "Optimization process took : " + CrowCuckooOptimizer.GetOptimizationTime());
            CrowCuckooOptimizer.DisplayPerformanceSummary();
            // display the solution based the IBCSA-BCS algorithm
            CrowCuckooOptimizer.DisplayBestSolution();

            return "IBCSA-BCS is done";
        };

        // create an executor service with a pool of threads
        // submit all callables to the threads in the pool
        var executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        var STime = System.currentTimeMillis();
        var SubmitCalls = executorService.invokeAll(List.of(C1, C2, C3, C4, C5, C6, C7));
        var FTime = System.currentTimeMillis();

        // get the results
        for (var result : SubmitCalls)
            out.println(result.get());

        System.gc();

        out.println("The execution time of the submitted algorithms took" + "\t" + (double) (FTime - STime) / 1000 + "s");

        // close the executor service
        executorService.shutdown();
    }
}

/*class Test1 {

    static class A { }
    static class B extends A { }
    static class C extends B { }

    public static void main(String[] args) {
        var As = new ArrayList<A>() {{add(new A()); add(new A());}};
        PrintE(As);
        PrintS(As);

        var Bs = new ArrayList<B>() {{add(new B()); add(new B());}};
        PrintE(Bs);
        PrintS(Bs);

        var Cs = new ArrayList<A>() {{add(new C()); add(new C());}};
        PrintE(Cs);
        PrintS(Cs);
    }

    private static void PrintE(List<? extends A> lista) {
        lista.iterator().forEachRemaining(x -> out.println(x.hashCode()));
        A a = lista.get(0);  // it works
        B b = lista.get(0);  // compile error since compiler does not know which type would be pased here
        C c = lista.get(0);  // compile error since compiler does not know which type would be pased here

        lista.add(new A());  // compile error when add object from any type
        lista.add(new B());  // compile error when add object from any type
        lista.add(new C());  // compile error when add object from any type

        lista.add(null);  // this is allowed since
    }

    private static void PrintS(List<? super B> lista) {
        lista.iterator().forEachRemaining(x -> out.println(x.hashCode()));
        A a = lista.get(0);  // compile error since compiler does not know which type would be pased here
        B b = lista.get(0);  // compile error since compiler does not know which type would be pased here
        C c = lista.get(0);  // compile error since compiler does not know which type would be pased here

        lista.add(new A());  // compile error when add object from any type
        lista.add(new B());  // it works
        lista.add(new C());  // it works

        lista.add(null);  // this is allowed since
    }
}*/

class Test2 {

    static class A { }
    static class B extends A { }

    public static void main(String[] args) {
        out.println(CONCAT(new ArrayList<A>(List.of(new A())), new ArrayList<B>(List.of(new B()))));
        out.println(concat(new ArrayList<Integer>(List.of(1, 2, 3, 4)), new ArrayList<Integer>(List.of(5, 6, 7, 8))));
    }

    private static <E> List<? extends A> CONCAT(List<? extends A> a, List<? extends A> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

    private static Stream<?> concat(List<?> a, List<?> b) {
        return Stream.concat(a.stream(), b.stream());
    }
}

class Test3 {
    public static void main(String[] args) {
        var lista = new ArrayList<>(List.of(1, 2, 3, 4));
        var Nums = new ArrayList();  // warning 'row use parametrized class'
        Nums = lista;
        Nums.add('a');  // warning
        out.println(Nums);
    }
}

