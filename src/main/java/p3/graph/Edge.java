package p3.graph;

/**
 * Represents a weighted, directed edge in a {@link Graph}.
 * <p>
 * It connects two nodes and associates a weight with the connection. The weight is any integer value and is used to
 * order the edges in the graph.
 * <p>
 * Note that the edges are directed, i.e., an edge from node A to node B is different from an edge from node B to node A.
 * The existence of one of them does not imply the existence of the other.
 *
 * @param <N>
 */
public interface Edge<N> extends Comparable<Edge<N>> {

    /**
     * Returns the node that this edge starts at, i.e., the node this edge is going out from.
     *
     * @return The node that this edge starts at.
     */
    N from();

    /**
     * Returns the node that this edge ends at, i.e., the node this edge is going into.
     *
     * @return The node that this edge ends at.
     */
    N to();

    /**
     * The weight of the edge. The precise meaning of this value is up to the user.
     *
     * <p>
     * This value is used to order edges in the graph.
     * </p>
     *
     * @return the weight of the edge
     */
    int weight();

    /**
     * Two edges are equal if they have the same nodes. The weight of the edges is not considered for equality.
     *
     * <p>More precisely, two edges <code>x</code> and <code>y</code> are equal iff:</p>
     * <ul>
     *     <li><code>Objects.equals(x.from(), y.from())</code></li>
     *     <li><code>Objects.equals(x.to(), y.to())</code></li>
     * </ul>
     *
     * @param other the other edge
     * @return {@code true} if the edges are equal, {@code false} otherwise
     */
    boolean equals(Object other);

    /**
     * Edges are ordered by weight.
     *
     * @param other the other edge
     * @return Whether this edge's weight is less than, equal to, or greater than the other edge's weight
     */
    @Override
    default int compareTo(Edge<N> other) {
        return Integer.compare(weight(), other.weight());
    }

    /**
     * Creates a new edge with the given nodes and weight.
     *
     * @param from      the first node
     * @param to      the second node
     * @param weight the weight of the edge
     * @param <N>    the type of the nodes in the graph
     * @return a new edge with the given nodes and weight
     */
    static <N> Edge<N> of(N from, N to, int weight) {
        return new EdgeImpl<>(from, to, weight);
    }
}
