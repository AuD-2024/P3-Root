package p3.solver;

public interface DFS<N> {
    /**
     * Runs Depth-First search on the graph.
     *
     * @param start start node in the graph
     */
    void dfs(N start);
}
