package p3.solver;

import p3.graph.Node;

import java.util.List;

/**
 * Interface for path calculators.
 *
 * @param <N> The type of the nodes in the graph.
 */
public interface PathCalculator<N> {

    /**
     * Calculate an optimal path between two nodes in a graph.
     *
     * <p>
     * The criteria for optimality is up to the implementation.
     * </p>
     *
     * <p>
     * The result is a list of along the path from the start node to the end node.
     * The start and the end node are both included in the returned list.
     * </p>
     *
     * @return A list representing the path found between the start and end nodes
     */
    List<Node<N>> calculatePath(Node<N> start, Node<N> end);
}
