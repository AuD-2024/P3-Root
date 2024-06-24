package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.ObjIntConsumer;

public class DFS<N> implements GraphTraverser<N> {
    /**
     * Factory for creating new instances of {@link DFS}.
     */
    public static GraphTraverser.Factory FACTORY = DFS::new;


    /**
     * The graph to calculate paths in.
     */
    protected final Graph<N> graph;

    /**
     * Visited contains all nodes that have already been visited.
     */
    protected final Set<N> visited;

    /**
     * Stores the current timestep during the visiting by the DFS algorithm.
     */
    protected int time = 0;

    /**
     * Stores whether the graph contains negative cycles or not.
     */
    protected boolean cyclic;

    public DFS(Graph<N> graph) {
        this.graph = graph;
        this.visited = new HashSet<>();
        this.cyclic = false;
    }

    /**
     * Determines if the graph does contain negative cycles
     * @return true if graph contains negative cycles.
     */
    public boolean isCyclic() {
        return cyclic;
    }

    @Override
    public void dfs(ObjIntConsumer<N> consumer) {
        init();
        for (N node : graph.getNodes()) {
            if (!visited.contains(node)) {
                visit(consumer, node);
            }
        }
    }

    /**
     * Init initializes the DFS graph and its fields
     */
    private void init() {
        visited.clear();
        cyclic = false;
        time = 0;
    }

    /**
     * visit visits a new node in the graph.
     * The discovered node is propagated to the consumer.
     *
     * @param consumer Function that accepts the node and its finish time.
     * @param current Node that is processed by this method
     */
    private void visit(ObjIntConsumer<N> consumer, N current) {
        time++;

        for (Edge<N> edge : graph.getOutgoingEdges(current)) {
            if (!visited.contains(edge.to())) {
                visit(consumer, edge.to());
            } else {
                this.cyclic = true;
            }
        }

        time++;

        consumer.accept(current, time);
    }
}
