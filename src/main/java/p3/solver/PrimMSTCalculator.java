package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.*;

public class PrimMSTCalculator<N> implements MSTCalculator<N> {
    /**
     * Factory for creating new instances of {@link PrimMSTCalculator}.
     */
    public static MSTCalculator.Factory FACTORY = PrimMSTCalculator::new;

    /**
     * The graph to calculate the MST for.
     */
    protected final Graph<N> graph;

    /**
     * Stores the predecessor for each node.
     */
    protected final Map<N, N> predecessors;

    /**
     * Stores the current minimum distance for each node
     */
    protected final Map<N, Integer> keys;

    /**
     * Stores the nodes that have been visited by the graph.
     */
    protected final Set<N> visited;

    /**
     * The edges in the MST.
     */
    protected final Set<Edge<N>> mstEdges;

    public PrimMSTCalculator(Graph<N> graph) {
        this.graph = graph;
        this.mstEdges = new HashSet<>();
        this.predecessors = new HashMap<>();
        this.keys = new HashMap<>();
        this.visited = new HashSet<>();
    }

    @Override
    public Graph<N> calculateMST(N root) {
        init(root);
        while (!visited.containsAll(graph.getNodes())) {
            processNode(extractMin());
        }

        return Graph.of(graph.getNodes(), mstEdges);
    }

    /**
     * Processes the current node with the prim algorithm
     * @param node current node processed by the algorithm
     */
    protected void processNode(N node) {
        visited.add(node);

        for (Edge<N> outgoingEdge : graph.getOutgoingEdges(node)) {
            if (!visited.contains(outgoingEdge.to()) && outgoingEdge.weight() < keys.get(outgoingEdge.to())) {
                keys.put(outgoingEdge.to(), outgoingEdge.weight());
                predecessors.put(outgoingEdge.to(), node);
            }
        }
    }

    /**
     * Initializes the fields before executing the prim algorithm
     * @param root the root node of the tree
     */
    protected void init(N root) {
        mstEdges.clear();
        predecessors.clear();
        keys.clear();

        for (N node : graph.getNodes()) {
            predecessors.put(node, null);
            keys.put(node, Integer.MAX_VALUE);
        }

        keys.put(root, Integer.MIN_VALUE);
    }

    /**
     * Extracts the current minimum node from the graph which target was not yet visited
     * @return nearest node
     */
    protected N extractMin() {
        int min = Integer.MAX_VALUE;
        Edge<N> minEdge = null;

        for (Edge<N> edge : graph.getEdges()) {
            if (!visited.contains(edge.to()) && edge.weight() < min) {
                minEdge = edge;
                min = edge.weight();
            }
        }

        return minEdge.to();
    }
}
