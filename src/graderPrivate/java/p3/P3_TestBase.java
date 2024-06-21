package p3;

import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.graph.AdjacencyGraph;
import p3.graph.AdjacencyMatrix;
import p3.graph.AdjacencyRepresentation;

import java.lang.reflect.Field;
import java.util.Map;

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
}
