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

    @Override
    public List<N> calculatePath(N start, N end) {
        init(start);
        relax();

        List<Edge<N>> negativeCycles = checkNegativeCycles();
        if (negativeCycles.isEmpty()) {
            return reconstructPath(start, end);
        } else {
            throw new RuntimeException("Got some cycles");
        }
    }


    /**
     * This method initializes all distances from the start till the end with the appropriate distances.
     *
     * @param start the node that defines the beginning of the graph.
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
        graph.getNodes()
            .stream()
            .flatMap(ignored -> graph.getEdges().stream())
            .forEach(edge -> {
                int startDistance = distances.get(edge.from());
                int targetWeight = startDistance + edge.weight();
                if (startDistance != Integer.MAX_VALUE && targetWeight < distances.get(edge.to())) {
                    distances.put(edge.to(), targetWeight);
                    predecessors.put(edge.to(), edge.from());
                }
            });
    }


    protected List<Edge<N>> checkNegativeCycles() {
        List<Edge<N>> cyclicEdges = new ArrayList<>();

        for (Edge<N> edge : graph.getEdges()) {
            int src = distances.get(edge.from());
            int dest = distances.get(edge.to());
            if (src != Integer.MAX_VALUE && src + edge.weight() < dest) {
                cyclicEdges.add(edge);
            }
        }

        return cyclicEdges;
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
