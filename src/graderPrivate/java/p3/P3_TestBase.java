package p3;

import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.graph.AdjacencyGraph;
import p3.graph.AdjacencyMatrix;
import p3.graph.AdjacencyRepresentation;
import p3.graph.Edge;
import p3.solver.BellmanFordPathCalculator;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.fail;

@SkipAfterFirstFailedTest
public abstract class P3_TestBase {

    public static void checkVerify(Runnable verifier, Context context, String msg) {
        try {
            verifier.run();
        } catch (AssertionError e) {
            fail(context, result -> msg + " Original error message:\n" + e.getMessage());
        } catch (Exception e) {
            fail(context, result -> "Unexpected Exception:\n" + e.getMessage());
        }
    }

    public static Set<Edge<Integer>> listToEdgeSet(List<List<Integer>> edges) {
        Set<Edge<Integer>> edgeSet = new HashSet<>();

        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.get(i).size(); j++) {
                int weight = edges.get(i).get(j);
                if (weight == 0) continue;
                edgeSet.add(Edge.of(i, j, weight));
            }
        }

        return edgeSet;
    }

    // Reflection helper methods

    public static boolean[][] getAdjacencyMatrix(AdjacencyMatrix matrix) throws ReflectiveOperationException {
        Field matrixField = AdjacencyMatrix.class.getDeclaredField("matrix");
        matrixField.setAccessible(true);
        return (boolean[][]) matrixField.get(matrix);
    }

    public static void setNodeToIndex(AdjacencyGraph<Integer> graph, Map<Integer, Integer> nodeToIndex) throws ReflectiveOperationException {
        Field nodeToIndexField = AdjacencyGraph.class.getDeclaredField("nodeToIndex");
        nodeToIndexField.setAccessible(true);
        nodeToIndexField.set(graph, nodeToIndex);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> getNodeToIndex(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        Field nodeToIndexField = AdjacencyGraph.class.getDeclaredField("nodeToIndex");
        nodeToIndexField.setAccessible(true);
        return (Map<Integer, Integer>) nodeToIndexField.get(graph);
    }

    public static void setIndexToNode(AdjacencyGraph<Integer> graph, Map<Integer, Integer> indexToNode) throws ReflectiveOperationException {
        Field indexToNodeField = AdjacencyGraph.class.getDeclaredField("indexToNode");
        indexToNodeField.setAccessible(true);
        indexToNodeField.set(graph, indexToNode);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> getIndexToNode(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        Field indexToNodeField = AdjacencyGraph.class.getDeclaredField("indexToNode");
        indexToNodeField.setAccessible(true);
        return (Map<Integer, Integer>) indexToNodeField.get(graph);
    }

    public static void setWeights(AdjacencyGraph<Integer> graph, Map<Integer, Map<Integer, Integer>> weights) throws ReflectiveOperationException {
        Field weightsField = AdjacencyGraph.class.getDeclaredField("weights");
        weightsField.setAccessible(true);
        weightsField.set(graph, weights);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, Map<Integer, Integer>> getWeights(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        Field weightsField = AdjacencyGraph.class.getDeclaredField("weights");
        weightsField.setAccessible(true);
        return (Map<Integer, Map<Integer, Integer>>) weightsField.get(graph);
    }

    public static AdjacencyRepresentation getRepresentation(AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        Field representationField = AdjacencyGraph.class.getDeclaredField("representation");
        representationField.setAccessible(true);
        return (AdjacencyRepresentation) representationField.get(graph);
    }

    public static void setPredecessors(BellmanFordPathCalculator<Integer> calculator, Map<Integer, Integer> predecessors) throws ReflectiveOperationException {
        Field predecessorsField = BellmanFordPathCalculator.class.getDeclaredField("predecessors");
        predecessorsField.setAccessible(true);
        predecessorsField.set(calculator, predecessors);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> getPredecessors(BellmanFordPathCalculator<Integer> calculator) throws ReflectiveOperationException {
        Field predecessorsField = BellmanFordPathCalculator.class.getDeclaredField("predecessors");
        predecessorsField.setAccessible(true);
        return (Map<Integer, Integer>) predecessorsField.get(calculator);
    }

    public static void setDistances(BellmanFordPathCalculator<Integer> calculator, Map<Integer, Integer> distances) throws ReflectiveOperationException {
        Field distancesField = BellmanFordPathCalculator.class.getDeclaredField("distances");
        distancesField.setAccessible(true);
        distancesField.set(calculator, distances);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> getDistances(BellmanFordPathCalculator<Integer> calculator) throws ReflectiveOperationException {
        Field distancesField = BellmanFordPathCalculator.class.getDeclaredField("distances");
        distancesField.setAccessible(true);
        return (Map<Integer, Integer>) distancesField.get(calculator);
    }
}
