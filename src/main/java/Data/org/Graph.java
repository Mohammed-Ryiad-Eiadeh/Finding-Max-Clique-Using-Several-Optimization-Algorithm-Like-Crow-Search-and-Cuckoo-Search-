package Data.org;

import org.graphstream.graph.implementations.SingleGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.out;
import static java.lang.System.setProperty;

/** <li>constructing graph as adjacency matrix from the dataset</li> */

public final class Graph {

    private int[][]  AdjMatrix;
    private Scanner Read;
    private int[] DegreeHolder;
    private SingleGraph GraphVisualizer;

    /**
     * Default constructor of the graph
     * @param dataFile refers to the file which has the nodes and their edges
     */
    public Graph(String dataFile) {
        try (Scanner resource = new Scanner(new File(dataFile))) {
            Read = resource;
            int NumOfNode = Integer.parseInt(Read.nextLine());
            AdjMatrix = new int[NumOfNode][NumOfNode];
            DegreeHolder = new int[NumOfNode];
            setProperty("org.graphstream.ui", "swing");
            GraphVisualizer = new SingleGraph("Graph");
            GraphVisualizer.setStrict(false);
            GraphVisualizer.setAutoCreate(true);
            CreatGraphAsAdjacencyMatrix();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method used to build graph as adjacency matrix
     */
    private void CreatGraphAsAdjacencyMatrix() {
        while (Read.hasNext()) {
            String Edge = Read.nextLine();
            String[] TwoVertex = Edge.split("\s");
            GraphVisualizer.addEdge(TwoVertex[0].concat(TwoVertex[1]), TwoVertex[0], TwoVertex[1]);
            AdjMatrix[Integer.parseInt(TwoVertex[0]) - 1][Integer.parseInt(TwoVertex[1]) - 1] = 1;
            AdjMatrix[Integer.parseInt(TwoVertex[1]) - 1][Integer.parseInt(TwoVertex[0]) - 1] = 1;
        }
    }

    /**
     * This method used to return the graph as adjacency matrix
     * @return returns the adjacency matrix
     */
    public int[][] GetGraph() {
        return AdjMatrix;
    }

    /**
     * @return the degree of a corresponding node
     */
    public int[] VertexDegree() {
        IntStream.range(0, AdjMatrix.length).forEach(i -> DegreeHolder[i] = Arrays.stream(AdjMatrix[i]).filter(ii -> ii > 0).parallel().sum());
        return DegreeHolder;
    }

    /**
     * This method used to print the degree of each vertex in the graph
     */
    public void ShowVertexDegree() {
        if (Arrays.stream(DegreeHolder).parallel().sum() == 0)
            VertexDegree();
        out.print("\nVertex Degree:");
        out.println(Arrays.stream(DegreeHolder).mapToObj(String::valueOf).collect(Collectors.joining("\t\t", "\t\t\t[ ", " ]\n")));
    }

    /**
     * This method used to print the adjacency matrix
     */
    public void PrintGraph() {
        out.println("Graph as adjacency matrix:");
        AtomicInteger indexH = new AtomicInteger(1);
        out.println(Arrays.stream(AdjMatrix[0]).mapToObj(ii -> (indexH.getAndAdd(1)) + "").collect(Collectors.joining("\t\t", "\t\t\t[ ", " ]\n\n\n")));
        AtomicInteger indexT = new AtomicInteger(1);
        IntStream.range(0, AdjMatrix.length).forEach(ii -> out.println(Arrays.stream(AdjMatrix[ii]).mapToObj(String::valueOf).parallel().collect(Collectors.joining("\t\t",  indexT.getAndAdd(1) + "\t\t\t[ ",  " ]\n"))));
    }

    /**
     * This method used to visualize the graph
     */
    public void ShowGraph() {
        if (AdjMatrix[0].length <= 200)
            GraphVisualizer.display();
        else
            out.println("Graph with more than 200 nodes is not a good choice (may cause a synchronous exception) to be visualized");
    }
}
