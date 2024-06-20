package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TopologicalSorting<N> {
    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;
    private final Set<N> visited;
    private final List<N> topologicalOrder;

    public TopologicalSorting(Graph<N> graph) {
        this.graph = graph;
        this.visited = new HashSet<>();
        this.topologicalOrder = new LinkedList<>();
    }

    public List<N> sort(N start) {
        return topologicalSort(start);
    }

    public List<N> getTopologicalOrder() {
        return topologicalOrder;
    }

    private List<N> topologicalSort(N start) {
        init();
        dfs(start);
        return topologicalOrder;
    }

    private void init() {
        visited.clear();
        topologicalOrder.clear();
    }

    private void dfs(N current) {
        visited.add(current);
        for (Edge<N> edge : graph.getOutgoingEdges(current)) {
            if (!visited.contains(edge.to())) {
                dfs(edge.to());
            } else {
                throw new CycleException("A cycle was detected");
            }
        }

        topologicalOrder.add(0, current);
    }
}
