package p3.graph;

import java.util.Set;

/**
 * A directed graph with nodes of type {@code N}.
 *
 * <p>
 * A graph is a set of nodes and a set of edges.
 * Each edge connects two nodes.
 * There can be at most one edge between two nodes.
 * </p>
 *
 * <p>
 * A graph is not necessarily mutable.
 * </p>
 *
 * @param <N> the type of the nodes in this graph.
 */
public interface Graph<N> {

    /**
     * Returns all nodes in this graph.
     * @return a set of all nodes in this graph.
     */
    Set<Node<N>> getNodes();

    /**
     * Returns all edges in this graph.
     * @return a set of all edges in this graph.
     */
    Set<Edge<N>> getEdges();

    /**
     * Creates a new empty immutable graph.
     * @return a new empty immutable graph.
     * @param <N> the type of the nodes in the graph.
     */
    @SuppressWarnings("unchecked")
    static <N> Graph<N> of() {
        return (Graph<N>) BasicGraph.EMPTY.get();
    }

    /**
     * Creates a new immutable graph with the given nodes and edges.
     * @param nodes the nodes in the graph.
     * @param edges the edges in the graph.
     * @return a new immutable graph with the given nodes and edges.
     * @param <N> the type of the nodes in the graph.
     */
    static <N> Graph<N> of(Set<Node<N>> nodes, Set<Edge<N>> edges) {
        return new BasicGraph<>(nodes, edges);
    }
}
