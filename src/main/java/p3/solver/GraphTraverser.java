package p3.solver;

import p3.graph.Graph;

import java.util.function.ObjIntConsumer;

public interface GraphTraverser<N> {
    /**
     * Runs Depth-First search on the graph.
     *
     * @param consumer Consumer that accepts the node and its finish time.
     */
    void dfs(ObjIntConsumer<N> consumer);

    /**
     * A factory for creating new instances of {@link MSTCalculator}.
     */
    interface Factory {

        /**
         * Create a new instance of {@link GraphTraverser} for the given graph.
         * @param graph the graph to calculate the path for.
         * @return a new instance of {@link GraphTraverser}.
         * @param <N> The type of the nodes in the graph.
         */
        <N> GraphTraverser<N> create(Graph<N> graph);
    }
}
