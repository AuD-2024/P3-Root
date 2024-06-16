package p3.graph;

import p3.SetUtils;

import java.util.Set;

public class Node<N> {
    protected final Set<Edge<N>> edges;

    public Set<Edge<N>> getAdjacentEdges() {
        return edges;
    }

    public Node(Set<Edge<N>> adjacentEdges) {
        this.edges = SetUtils.immutableCopyOf(adjacentEdges);
    }
}
