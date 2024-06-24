package p3.solver;

import p3.graph.Graph;

import java.util.List;

/**
 * Interface for calculating a path between two nodes in a given {@link Graph}.
 *
 * @param <N> The type of the nodes in the graph.
 */
public interface PathCalculator<N> {

    /**
     * Calculate an optimal path between two nodes in a graph.
     * <p>
     * The criteria for optimality is up to the implementation.
     * <p>
     * The result is a list of along the path from the start node to the end node.
     * The start and the end node are both included in the returned list.
     * <p>
     * An implementation may throw an {@link CycleException} if a negative cycle within the graph is detected.
     * However, this is not required as not all implementations may be able to detect negative cycles.
     *
     * @return A list representing the path found between the start and end nodes.
     * @throws CycleException optionally, if a negative cycle is detected.
     */
    List<N> calculatePath(N start, N end);

    /**
     * A factory for creating new instances of {@link MSTCalculator}.
     */
    interface Factory {

        /**
         * Create a new instance of {@link PathCalculator} for the given graph.
         *
         * @param graph the graph to calculate the MST for.
         * @param <N>   the type of the nodes in the graph.
         * @return a new instance of {@link PathCalculator} using the given graph.
         */
        <N> PathCalculator<N> create(Graph<N> graph);
    }
}
