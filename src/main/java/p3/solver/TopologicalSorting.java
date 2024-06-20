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
    private final List<Node<N>> topologicalOrder;

    public TopologicalSorting(Graph<N> graph) {
        this.graph = graph;
        this.visited = new HashSet<>();
        this.topologicalOrder = new LinkedList<>();
    }

    public List<Node<N>> sort(Node<N> start) {
        return topologicalSort(start);
    }

    public List<Node<N>> getTopologicalOrder() {
        return topologicalOrder;
    }

    private List<Node<N>> topologicalSort(Node<N> start) {
        init();
        dfs(start);
        return topologicalOrder;
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

        topologicalOrder.add(0, current);
    }
}
