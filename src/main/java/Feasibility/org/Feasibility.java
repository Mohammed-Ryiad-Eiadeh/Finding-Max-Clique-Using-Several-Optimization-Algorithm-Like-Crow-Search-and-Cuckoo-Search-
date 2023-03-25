package Feasibility.org;

import java.util.Random;

/**
 * Convert infeasible solution to feasible one
 */

public class Feasibility {

    private final int[][] AdjMatrix;

    /**
     * Default constructor of the feasibility
     * @param AdjMatrix refers to the constructed adjacency matrix of the graph
     */
    public Feasibility(int[][] AdjMatrix) {
        this.AdjMatrix = AdjMatrix;
    }

    /**
     * This method used to fix the infiasible solution and convert them to feasible one
     * @param Solution refers to one solution from all ones
     * @return return the evolved feasible solution
     */
    public double[] GuaranteeFeasibility(double[] Solution) {
        for (var i = 0; i < Solution.length; i++)
            if (Solution[i] == 1.0)
                for (var j = i + 1; j < Solution.length; j++)
                    if (Solution[j] == 1.0)
                        if (AdjMatrix[i][j] != 1 || AdjMatrix[j][i] != 1)
                            if (new Random().nextDouble() >= 0.5)
                                Solution[j] = 0.0;
                            else
                                Solution[i] = 0.0;
        return Solution;  // return EvolveSolution(Solution);
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
