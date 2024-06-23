package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.*;

public class BellmanFordPathCalculator<N> implements PathCalculator<N> {
    public static PathCalculator.Factory FACTORY = BellmanFordPathCalculator::new;

    /**
     * The graph to calculate paths in.
     */
    protected Graph<N> graph;

    /**
     * The distance from the start node to each node in the graph.
     */
    protected final Map<N, Integer> distances;

    /**
     * The predecessor of each node in the graph along the shortest path to the start node.
     */
    protected final Map<N, N> predecessors;

    public BellmanFordPathCalculator(Graph<N> graph) {
        this.graph = graph;
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
    }

    @Override
    public List<N> calculatePath(N start, N end) {
        initSSSP(start);
        processGraph();

        Set<Edge<N>> negativeCycles = checkNegativeCycles();
        if (negativeCycles.isEmpty()) {
            return reconstructPath(start, end);
        } else {
            throw new CycleException("A negative cycle was detected");
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
     * This method processes the given graph with the BellmanFord algorithm.
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
        int distance = distances.get(edge.from());
        int weight = distance + edge.weight();
        if (distance != Integer.MAX_VALUE && weight < distances.get(edge.to())) {
            distances.put(edge.to(), weight);
            predecessors.put(edge.to(), edge.from());
        }
    }

    /**
     * This method checks the graph for cyclic edges.
     *
     *
     * @return all cyclic edges in the graph
     */
    protected Set<Edge<N>> checkNegativeCycles() {
        Set<Edge<N>> cyclicEdges = new HashSet<>();

        for (Edge<N> edge : graph.getEdges()) {
            int src = distances.get(edge.from());
            int dest = distances.get(edge.to());
            if (src != Integer.MAX_VALUE && src + edge.weight() < dest) {
                cyclicEdges.add(edge);
            }
        }

        return cyclicEdges;
    }

    /**
     * This method reconstructs the path between two vertices in the graph.
     *
     * @param start Start node in the graph
     * @param end End node in the graph
     * @return List of nodes between start and end nodes in the graph
     */
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
