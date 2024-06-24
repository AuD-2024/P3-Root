package p3.graph;

import org.junit.jupiter.params.ParameterizedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.call;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.callObject;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

public class AdjacencyListTest extends P3_TestBase {

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencylist/addEdge.json")
    public void testAddEdge(JsonParameterSet params) {
        List<List<Integer>> inputAdjacencyList = params.get("adjacencyList");
        List<List<Integer>> expectedAdjacencyList = params.get("expectedAdjacencyList");
        int from = params.getInt("from");
        int to = params.getInt("to");

        LinkedList<Integer>[] actualList = listToAdjacencyList(inputAdjacencyList);
        LinkedList<Integer>[] expectedList = listToAdjacencyList(expectedAdjacencyList);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyList.addEdge")
            .add("previous adjacencyList", Arrays.toString(actualList))
            .add("from", from)
            .add("to", to)
            .add("expected adjacencyList", Arrays.toString(expectedList));

        AdjacencyList adjacencyList = new AdjacencyList(actualList);

        call(() -> adjacencyList.addEdge(from, to), context.build(), result -> "AdjacencyList.addEdge should not throw an exception");

        context.add("actual adjacencyList", Arrays.toString(actualList));

        assertAdjacencyListEquals(expectedList, actualList, context.build());
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencylist/hasEdge.json")
    public void testHasEdge(JsonParameterSet params) {
        List<List<Integer>> inputAdjacencyList = params.get("adjacencyList");
        boolean expected = params.getBoolean("expected");
        int from = params.getInt("from");
        int to = params.getInt("to");

        LinkedList<Integer>[] actualList = listToAdjacencyList(inputAdjacencyList);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyList.hasEdge")
            .add("adjacencyList", Arrays.toString(actualList))
            .add("from", from)
            .add("to", to)
            .add("expected", expected);

        AdjacencyList adjacencyList = new AdjacencyList(actualList);

        boolean actual = callObject(() -> adjacencyList.hasEdge(from, to), context.build(), result -> "AdjacencyList.addEdge should not throw an exception");

        context.add("actual", actual);

        assertEquals(expected, actual, context.build(), result -> "The method did not return the correct value");

        assertAdjacencyListEquals(listToAdjacencyList(inputAdjacencyList), actualList, context.build());
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencylist/getAdjacentIndices.json")
    public void testGetAdjacentIndices(JsonParameterSet params) {
        List<List<Integer>> inputAdjacencyList = params.get("adjacencyList");
        List<Integer> expected = params.get("expected");
        int index = params.getInt("index");

        LinkedList<Integer>[] actualList = listToAdjacencyList(inputAdjacencyList);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyList.hasEdge")
            .add("adjacencyList", Arrays.toString(actualList))
            .add("index", index)
            .add("expected", expected);

        AdjacencyList adjacencyList = new AdjacencyList(actualList);

        Set<Integer> actual = callObject(() -> adjacencyList.getAdjacentIndices(index), context.build(),
            result -> "AdjacencyList.getAdjacentIndices should not throw an exception");

        context.add("actual", actual);

        assertEquals(expected.size(), actual.size(), context.build(), result -> "The returned set does not have the correct size");

        for (int i : expected) {
            assertTrue(actual.contains(i), context.build(), result -> "The returned set does not contain the value %d".formatted(i));
        }
    }

    private void assertAdjacencyListEquals(LinkedList<Integer>[] expected, LinkedList<Integer>[] actual, Context context) {
        for (int i = 0; i < expected.length; i++) {
            int finalI = i;
            LinkedList<Integer> expectedList = expected[i];
            LinkedList<Integer> actualList = actual[i];

            assertEquals(expectedList.size(), actualList.size(), context,
                result -> "The size of the linked list at index %d is not correct".formatted(finalI));

            for (int j = 0; j < expectedList.size(); j++) {
                int finalJ = j;
                assertEquals(expectedList.get(j), actualList.get(j), context,
                    result -> "The element at index %d of the linked list at index %d is not correct".formatted(finalJ, finalI));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private LinkedList<Integer>[] listToAdjacencyList(List<List<Integer>> list) {
        LinkedList<Integer>[] adjacencyList = new LinkedList[list.size()];

        for (int i = 0; i < list.size(); i++) {
            adjacencyList[i] = new LinkedList<>(list.get(i));
        }

        return adjacencyList;
    }

}
