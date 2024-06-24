package p3.graph;

import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.call;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.callObject;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class AdjacencyMatrixTest extends P3_TestBase {

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencymatrix/addEdge.json")
    public void testAddEdge(JsonParameterSet params) {
        List<List<Boolean>> matrixList = params.get("adjacencyMatrix");
        List<List<Boolean>> expectedMatrixList = params.get("expectedAdjacencyMatrix");
        int from = params.getInt("from");
        int to = params.getInt("to");

        boolean[][] matrix = listToMatrix(matrixList);
        boolean[][] expected = listToMatrix(expectedMatrixList);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyMatrix.addEdge")
            .add("previous matrix", Arrays.deepToString(matrix))
            .add("from", from)
            .add("to", to)
            .add("expected matrix", Arrays.deepToString(expected));

        AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(matrix);

        call(() -> adjacencyMatrix.addEdge(from, to), context.build(), result -> "AdjacencyMatrix.addEdge should not throw an exception");

        context.add("actual matrix", Arrays.deepToString(matrix));

        assertArrayDeepEquals(expected, matrix, context.build());
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencymatrix/hasEdge.json")
    public void testHasEdge(JsonParameterSet params) {
        List<List<Boolean>> matrixList = params.get("adjacencyMatrix");
        boolean expected = params.getBoolean("expected");
        int from = params.getInt("from");
        int to = params.getInt("to");

        boolean[][] matrix = listToMatrix(matrixList);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyMatrix.hasEdge")
            .add("matrix", Arrays.deepToString(matrix))
            .add("from", from)
            .add("to", to)
            .add("expected", expected);

        AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(matrix);

        boolean actual = callObject(() -> adjacencyMatrix.hasEdge(from, to), context.build(),
            result -> "AdjacencyMatrix.hasEdge should not throw an exception");

        context.add("actual", actual);

        assertEquals(expected, actual, context.build(), result -> "The method did not return the correct value");

        assertArrayDeepEquals(listToMatrix(matrixList), matrix, context.build());
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencymatrix/getAdjacentIndices.json")
    public void testGetAdjacentIndices(JsonParameterSet params) {
        List<List<Boolean>> matrixList = params.get("adjacencyMatrix");
        List<Integer> expected = params.get("expected");
        int index = params.getInt("index");

        boolean[][] matrix = listToMatrix(matrixList);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyMatrix.getAdjacentIndices")
            .add("matrix", Arrays.deepToString(matrix))
            .add("index", index)
            .add("expected", expected);

        AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(matrix);

        Set<Integer> actual = callObject(() -> adjacencyMatrix.getAdjacentIndices(index), context.build(),
            result -> "AdjacencyMatrix.getAdjacentIndices should not throw an exception");

        context.add("actual", actual);

        assertEquals(expected.size(), actual.size(), context.build(), result -> "The returned set does not have the correct size");

        for (int i : expected) {
            assertTrue(actual.contains(i), context.build(), result -> "The returned set does not contain the value %d".formatted(i));
        }

        assertArrayDeepEquals(listToMatrix(matrixList), matrix, context.build());
    }

    private void assertArrayDeepEquals(boolean[][] expected, boolean[][] actual, Context context) {

        for (int i = 0; i < expected.length; i++) {
            int finalI = i;

            for (int j = 0; j < expected.length; j++) {
                int finalJ = j;
                assertEquals(expected[i][j], actual[i][j], context,
                    result -> "The value at index (%d, %d) is not correct".formatted(finalI, finalJ));
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

}
