package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class DFSImpl<N> implements DFS<N> {
    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;
    private final Set<N> visited;
    private final BiConsumer<Integer, N> consumer;
    private int time = 0;

    public DFSImpl(Graph<N> graph, BiConsumer<Integer, N> consumer) {
        this.graph = graph;
        this.visited = new HashSet<>();
        this.consumer = consumer;
    }

    @Override
    public void dfs(N start) {
        init();
        visit(start);
    }

    private void init() {
        visited.clear();
        time = 0;
    }

    private void visit(N current) {
        int discoveryTime = time;
        visited.add(current);
        time++;

        for (Edge<N> edge : graph.getOutgoingEdges(current)) {
            if (!visited.contains(edge.to())) {
                dfs(edge.to());
            } else {
                throw new CycleException("A cycle was detected");
            }
        }

        consumer.accept(discoveryTime, current);
    }
}
