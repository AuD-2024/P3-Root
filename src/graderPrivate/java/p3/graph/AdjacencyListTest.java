package p3.graph;

import org.junit.jupiter.params.ParameterizedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;
import static p3.util.AssertionUtil.assertEquals;
import static p3.util.AssertionUtil.assertSetEquals;
import static p3.util.ReflectionUtil.getAdjacencyList;
import static p3.util.ReflectionUtil.setAdjacencyList;

public class AdjacencyListTest extends P3_TestBase {

    @Override
    public String getTestedClassName() {
        return "AdjacencyList";
    }

    @Override
    public List<String> getOptionalParams() {
        return List.of("from", "to", "index", "adjacencyList", "expected");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencylist/addEdge.json")
    public void testAddEdge(JsonParameterSet params) throws ReflectiveOperationException {
        LinkedList<Integer>[] expectedList = listToAdjacencyList(params.get("expectedAdjacencyList"));

        Context.Builder<?> context = createContext(params, "addEdge", Map.of("expected adjacencyList", Arrays.toString(expectedList)));
        AdjacencyList adjacencyList = createAdjacencyList(params);

        call(() -> adjacencyList.addEdge(params.getInt("from"), params.getInt("to")), context, "addEdge");

        context.add("actual adjacencyList", Arrays.toString(getAdjacencyList(adjacencyList)));

        assertAdjacencyListEquals(expectedList, getAdjacencyList(adjacencyList), context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencylist/hasEdge.json")
    public void testHasEdge(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "hasEdge");
        AdjacencyList adjacencyList = createAdjacencyList(params);

        boolean actual = callObject(() -> adjacencyList.hasEdge(params.getInt("from"), params.getInt("to")), context, "addEdge");

        context.add("actual", actual);

        assertEquals(params.getBoolean("expected"), actual, context, "The method did not return the correct value");
        assertAdjacencyListEquals(listToAdjacencyList(params.get("adjacencyList")), getAdjacencyList(adjacencyList), context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencylist/getAdjacentIndices.json")
    public void testGetAdjacentIndices(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "getAdjacentIndices");
        AdjacencyList adjacencyList = createAdjacencyList(params);

        Set<Integer> actual = callObject(() -> adjacencyList.getAdjacentIndices(params.getInt("index")), context, "getAdjacentIndices");

        context.add("actual", actual);

        assertSetEquals(new HashSet<>(params.get("expected")), actual, context, "The method did not return the correct value");
        assertAdjacencyListEquals(listToAdjacencyList(params.get("adjacencyList")), getAdjacencyList(adjacencyList), context);
    }

    private void assertAdjacencyListEquals(LinkedList<Integer>[] expected, LinkedList<Integer>[] actual, Context.Builder<?> context) {
        for (int i = 0; i < expected.length; i++) {
            LinkedList<Integer> expectedList = expected[i];
            LinkedList<Integer> actualList = actual[i];

            assertEquals(expectedList.size(), actualList.size(), context, "The size of the linked list at index %d is not correct".formatted(i));

            for (int j = 0; j < expectedList.size(); j++) {
                assertEquals(expectedList.get(j), actualList.get(j), context, "The element at index %d of the linked list at index %d is not correct".formatted(j, i));
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

    private AdjacencyList createAdjacencyList(JsonParameterSet params) throws ReflectiveOperationException {
        LinkedList<Integer>[] list = listToAdjacencyList(params.get("adjacencyList"));
        AdjacencyList adjacencyList = new AdjacencyList(list.length);
        setAdjacencyList(adjacencyList, list);
        return adjacencyList;
    }

}
