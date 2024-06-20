package p3.graph;

import p3.SetUtils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A basic implementation of an immutable {@link Graph} that uses a {@link Map} to store the edges that are adjacent to each node.
 *
 * @param <N>
 */
public class BasicGraph<N> implements Graph<N> {
    /**
     * The nodes in this graph.
     */
    protected final Set<Node<N>> nodes;

    /**
     * The edges in this graph.
     */
    protected final Set<Edge<N>> edges;

    /**
     * Constructs a new {@link BasicGraph} with the given nodes and edges.
     *
     * @param nodes the nodes.
     * @param edges the edges.
     */
    public BasicGraph(Set<Node<N>> nodes, Set<Edge<N>> edges) {
        Set<Node<N>> nodesWithEdges = nodes.stream()
            .map(n -> new Node<>(edges.stream().filter(d -> d.from().equals(n)).collect(Collectors.toSet())))
            .collect(Collectors.toSet());

        this.nodes = SetUtils.immutableCopyOf(nodesWithEdges);
        this.edges = SetUtils.immutableCopyOf(edges);
    }

    public void addEdge(Edge<N> edge) {
        nodes.stream().filter(node -> node == edge.from()).findFirst().ifPresent(node -> node.edges.add(edge));
    }

    public void addNode(Node<N> node) {
        nodes.add(node);
    }

    @Override
    public Set<Node<N>> getNodes() {
        return nodes;
    }

    @Override
    public Set<Edge<N>> getEdges() {
        return edges;
    }
}
