package p3.graph;

import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static p3.util.AssertionUtil.assertEquals;
import static p3.util.AssertionUtil.assertSetEquals;
import static p3.util.ReflectionUtil.getAdjacencyMatrix;
import static p3.util.ReflectionUtil.setAdjacencyMatrix;

@TestForSubmission
public class AdjacencyMatrixTest extends P3_TestBase {

    @Override
    public String getTestedClassName() {
        return "AdjacencyMatrix";
    }

    @Override
    public List<String> getOptionalParams() {
        return List.of("from", "to", "matrix", "index", "expected");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencymatrix/addEdge.json")
    public void testAddEdge(JsonParameterSet params) throws ReflectiveOperationException {
        boolean[][] expected = listToMatrix(params.get("expectedAdjacencyMatrix"));

        Context.Builder<?> context = createContext(params, "addEdge", Map.of("expected matrix", Arrays.deepToString(expected)));
        AdjacencyMatrix adjacencyMatrix = createAdjacencyMatrix(params);

        call(() -> adjacencyMatrix.addEdge(params.getInt("from"), params.getInt("to")), context, "addEdge");

        context.add("actual matrix", Arrays.deepToString(getAdjacencyMatrix(adjacencyMatrix)));

        assertArrayDeepEquals(expected, getAdjacencyMatrix(adjacencyMatrix), context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencymatrix/hasEdge.json")
    public void testHasEdge(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "hasEdge");
        AdjacencyMatrix adjacencyMatrix = createAdjacencyMatrix(params);

        boolean actual = callObject(() -> adjacencyMatrix.hasEdge(params.getInt("from"), params.getInt("to")), context, "hasEdge");

        context.add("actual", actual);

        assertEquals(params.getBoolean("expected"), actual, context, "The method did not return the correct value");
        assertArrayDeepEquals(listToMatrix(params.get("adjacencyMatrix")), getAdjacencyMatrix(adjacencyMatrix), context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencymatrix/getAdjacentIndices.json")
    public void testGetAdjacentIndices(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "getAdjacentIndices");
        AdjacencyMatrix adjacencyMatrix = createAdjacencyMatrix(params);

        Set<Integer> actual = callObject(() -> adjacencyMatrix.getAdjacentIndices(params.getInt("index")), context, "getAdjacentIndices");

        context.add("actual", actual);

        assertSetEquals(new HashSet<>(params.get("expected")), actual, context, "returned");
        assertArrayDeepEquals(listToMatrix(params.get("adjacencyMatrix")), getAdjacencyMatrix(adjacencyMatrix), context);
    }

    private void assertArrayDeepEquals(boolean[][] expected, boolean[][] actual, Context.Builder<?> context) {
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                assertEquals(expected[i][j], actual[i][j], context, "The value at index (%d, %d) is not correct".formatted(i, j));
            }
        }
    }

    private boolean[][] listToMatrix(List<List<Boolean>> matrixList) {
        boolean[][] matrix = new boolean[matrixList.size()][matrixList.size()];

        for (int i = 0; i < matrixList.size(); i++) {
            for (int j = 0; j < matrixList.size(); j++) {
                matrix[i][j] = matrixList.get(i).get(j);
            }
        }

        return matrix;
    }

    private AdjacencyMatrix createAdjacencyMatrix(JsonParameterSet params) throws ReflectiveOperationException {
        boolean[][] matrix = listToMatrix(params.get("adjacencyMatrix"));
        AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(matrix.length);
        setAdjacencyMatrix(adjacencyMatrix, matrix);
        return adjacencyMatrix;
    }

}
