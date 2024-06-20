package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;
import p3.graph.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TopologicalSorting<N> {
    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;
    private final Set<Node<N>> visited;
    private final LinkedList<Node<N>> topologicalOrder;

    public TopologicalSorting(Graph<N> graph) {
        this.graph = graph;
        this.visited = new HashSet<>();
        this.topologicalOrder = new LinkedList<>();
    }

    public void sort(Node<N> start) {
        topologicalSort(start);
    }

    public List<Node<N>> getTopologicalOrder() {
        return topologicalOrder;
    }

    private void topologicalSort(Node<N> start) {
        init();
        dfs(start);
    }

    private void init() {
        visited.clear();
        topologicalOrder.clear();
    }

    private void dfs(Node<N> current) {
        visited.add(current);
        for (Edge<N> edge : current.getAdjacentEdges()) {
            if (!visited.contains(edge.to())) {
                dfs(edge.to());
            } else {
                throw new CycleException("A cycle was detected");
            }
        }

        topologicalOrder.addFirst(current);
    }
}
