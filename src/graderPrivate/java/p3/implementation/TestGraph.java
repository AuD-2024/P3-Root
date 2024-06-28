package p3.implementation;

import p3.graph.Edge;
import p3.graph.MutableGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestGraph<N> implements MutableGraph<N> {

    private final Set<N> nodes;

    private final Set<Edge<N>> edges;

    public TestGraph(Set<N> nodes, Set<Edge<N>> edges) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
    }

    public TestGraph(List<N> nodes) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>();
    }

    public TestGraph(List<N> nodes, Set<Edge<N>> edges) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
    }

    @Override
    public void addNode(N node) {
        nodes.add(node);
    }

    @Override
    public void addEdge(Edge<N> edge) {
        edges.add(edge);
    }

    @Override
    public Set<N> getNodes() {
        return new HashSet<>(nodes);
    }

    @Override
    public Set<Edge<N>> getEdges() {
        return new HashSet<>(edges);
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
