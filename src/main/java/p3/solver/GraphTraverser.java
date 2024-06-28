package p3.solver;

import p3.graph.Graph;

import java.util.function.ObjIntConsumer;

/**
 * Interface for traversing the node of a given {@link Graph}.
 *
 * @param <N> the type of the nodes in the graph.
 */
public interface GraphTraverser<N> {
    /**
     * Traverses the graph and visits each node in the graph exactly once. The order in which the nodes are visited is
     * determined by the algorithm used to traverse the graph.
     * <p>
     * The consumer is called for each node in the graph in the order they are finished by the traversing algorithm,
     * i.e., the consumer is called each time a node is colored black. Additionally, the second argument of the consumer
     * is the finish time of the node.
     *
     * @param consumer Consumer that accepts the node and its finish time.
     */
    void traverse(ObjIntConsumer<N> consumer);

    /**
     * A factory for creating new instances of {@link MSTCalculator}.
     */
    interface Factory {

        /**
         * Create a new instance of {@link GraphTraverser} for the given graph.
         *
         * @param graph the graph to calculate the path for.
         * @param <N>   the type of the nodes in the graph.
         * @return a new instance of {@link GraphTraverser}.
         */
        <N> GraphTraverser<N> create(Graph<N> graph);
    }
}
