package p3.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple implementation of an immutable {@link Graph}.
 *
 * @param <N> the type of the nodes in the graph.
 */
public class BasicGraph<N> implements Graph<N> {

    /**
     * An unmodifiable copy of the nodes in the graph.
     */
    private final Set<N> nodes;

    /**
     * An unmodifiable copy of the edges in the graph.
     */
    private final Set<Edge<N>> edges;

    /**
     * Creates a new {@link BasicGraph} with the given nodes and edges.
     *
     * @param nodes the nodes in the graph.
     * @param edges the edges in the graph.
     */
    public BasicGraph(Set<N> nodes, Set<Edge<N>> edges) {
        this.nodes = Set.copyOf(nodes);
        this.edges = Set.copyOf(edges);
    }

    @Override
    public Set<N> getNodes() {
        return nodes;
    }

    @Override
    public Set<Edge<N>> getEdges() {
        return edges;
    }

    @Override
    public Set<Edge<N>> getOutgoingEdges(N node) {
        return edges.stream()
            .filter(edge -> edge.from().equals(node))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Edge<N>> getIngoingEdges(N node) {
        return edges.stream()
            .filter(edge -> edge.to().equals(node))
            .collect(Collectors.toSet());
    }

    @Override
    public Edge<N> getEdge(N from, N to) {
        return edges.stream()
            .filter(edge -> edge.from().equals(from) && edge.to().equals(to))
            .findFirst()
            .orElse(null);
    }
}
