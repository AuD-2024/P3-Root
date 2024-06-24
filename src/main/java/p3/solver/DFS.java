package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.ObjIntConsumer;

/**
 * An implementation of the {@link GraphTraverser} interface that uses the Depth-First Search algorithm to traverse
 * the graph
 *
 * @param <N> the type of the nodes in the graph.
 */
public class DFS<N> implements GraphTraverser<N> {

    /**
     * Factory for creating new instances of {@link DFS}.
     */
    public static final GraphTraverser.Factory FACTORY = DFS::new;

    /**
     * The graph to traverse.
     */
    protected final Graph<N> graph;

    /**
     * A set of all nodes that have already been visited. A node being added this set is equivalent to it being
     * colored gray.
     */
    protected final Set<N> visited;

    /**
     * Stores the current timestep during the visiting by the DFS algorithm.
     */
    protected int time = 0;

    /**
     * Creates a new {@link DFS} for the given graph.
     *
     * @param graph the graph to traverse.
     */
    public DFS(Graph<N> graph) {
        this.graph = graph;
        this.visited = new HashSet<>();
    }

    @Override
    public void traverse(ObjIntConsumer<N> consumer) {
        init();
        for (N node : graph.getNodes()) {
            if (!visited.contains(node)) {
                visit(consumer, node);
            }
        }
    }

    /**
     * Initializes the DFS algorithm to its starting state.
     */
    protected void init() {
        visited.clear();
        time = 0;
    }

    /**
     * Visits a new node in the graph.
     * <p>
     * A node is visited by first coloring it gray and then recursively visiting all its neighbors. After all neighbors
     * have been visited, the node is colored black, i.e., it is passed to the consumer.
     *
     * @param consumer Function that accepts the node and its finish time.
     * @param current  Node that is processed by this method
     */
    protected void visit(ObjIntConsumer<N> consumer, N current) {
        time++;
        visited.add(current);

        for (Edge<N> edge : graph.getOutgoingEdges(current)) {
            if (!visited.contains(edge.to())) {
                visit(consumer, edge.to());
            }
        }

        time++;

        consumer.accept(current, time);
    }
}
