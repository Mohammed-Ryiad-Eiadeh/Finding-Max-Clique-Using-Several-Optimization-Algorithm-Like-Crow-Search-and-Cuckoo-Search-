package Feasibility.org;

import java.util.Random;

/**
 * Convert infeasible solution to feasible one
 */

public class Feasibility {

    private final int[][] AdjMatrix;

    /**
     * Default constructor of the feasibility
     * @param adjMatrix refers to the constructed adjacency matrix of the graph
     */
    public Feasibility(int[][] adjMatrix) {
        this.AdjMatrix = adjMatrix;
    }

    /**
     * This method is used to fix the infiasible solution and convert them to feasible one
     * @param solution refers to one solution from all ones
     * @return return the evolved feasible solution
     */
    public double[] GuaranteeFeasibility(double[] solution) {
        for (int i = 0; i < solution.length; i++)
            if (solution[i] == 1.0)
                for (int j = i + 1; j < solution.length; j++)
                    if (solution[j] == 1.0)
                        if (AdjMatrix[i][j] != 1 || AdjMatrix[j][i] != 1)
                            if (new Random().nextDouble() >= 0.5)
                                solution[j] = 0.0;
                            else
                                solution[i] = 0.0;
        return solution;  // return EvolveSolution(Solution);
    }
/*
    *//**
     * @param Solution refers to a feasible solution
     * @return return the evolved feasible solution
     *//*
    public double[] EvolveSolution(double[] Solution) {
        var Flag = true;
        for (var i = 0; i < Solution.length; i++)
            if (Solution[i] == 0.0) {
                for (var ii = AdjMatrix.length - 1; ii > 0; ii--)
                    if (Solution[ii] == 1 && AdjMatrix[ii][i] != 1) {
                        Flag = false;
                        break;
                    }
                if (Flag)
                    Solution[i] = 1.0;
                }
        return Solution;
    }*/
}
