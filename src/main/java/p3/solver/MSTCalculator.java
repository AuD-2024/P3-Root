package p3.solver;

import p3.graph.Graph;

/**
 * Interface for calculating a minimum spanning tree for a given {@link Graph}.
 *
 * @param <N> the type of the nodes in the graph.
 */
public interface MSTCalculator<N> {

    /**
     * Calculate the MST for the graph.
     * <p>
     * The criteria for minimality is up to the implementation.
     * <p>
     * The result is a graph containing the same nodes as the original graph, but only the edges in the MST.
     * The result graph is a subgraph of the original graph and is guaranteed to be connected and acyclic.
     *
     * @param root the root node of the MST.
     * @return A new graph describing the MST.
     */
    Graph<N> calculateMST(N root);

    /**
     * A factory for creating new instances of {@link MSTCalculator}.
     */
    interface Factory {

        /**
         * Create a new instance of {@link MSTCalculator} for the given graph.
         *
         * @param graph the graph to calculate the MST for.
         * @param <N>   The type of the nodes in the graph.
         * @return a new instance of {@link MSTCalculator} using the given graph.
         */
        <N> MSTCalculator<N> create(Graph<N> graph);
    }
}
