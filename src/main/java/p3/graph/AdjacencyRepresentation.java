package p3.graph;

import java.util.Set;

/**
 * An interface for representing a directed graph using adjacency information for each node, i.e., storing for each node the
 * nodes it is connected/adjacent to, i.e., the nodes wo which it has an edge.
 * <p>
 * The nodes are represented with unique indices in the range {@code [0, size() - 1]}. The user of this interface is responsible for
 * maintaining the mapping between the indices and the nodes.
 * <p>
 * The representation is directed, i.e., an edge from node {@code A} to node {@code B} is different from an
 * edge from node {@code B} to node {@code A}. The existence of one of them does not imply the existence of the other.
 * <p>
 * The representation assumes a fixed number of nodes. The number of nodes can be increased by calling {@link #grow()},
 * however, this usually comes with a performance penalty.
 *
 * @see AdjacencyMatrix
 * @see AdjacencyList
 */
public interface AdjacencyRepresentation {

    /**
     * Adds an edge between the given indices, i.e., adds a connection from the node at index {@code from} to the node
     * at index {@code to}.
     *
     * @param from the index of the node the edge starts at.
     * @param to   the index of the node the edge ends at.
     * @throws IndexOutOfBoundsException if either {@code from} or {@code to} is not in the range {@code [0, size() - 1]}.
     */
    void addEdge(int from, int to);

    /**
     * Returns whether there is an edge that starts at the node with index {@code from} at ends at the node with index {@code to}.
     *
     * @param from The index of the node the edge starts at.
     * @param to   The index of the node the edge ends at.
     * @return {@code true} if there is an edge between the nodes at the given indices, {@code false} otherwise.
     * @throws IndexOutOfBoundsException if either {@code from} or {@code to} is not in the range {@code [0, size() - 1]}.
     */
    boolean hasEdge(int from, int to);

    /**
     * Returns the indices of the nodes that are adjacent to the node at the given index.
     *
     * @param index the index of the node to get the adjacent nodes of.
     * @return a set of the indices of the nodes that are adjacent to the node at the given index.
     * @throws IndexOutOfBoundsException if the given index is not in the range {@code [0, size() - 1]}.
     */
    Set<Integer> getAdjacentIndices(int index);

    /**
     * Returns the number of represented nodes.
     *
     * @return the number of represented nodes.
     */
    int size();

    /**
     * Increases the size of the representation, i.e., the number of represented nodes, by one. The information about
     * the previous nodes is preserved.
     * <p>
     * This method effectively adds a new, unconnected node with index {@link #size()} to the graph.
     * <p>
     * Note that this method usually comes with a performance penalty, as it may require copying the entire
     * representation to a new, larger one.
     */
    void grow();

}
