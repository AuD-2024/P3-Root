package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;
import p3.graph.Node;

import java.util.Map;
import java.util.Set;

public class TopologicalSorting<N> {
    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;

    private Set<Node<N>> visited;

    private Set<Node<N>> topologicalOrder;

    public Set<Node<N>> topologicalSort(Node<N> start) {
        init();
        processNode(start);

        return this.topologicalOrder;
    }

    public Set<Edge<N>> detectCycles() {
        return Set.of();
    }

    private void init() {
        visited.clear();
        topologicalOrder.clear();
    }

    private void processNode(Node<N> current) {
        visited.add(current);
        for (Edge<N> edge : current.getAdjacentEdges()) {
            if (!visited.contains(edge.to())) {
                processNode(edge.to());
            }
        }

        topologicalOrder.add(current);
    }
}
