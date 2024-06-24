package p3.solver;

import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;
import p3.graph.Edge;
import p3.graph.Graph;
import p3.implementation.TestGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

public class BellmanFordCalculatorTest extends P3_TestBase {

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/initSSSP.json")
    public void testInitSSSP(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));
        int start = params.getInt("start");
        List<Integer> predecessorList = params.get("predecessors");
        List<Integer> distanceList = params.get("distances");
        List<Integer> expectedPredecessorList = params.get("expectedPredecessors");
        List<Integer> expectedDistanceList = params.get("expectedDistances");

        Map<Integer, Integer> predecessors = createPredecessorMap(predecessorList, nodes);
        Map<Integer, Integer> distances = createDistanceMap(distanceList, nodes);

        Map<Integer, Integer> expectedPredecessors = createPredecessorMap(expectedPredecessorList, nodes);
        Map<Integer, Integer> expectedDistances = createDistanceMap(expectedDistanceList, nodes);

        Graph<Integer> graph = new TestGraph<>(new HashSet<>(nodes), edges);
        BellmanFordPathCalculator<Integer> calculator = new BellmanFordPathCalculator<>(graph);

        setPredecessors(calculator, predecessors);
        setDistances(calculator, distances);

        Context.Builder<?> context = contextBuilder()
            .subject("BellmanFordPathCalculator.initSSSP")
            .add("nodes", nodes)
            .add("start node", start)
            .add("previous predecessors", predecessors.toString())
            .add("previous distances", distances.toString())
            .add("expected predecessors", expectedPredecessors.toString())
            .add("expected distances", expectedDistances.toString());

        call(() -> calculator.initSSSP(start), context.build(), result -> "initSSSP should not throw an exception");

        context.add("actual predecessors", predecessors.toString());
        context.add("actual distances", distances.toString());

        assertMapEquals(expectedPredecessors, predecessors, context.build(), "predecessor");
        assertMapEquals(expectedDistances, distances, context.build(), "distance");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/relax.json")
    public void testRelax(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));
        int from = params.getInt("from");
        int to = params.getInt("to");
        int weight = params.getInt("weight");
        List<Integer> predecessorList = params.get("predecessors");
        List<Integer> distanceList = params.get("distances");
        List<Integer> expectedPredecessorList = params.get("expectedPredecessors");
        List<Integer> expectedDistanceList = params.get("expectedDistances");

        Edge<Integer> edge = Edge.of(from, to, weight);

        Map<Integer, Integer> predecessors = createPredecessorMap(predecessorList, nodes);
        Map<Integer, Integer> distances = createDistanceMap(distanceList, nodes);

        Map<Integer, Integer> expectedPredecessors = createPredecessorMap(expectedPredecessorList, nodes);
        Map<Integer, Integer> expectedDistances = createDistanceMap(expectedDistanceList, nodes);

        Graph<Integer> graph = new TestGraph<>(new HashSet<>(nodes), edges);
        BellmanFordPathCalculator<Integer> calculator = new BellmanFordPathCalculator<>(graph);

        setPredecessors(calculator, predecessors);
        setDistances(calculator, distances);

        Context.Builder<?> context = contextBuilder()
            .subject("BellmanFordPathCalculator.relax")
            .add("nodes", nodes)
            .add("edgeToRelax", edge)
            .add("previous predecessors", predecessors.toString())
            .add("previous distances", distances.toString())
            .add("expected predecessors", expectedPredecessors.toString())
            .add("expected distances", expectedDistances.toString());

        call(() -> calculator.relax(edge), context.build(), result -> "relax should not throw an exception");

        context.add("actual predecessors", predecessors.toString());
        context.add("actual distances", distances.toString());

        assertMapEquals(expectedPredecessors, predecessors, context.build(), "predecessor");
        assertMapEquals(expectedDistances, distances, context.build(), "distance");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/processGraph.json")
    public void testProcessGraph(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));

        Graph<Integer> graph = new TestGraph<>(new HashSet<>(nodes), edges);
        BellmanFordPathCalculator<Integer> calculator = spy(new BellmanFordPathCalculator<>(graph));

        ArgumentCaptor<Edge<Integer>> edgeCaptor = ArgumentCaptor.forClass(Edge.class);
        doNothing().when(calculator).relax(edgeCaptor.capture());

        Context.Builder<?> context = contextBuilder()
            .subject("BellmanFordPathCalculator.processGraph")
            .add("nodes", nodes)
            .add("edges", edges);

        call(calculator::processGraph, context.build(), result -> "processGraph should not throw an exception");

        int expectedCount = edges.size() * (nodes.size() - 1);
        assertEquals(expectedCount, edgeCaptor.getAllValues().size(), context.build(),
            result -> "The relax method should be called (nodes.size - 1) * edges.size times");

        List<Edge<Integer>> currentIterationEdges = new ArrayList<>();
        for (int i = 0; i < expectedCount; i++) {
            currentIterationEdges.add(edgeCaptor.getAllValues().get(i));

            if (currentIterationEdges.size() == edges.size()) {
                for (Edge<Integer> edge : edges) {
                    int iteration = i / edges.size();
                    assertTrue(currentIterationEdges.contains(edge), context.build(),
                        result -> "The edges in iteration %d (relax invocation %d to %d) do not contain edge %s"
                            .formatted(iteration, iteration * edges.size(), (iteration + 1) * edges.size(), edge));
                }
            }
        }

        assertTrue(getPredecessors(calculator).isEmpty(), context.build(), result -> "The predecessors map should not change");
        assertTrue(getDistances(calculator).isEmpty(), context.build(), result -> "The distances map should not change");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/checkNegativeCycles.json")
    public void testCheckNegativeCycles(JsonParameterSet params) throws ReflectiveOperationException {
        testCheckNegativeCycles(params, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/checkNegativeCycles.json")
    public void testCheckNegativeCyclesExact(JsonParameterSet params) throws ReflectiveOperationException {
        testCheckNegativeCycles(params, true);
    }

    private void testCheckNegativeCycles(JsonParameterSet params, boolean exact) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));
        List<Integer> predecessorList = params.get("predecessors");
        List<Integer> distanceList = params.get("distances");
        Set<Edge<Integer>> expectedEdges = listToEdgeSet(params.get("expectedEdges"));

        Map<Integer, Integer> predecessors = createPredecessorMap(predecessorList, nodes);
        Map<Integer, Integer> distances = createDistanceMap(distanceList, nodes);

        Graph<Integer> graph = new TestGraph<>(new HashSet<>(nodes), edges);
        BellmanFordPathCalculator<Integer> calculator = new BellmanFordPathCalculator<>(graph);

        setPredecessors(calculator, predecessors);
        setDistances(calculator, distances);

        Context.Builder<?> context = contextBuilder()
            .subject("BellmanFordPathCalculator.checkNegativeCycles")
            .add("nodes", nodes)
            .add("edges", edges)
            .add("predecessors", predecessors)
            .add("distances", distances)
            .add("expectedEdges", expectedEdges);

        Set<Edge<Integer>> actualEdges = callObject(calculator::checkNegativeCycles, context.build(),
            result -> "checkNegativeCycles should not throw an exception");

        context.add("actualEdges", actualEdges);

        assertNotNull(actualEdges, context.build(), result -> "The method should not return null");

        if (expectedEdges.isEmpty()) {
            assertTrue(actualEdges.isEmpty(), context.build(), result -> "The method should return an empty set");
        } else {
            if (exact) {
                assertEquals(expectedEdges.size(), actualEdges.size(), context.build(), result -> "The returned set does not have the correct size");

                for (Edge<Integer> edge : expectedEdges) {
                    assertTrue(actualEdges.contains(edge), context.build(), result -> "The returned set does not contain the edge %s".formatted(edge));
                    assertEquals(edge.weight(), actualEdges.stream().filter(e -> e.equals(edge)).findFirst().get().weight(),
                        context.build(), result -> "The returned edge %s has the wrong weight".formatted(edge));
                }
            } else {
                assertTrue(!actualEdges.isEmpty(), context.build(), result -> "The returned set should not be empty");
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/calculatePath.json")
    public void testCalculatePath(JsonParameterSet params) {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));
        int start = params.getInt("start");
        int end = params.getInt("end");
        Set<Edge<Integer>> negativeCycleEdges = listToEdgeSet(params.get("negativeCycleEdges"));
        boolean shouldThrowException = params.getBoolean("shouldThrowException");

        List<Edge<Integer>> resultList = new ArrayList<>();

        Graph<Integer> graph = new TestGraph<>(new HashSet<>(nodes), edges);
        BellmanFordPathCalculator<Integer> calculator = spy(new BellmanFordPathCalculator<>(graph));

        ArgumentCaptor<Integer> initSSSPCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> reconstructPathStartCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> reconstructPathEndCaptor = ArgumentCaptor.forClass(Integer.class);

        doNothing().when(calculator).initSSSP(initSSSPCaptor.capture());
        doNothing().when(calculator).processGraph();
        doReturn(negativeCycleEdges).when(calculator).checkNegativeCycles();
        doReturn(resultList).when(calculator).reconstructPath(reconstructPathStartCaptor.capture(), reconstructPathEndCaptor.capture());

        InOrder inOrder = inOrder(calculator);

        Context context = contextBuilder()
            .subject("BellmanFordPathCalculator.calculatePath")
            .add("nodes", nodes)
            .add("edges", edges)
            .add("negativeCycleEdges", negativeCycleEdges)
            .add("shouldThrowException", shouldThrowException)
            .build();

        boolean cycleExceptionThrown = false;

        try {
            List<Integer> actual = calculator.calculatePath(start, end);
            assertSame(resultList, actual, context, result -> "The method should return the result of reconstructPath");
        } catch (CycleException e) {
            cycleExceptionThrown = true;
        } catch (AssertionError e) {
            throw e;
        } catch (Throwable e) {
            call(() -> {
                throw e;
            }, context, result -> "The method should not throw any other exception than CycleException");
        }

        if (cycleExceptionThrown && !shouldThrowException) {
            fail(context, result -> "The method should not throw a CycleException but it did");
        } else if (!cycleExceptionThrown && shouldThrowException) {
            fail(context, result -> "The method should throw a CycleException but it did not");
        }

        checkVerify(() -> inOrder.verify(calculator).initSSSP(any()), context, "initSSSP should be called exactly once");
        assertEquals(start, initSSSPCaptor.getValue(), context, result -> "initSSSP should be called with the start node");

        checkVerify(() -> inOrder.verify(calculator).processGraph(), context, "processGraph should be called exactly once after initSSSP");
        checkVerify(() -> inOrder.verify(calculator).checkNegativeCycles(), context, "checkNegativeCycles should be called exactly once after processGraph");

        if (!shouldThrowException) {
            checkVerify(() -> inOrder.verify(calculator).reconstructPath(any(), any()), context, "reconstructPath should be called exactly once after checkNegativeCycles");
            assertEquals(start, reconstructPathStartCaptor.getValue(), context, result -> "reconstructPath should be called with the start node as the first parameter");
            assertEquals(end, reconstructPathEndCaptor.getValue(), context, result -> "reconstructPath should be called with the end node as the second parameter");
        }

    }

    private void assertMapEquals(Map<Integer, Integer> expected, Map<Integer, Integer> actual, Context context, String mapName) {
        assertEquals(expected.size(), actual.size(), context, result -> "The size of the %s map is not correct".formatted(mapName));

        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertTrue(actual.containsKey(entry.getKey()), context, result -> "%s map should contain key %d".formatted(mapName, entry.getKey()));
            assertEquals(entry.getValue(), actual.get(entry.getKey()), context, result -> "%s map contains the wrong value for key %d".formatted(mapName, entry.getKey()));
        }
    }

    private Map<Integer, Integer> createPredecessorMap(List<Integer> predecessorList, List<Integer> nodes) {
        Map<Integer, Integer> predecessors = new HashMap<>();

        for (int i = 0; i < nodes.size(); i++) {
            predecessors.put(nodes.get(i), predecessorList.get(i));
        }

        return predecessors;
    }

    private Map<Integer, Integer> createDistanceMap(List<Integer> distanceList, List<Integer> nodes) {
        Map<Integer, Integer> distances = new HashMap<>();

        for (int i = 0; i < nodes.size(); i++) {
            Integer distance = distanceList.get(i);
            distances.put(nodes.get(i), distance == null ? Integer.MAX_VALUE : distance);
        }

        return distances;
    }

}
