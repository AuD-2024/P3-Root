package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;
import p3.graph.Node;

import java.util.HashSet;
import java.util.Set;

public class PrimMSTCalculator<N> implements MSTCalculator<N> {
    /**
     * The graph to calculate the MST for.
     */
    protected final Graph<N> graph;

    /**
     * The edges in the MST.
     */
    protected final Set<Edge<N>> mstEdges;

    public PrimMSTCalculator(Graph<N> graph) {
        this.graph = graph;
        this.mstEdges = new HashSet<>();
    }

    @Override
    public Graph<N> calculateMST() {
        init();

        for (Node<N> node : graph.getNodes()) {
            Set<Edge<N>> edges = node.getAdjacentEdges();
            Edge<N> minEdge = minimumWeight(edges);
            mstEdges.add(minEdge);
        }

        return Graph.of(graph.getNodes(), mstEdges);
    }

    protected void init() {
        mstEdges.clear();

        for (Node<N> node : graph.getNodes()) {}
    }


    protected Edge<N> minimumWeight(Set<Edge<N>> edges) {
        int min = Integer.MAX_VALUE;
        Edge<N> minEdge = null;

        for (Edge<N> edge : edges) {
            if (edge.weight() < min) {
                min = edge.weight();
                minEdge = edge;
            }
        }

        return minEdge;
    }
}
