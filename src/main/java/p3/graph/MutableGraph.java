package p3.graph;

import java.util.Set;

/**
 * A directed, weighted, mutable graph with nodes of type {@code N}.
 * <p>
 * Implementations of this interface are mutable, i.e., nodes and edges can be added to the graph after its creation.
 *
 * @param <N> the type of the nodes in this graph.
 * @see Graph
 */
public interface MutableGraph<N> extends Graph<N> {

    /**
     * Adds the given node to the graph. It initially has no ingoing or outgoing edges.
     * <p>
     * If the node is already in the graph, this method does nothing.
     */
    void addNode(N node);

    /**
     * Adds the given {@link Edge} to the graph. The connected nodes must have already been added to the graph.
     * <p>
     * If the edge is already in the graph, this method only updates the weight of the edge.
     *
     * @param edge the edge to add.
     * @throws IllegalArgumentException if at least one node of the edge is not in the graph.
     */
    void addEdge(Edge<N> edge);

    /**
     * Adds an {@link Edge} from the node {@code from} to the node {@code to} with the given weight. The nodes must have
     * already been added to the graph.
     * <p>
     * If an edge between the two nodes already exists, this method only updates the weight of the edge.
     *
     * @param from   the node the edge starts at.
     * @param to     the node the edge ends at.
     * @param weight the weight of the edge.
     * @throws IllegalArgumentException if at least one of the nodes is not in the graph.
     */
    default void addEdge(N from, N to, int weight) {
        addEdge(Edge.of(from, to, weight));
    }

    /**
     * First adds the nodes {@code from} and {@code to} to the graph, and then connects them with an {@link Edge} with
     * the given weight. It is not required that the nodes are not already in the graph. If they are, only the edge is
     * added. If an edge between the two nodes already exists, only the weight is updated.
     * <p>
     * Calling this method is equivalent to the following code:
     * <blockquote><pre>
     * addNode(from);
     * addNode(to);
     * addEdge(from, to, weight);
     * </pre></blockquote>
     *
     * @param from   the node the edge starts at.
     * @param to     the node the edge ends at.
     * @param weight the weight of the edge.
     */
    default void addNodesAndEdge(N from, N to, int weight) {
        addNode(from);
        addNode(to);
        addEdge(from, to, weight);
    }

    /**
     * Creates a new {@link MutableGraph} with the given nodes and edges.
     *
     * @param nodes the nodes in the graph.
     * @param edges the edges in the graph.
     * @param <N>   the type of the nodes in the graph.
     * @return a new, mutable graph with the given nodes and edges.
     */
    static <N> MutableGraph<N> of(Set<N> nodes, Set<Edge<N>> edges) {
        return new AdjacencyGraph<>(nodes, edges, AdjacencyList.FACTORY);
    }

}
