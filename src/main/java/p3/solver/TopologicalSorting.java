package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;
import p3.graph.Node;

import java.util.Set;

public class TopologicalSorting<N> {
    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;

    private Set<Node<N>> visited;

    private Set<Node<N>> topologicalOrder;

    public TopologicalSorting(Graph<N> graph) {
        this.graph = graph;
        this.visited = Set.of();
        this.topologicalOrder = Set.of();
    }

    public boolean validate(Node<N> start) {
        topologicalSort(start);
        return hasCycles();
    }

    /**
     * hasCycles validates the graph for cycles.
     *
     * @return true if graph has cycles, else false
     */
    private boolean hasCycles() {
        Set<Node<N>> visStack = Set.of();

        for (Node<N> node : topologicalOrder) {
            if (visStack.contains(node)) {
                return true;
            }

            visStack.add(node);
        }

        return false;
    }

    private void topologicalSort(Node<N> start) {
        init();
        processNode(start);
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
