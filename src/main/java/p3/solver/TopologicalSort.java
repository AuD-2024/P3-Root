package p3.solver;

import p3.graph.Graph;

import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of the topological sort algorithm.
 * <p>
 * A topological sort for a directed, acyclic {@link Graph} is a linear ordering of its nodes such that for every
 * directed {@link p3.graph.Edge} {@code (u, v)} from node {@code u} to node {@code v}, {@code u} comes before {@code v}
 * in the ordering.
 * <p>
 * This implementation uses the Depth-First Search algorithm to traverse the graph and is able to detect cycles in the
 * given graph.
 *
 * @param <N> the type of the nodes in the graph.
 */
public class TopologicalSort<N> {

    /**
     * The {@link DFS} instance used to traverse the graph.
     */
    private final DFS<N> dfs;

    /**
     * Creates a new {@link TopologicalSort} for the given graph.
     *
     * @param graph the graph to sort.
     */
    public TopologicalSort(Graph<N> graph) {
        this.dfs = new DFS<>(graph);
    }

    /**
     * Sorts the graph given in the constructor topologically.
     * If the graph contains a cycle, a {@link CycleException} is thrown.
     *
     * @return a list of all nodes in the graph sorted topologically.
     * @throws CycleException if the graph contains a cycle.
     */
    public List<N> sort() {
        LinkedList<N> sortedNodes = new LinkedList<>();

        dfs.traverse((node, ignored) -> {
            sortedNodes.addFirst(node);
        });

        if (dfs.isCyclic()) {
            throw new CycleException("Cannot perform topological sort on cyclic graph.");
        }

        return sortedNodes;
    }
}
