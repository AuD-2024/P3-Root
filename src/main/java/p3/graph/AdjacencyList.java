package p3.graph;

import p3.solver.MSTCalculator;

import java.util.LinkedList;
import java.util.Set;

/**
 * A representation of a directed graph using an array of linked lists.
 * <p>
 * The array is accessed with the indices of the nodes. The list at index {@code i} contains the indices of the nodes
 * the node with index {@code i} is connected to.
 *
 * @see AdjacencyRepresentation
 */
public class AdjacencyList implements AdjacencyRepresentation {

    /**
     * A factory that creates an empty {@link AdjacencyList} with the given initial size.
     */
    public static final AdjacencyRepresentation.Factory FACTORY = AdjacencyList::new;

    /**
     * The underlying array that stores the adjacencyList.
     */
    private LinkedList<Integer>[] adjacencyList;

    /**
     * Creates a new {@link AdjacencyList} with the given size.
     * <p>
     * Initially, the adjacencyList is empty, i.e., no connections between nodes exist.
     *
     * @param size The amount of nodes in the graph.
     */
    @SuppressWarnings("unchecked")
    public AdjacencyList(int size) {
        adjacencyList = new LinkedList[size];

        for (int i = 0; i < size; i++) {
            adjacencyList[i] = new LinkedList<>();
        }
    }

    @Override
    public void addEdge(int from, int to) {
        checkIndex(to);
        if (!adjacencyList[from].contains(to)) {
            adjacencyList[from].add(to);
        }
    }

    @Override
    public boolean hasEdge(int from, int to) {
        checkIndex(to);
        return adjacencyList[from].contains(to);
    }

    @Override
    public Set<Integer> getAdjacentIndices(int index) {
        return Set.copyOf(adjacencyList[index]);
    }

    @Override
    public int size() {
        return adjacencyList.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void grow() {
        LinkedList<Integer>[] newAdjacencyList = new LinkedList[adjacencyList.length + 1];

        System.arraycopy(adjacencyList, 0, newAdjacencyList, 0, adjacencyList.length);

        newAdjacencyList[adjacencyList.length] = new LinkedList<>();

        adjacencyList = newAdjacencyList;
    }

    /**
     * Checks whether the given index is in the range of the adjacencyList and throws an {@link IndexOutOfBoundsException}
     * if it is not.
     *
     * @param index the index to check.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= adjacencyList.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    /**
     * A factory for creating new instances of {@link AdjacencyList}.
     */
    interface Factory {

        /**
         * Create a new instance of {@link AdjacencyList} for the given graph.
         *
         * @param graph the graph to calculate the MST for.
         * @param <N>   The type of the nodes in the graph.
         * @return a new instance of {@link MSTCalculator}.
         */
        <N> MSTCalculator<N> create(Graph<N> graph);
    }
}
