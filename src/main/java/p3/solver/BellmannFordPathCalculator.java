package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.*;

public class BellmannFordPathCalculator<N> implements PathCalculator<N> {
    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;

    /**
     * The distance from the start node to each node in the graph.
     */
    protected final Map<N, Integer> distances = new HashMap<>();

    /**
     * The predecessor of each node in the graph along the shortest path to the start node.
     */
    protected final Map<N, N> predecessors = new HashMap<>();

    /**
     * The set of nodes that have not yet been visited.
     */
    protected final Set<N> remainingNodes = new HashSet<>();

    @Override
    public List<N> calculatePath(N start, N end) {
        init(start);
        relax();
        try {
            checkNegativeCycles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return reconstructPath(start, end);
    }


    /**
     * This method initializes all distances from the start till the end with the appropriate distances.
     *
     * @param start
     */
    protected void init(N start) {
        distances.clear();
        predecessors.clear();
        for (N node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }

        distances.put(start, 0);
    }

    /**
     * Relax relaxes the connection between all nodes.
     */
    protected void relax() {
        for (N node : graph.getNodes()) {
            for (Edge<N> edge : graph.getEdges()) {
                int newDistance = distances.get(edge.from()) + edge.weight();

                if (distances.get(edge.from()) != Integer.MAX_VALUE && newDistance < distances.get(edge.to())) {
                    distances.put((N) edge.to(), newDistance);
                    predecessors.put(edge.to(), edge.from());
                }
            }
        }

    }

    protected void checkNegativeCycles() throws Exception {
        for (Edge edge : graph.getEdges()) {
            int src = distances.get(edge.from());
            int dest = distances.get(edge.to());
            if (src != Integer.MAX_VALUE && src + edge.weight() < dest) {
                throw new Exception("Cycle Detected");
            }
        }
    }

    protected List<N> reconstructPath(N start, N end) {
        LinkedList<N> shortestPath = new LinkedList<>();
        N current = end;
        while (!current.equals(start)) {
            shortestPath.addFirst(current);
            current = predecessors.get(current);
        }
        shortestPath.addFirst(start);
        return shortestPath;
    }

}
