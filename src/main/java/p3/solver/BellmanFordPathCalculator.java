package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.*;

public class BellmanFordPathCalculator<N> implements PathCalculator<N> {

    /**
     * The graph to calculate paths in.
     */
    private final Graph<N> graph;

    /**
     * The distance from the start node to each node in the graph.
     */
    private final Map<N, Integer> distances = new HashMap<>();

    /**
     * The predecessor of each node in the graph along the shortest path to the start node.
     */
    private final Map<N, N> predecessors = new HashMap<>();

    public BellmanFordPathCalculator(Graph<N> graph) {
        this.graph = graph;
    }

    @Override
    public List<N> calculatePath(N start, N end) {
        initSSSP(start);

        processGraph();

        List<Edge<N>> negativeCycles = checkNegativeCycles();
        if (negativeCycles.isEmpty()) {
            return reconstructPath(start, end);
        } else {
            throw new CycleException("A cycle was detected");
        }
    }

    /**
     * This method initializes all distances from the start till the end with the appropriate distances.
     *
     * @param start the node that defines the beginning of the graph.
     */
    protected void initSSSP(N start) {
        distances.clear();
        predecessors.clear();
        for (N node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }

        distances.put(start, 0);
    }

    /**
     * Relax every edge for every node in the graph once.
     */
    protected void processGraph() {
        for (int i = 1; i < graph.getNodes().size(); i++) {
            for (Edge<N> edge : graph.getEdges()) {
                relax(edge);
            }
        }
    }

    /**
     * Relax relaxes the connection between all nodes.
     */
    protected void relax(Edge<N> edge) {
        int startWeight = distances.get(edge.from());
        int targetWeight = startWeight + edge.weight();
        if (startWeight != Integer.MAX_VALUE && targetWeight < distances.get(edge.to())) {
            distances.put(edge.to(), targetWeight);
            predecessors.put(edge.to(), edge.from());
        }
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
