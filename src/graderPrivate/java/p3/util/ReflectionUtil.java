package p3.util;

import p3.graph.AdjacencyGraph;
import p3.graph.AdjacencyList;
import p3.graph.AdjacencyMatrix;
import p3.graph.AdjacencyRepresentation;
import p3.solver.BellmanFordPathCalculator;
import p3.solver.PrimMSTCalculator;
import p3.solver.TopologicalSort;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ReflectionUtil {

    public static void setAdjacencyMatrix(AdjacencyMatrix matrix, boolean[][] matrixArray) throws ReflectiveOperationException {
        setField(matrix, "matrix", matrixArray);
    }

    public static boolean[][] getAdjacencyMatrix(AdjacencyMatrix matrix) throws ReflectiveOperationException {
        return getField(matrix, "matrix");
    }

    public static void setAdjacencyList(AdjacencyList graph, LinkedList<Integer>[] adjacencyList) throws ReflectiveOperationException {
        setField(graph, "adjacencyList", adjacencyList);
    }

    public static LinkedList<Integer>[] getAdjacencyList(AdjacencyList graph) throws ReflectiveOperationException {
        return getField(graph, "adjacencyList");
    }

    public static void setNodeToIndex(AdjacencyGraph<Integer> graph, Map<Integer, Integer> nodeToIndex) throws ReflectiveOperationException {
        setField(graph, "nodeToIndex", nodeToIndex);
    }

    public static Map<Integer, Integer> getNodeToIndex(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        return getField(graph, "nodeToIndex");
    }

    public static void setIndexToNode(AdjacencyGraph<Integer> graph, Map<Integer, Integer> indexToNode) throws ReflectiveOperationException {
        setField(graph, "indexToNode", indexToNode);
    }

    public static Map<Integer, Integer> getIndexToNode(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        return getField(graph, "indexToNode");
    }

    public static void setWeights(AdjacencyGraph<Integer> graph, Map<Integer, Map<Integer, Integer>> weights) throws ReflectiveOperationException {
        setField(graph, "weights", weights);
    }

    public static Map<Integer, Map<Integer, Integer>> getWeights(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        return getField(graph, "weights");
    }

    public static AdjacencyRepresentation getRepresentation(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        return getField(graph, "representation");
    }

    public static void setPredecessors(BellmanFordPathCalculator<Integer> calculator, Map<Integer, Integer> predecessors) throws ReflectiveOperationException {
        setField(calculator, "predecessors", predecessors);
    }

    public static void setDistances(BellmanFordPathCalculator<Integer> calculator, Map<Integer, Integer> distances) throws ReflectiveOperationException {
        setField(calculator, "distances", distances);
    }

    public static void setPredecessors(PrimMSTCalculator<Integer> calculator, Map<Integer, Integer> predecessors) throws ReflectiveOperationException {
        setField(calculator, "predecessors", predecessors);
    }

    public static void setKeys(PrimMSTCalculator<Integer> calculator, Map<Integer, Integer> keys) throws ReflectiveOperationException {
        setField(calculator, "keys", keys);
    }

    public static void setRemainingNodes(PrimMSTCalculator<Integer> calculator, Set<Integer> remainingNodes) throws ReflectiveOperationException {
        setField(calculator, "remainingNodes", remainingNodes);
    }

    public static void setDFS(TopologicalSort<Integer> topologicalSort, Object dfs) throws ReflectiveOperationException {
        setField(topologicalSort, "dfs", dfs);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object instance, String fieldName) throws ReflectiveOperationException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(instance);
    }

    private static void setField(Object instance, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

}
