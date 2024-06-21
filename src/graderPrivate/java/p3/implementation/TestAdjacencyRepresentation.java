package p3.implementation;

import p3.graph.AdjacencyRepresentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestAdjacencyRepresentation implements AdjacencyRepresentation {

    private Map<Integer, Map<Integer, Boolean>> adjacencyMap = new HashMap<>();

    private int size = 0;

    private boolean growEnabled = true;
    private boolean addEdgeEnabled = true;

    public TestAdjacencyRepresentation(int size) {
        this.size = size;
    }

    public void disableGrow() {
        growEnabled = false;
    }

    public void disableAddEdge() {
        addEdgeEnabled = false;
    }

    @Override
    public void addEdge(int from, int to) {
        if (!addEdgeEnabled) {
            throw new UnsupportedOperationException("representation.addEdge() should not be called");
        }

        checkIndex(from);
        checkIndex(to);

        adjacencyMap.computeIfAbsent(from, k -> new HashMap<>()).put(to, true);
    }

    @Override
    public boolean hasEdge(int from, int to) {
        checkIndex(from);
        checkIndex(to);

        return adjacencyMap.getOrDefault(from, Map.of()).getOrDefault(to, false);
    }

    @Override
    public Set<Integer> getAdjacentIndices(int index) {
        checkIndex(index);
        return adjacencyMap.getOrDefault(index, Map.of()).entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void grow() {
        if (!growEnabled) {
            throw new UnsupportedOperationException("representation.grow() should not be called");
        }
        size++;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

}
