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
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;
import static p3.util.AssertionUtil.assertEquals;
import static p3.util.AssertionUtil.assertMapEquals;
import static p3.util.AssertionUtil.assertNotNull;
import static p3.util.AssertionUtil.assertSame;
import static p3.util.AssertionUtil.assertTrue;
import static p3.util.AssertionUtil.fail;
import static p3.util.ReflectionUtil.setDistances;
import static p3.util.ReflectionUtil.setPredecessors;

public class BellmanFordCalculatorTest extends P3_TestBase {

    @Override
    public String getTestedClassName() {
        return "BellmanFordPathCalculator";
    }

    @Override
    public List<String> getOptionalParams() {
        return List.of("nodes", "edges", "start", "predecessors", "distances", "expectedPredecessors", "expectedDistances");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/initSSSP.json")
    public void testInitSSSP(JsonParameterSet params) throws ReflectiveOperationException {
        BellmanFordPathCalculator<Integer> calculator = createCalculator(params);
        Context.Builder<?> context = createContext(params, "initSSSP");

        call(() -> calculator.initSSSP(params.getInt("start")), context, "initSSSP");

        assertMapsCorrect(params, calculator, context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/relax.json")
    public void testRelax(JsonParameterSet params) throws ReflectiveOperationException {
        Edge<Integer> edge = Edge.of(params.getInt("from"), params.getInt("to"), params.getInt("weight"));

        BellmanFordPathCalculator<Integer> calculator = createCalculator(params);
        Context.Builder<?> context = createContext(params, "relax", Map.of("edge to relax", edge));

        call(() -> calculator.relax(edge), context, "relax");

        assertMapsCorrect(params, calculator, context);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/processGraph.json")
    public void testProcessGraph(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = getEdges(params);

        BellmanFordPathCalculator<Integer> calculator = createCalculator(params, true);

        ArgumentCaptor<Edge<Integer>> edgeCaptor = ArgumentCaptor.forClass(Edge.class);
        doNothing().when(calculator).relax(edgeCaptor.capture());

        Context.Builder<?> context = createContext(params, "processGraph");

        call(calculator::processGraph, context, "processGraph");

        int expectedCount = edges.size() * (nodes.size() - 1);
        assertEquals(expectedCount, edgeCaptor.getAllValues().size(), context, "The relax method should be called (nodes.size - 1) * edges.size times");

        List<Edge<Integer>> currentIterationEdges = new ArrayList<>();
        for (int i = 0; i < expectedCount; i++) {
            currentIterationEdges.add(edgeCaptor.getAllValues().get(i));

            if (currentIterationEdges.size() == edges.size()) {
                for (Edge<Integer> edge : edges) {
                    int iteration = i / edges.size();
                    assertTrue(currentIterationEdges.contains(edge), context, "The edges in iteration %d (relax invocation %d to %d) do not contain edge %s"
                        .formatted(iteration, iteration * edges.size(), (iteration + 1) * edges.size(), edge));
                }
            }
        }

        assertTrue(calculator.predecessors.isEmpty(), context, "The predecessors map should not change");
        assertTrue(calculator.distances.isEmpty(), context, "The distances map should not change");
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

    // TODO bleiben wir dabei das set returned wird?
    private void testCheckNegativeCycles(JsonParameterSet params, boolean exact) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = getEdges(params);

        Map<Integer, Integer> predecessors = createPredecessorMap(params, "predecessors");
        Map<Integer, Integer> distances = createDistanceMap(params, "distances");

        Set<Edge<Integer>> expectedEdges = getEdges(params, "expectedEdges");

        Graph<Integer> graph = new TestGraph<>(nodes, edges);
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

        Set<Edge<Integer>> actualEdges = callObject(calculator::checkNegativeCycles, context, "checkNegativeCycles");

        context.add("actualEdges", actualEdges);

        assertNotNull(actualEdges, context, "The method should not return null");

        if (expectedEdges.isEmpty()) {
            assertTrue(actualEdges.isEmpty(), context, "The method should return an empty set");
        } else {
            if (exact) {
                assertEquals(expectedEdges.size(), actualEdges.size(), context, "The returned set does not have the correct size");

                for (Edge<Integer> edge : expectedEdges) {
                    assertTrue(actualEdges.contains(edge), context, "The returned set does not contain the edge %s".formatted(edge));
                    assertEquals(edge.weight(), actualEdges.stream().filter(e -> e.equals(edge)).findFirst().get().weight(),
                        context, "The returned edge %s has the wrong weight".formatted(edge));
                }
            } else {
                assertTrue(!actualEdges.isEmpty(), context, "The returned set should not be empty");
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "bellmanford/calculatePath.json")
    public void testCalculatePath(JsonParameterSet params) {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = getEdges(params);

        int start = params.getInt("start");
        int end = params.getInt("end");

        Set<Edge<Integer>> negativeCycleEdges = getEdges(params, "negativeCycleEdges");
        boolean shouldThrowException = params.getBoolean("shouldThrowException");

        List<Edge<Integer>> resultList = new ArrayList<>();

        Graph<Integer> graph = new TestGraph<>(nodes, edges);
        BellmanFordPathCalculator<Integer> calculator = spy(new BellmanFordPathCalculator<>(graph));

        ArgumentCaptor<Integer> initSSSPCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> reconstructPathStartCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> reconstructPathEndCaptor = ArgumentCaptor.forClass(Integer.class);

        doNothing().when(calculator).initSSSP(initSSSPCaptor.capture());
        doNothing().when(calculator).processGraph();
        doReturn(negativeCycleEdges).when(calculator).checkNegativeCycles();
        doReturn(resultList).when(calculator).reconstructPath(reconstructPathStartCaptor.capture(), reconstructPathEndCaptor.capture());

        InOrder inOrder = inOrder(calculator);

        Context.Builder<?> context = contextBuilder()
            .subject("BellmanFordPathCalculator.calculatePath")
            .add("nodes", nodes)
            .add("edges", edges)
            .add("negativeCycleEdges", negativeCycleEdges)
            .add("shouldThrowException", shouldThrowException);

        boolean cycleExceptionThrown = false;

        try {
            List<Integer> actual = calculator.calculatePath(start, end);
            assertSame(resultList, actual, context, "The method should return the result of reconstructPath");
        } catch (CycleException e) {
            cycleExceptionThrown = true;
        } catch (AssertionError e) {
            throw e;
        } catch (Throwable e) {
            call(() -> {
                throw e;
            }, context, "The method should not throw any other exception than CycleException");
        }

        if (cycleExceptionThrown && !shouldThrowException) {
            fail(context, "The method should not throw a CycleException but it did");
        } else if (!cycleExceptionThrown && shouldThrowException) {
            fail(context, "The method should throw a CycleException but it did not");
        }

        checkVerify(() -> inOrder.verify(calculator).initSSSP(any()), context, "initSSSP should be called exactly once");
        assertEquals(start, initSSSPCaptor.getValue(), context, "initSSSP should be called with the start node");

        checkVerify(() -> inOrder.verify(calculator).processGraph(), context, "processGraph should be called exactly once after initSSSP");
        checkVerify(() -> inOrder.verify(calculator).checkNegativeCycles(), context, "checkNegativeCycles should be called exactly once after processGraph");

        if (!shouldThrowException) {
            checkVerify(() -> inOrder.verify(calculator).reconstructPath(any(), any()), context, "reconstructPath should be called exactly once after checkNegativeCycles");
            assertEquals(start, reconstructPathStartCaptor.getValue(), context, "reconstructPath should be called with the start node as the first parameter");
            assertEquals(end, reconstructPathEndCaptor.getValue(), context, "reconstructPath should be called with the end node as the second parameter");
        }

    }

    private BellmanFordPathCalculator<Integer> createCalculator(JsonParameterSet params) throws ReflectiveOperationException {
        return createCalculator(params, false);
    }

    private BellmanFordPathCalculator<Integer> createCalculator(JsonParameterSet params, boolean spy) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = params.availableKeys().contains("edges") ? getEdges(params) : Set.of();

        Graph<Integer> graph = new TestGraph<>(nodes, edges);
        BellmanFordPathCalculator<Integer> calculator = spy ? spy(new BellmanFordPathCalculator<>(graph)) : new BellmanFordPathCalculator<>(graph);

        if (params.availableKeys().contains("predecessors")) {
            setPredecessors(calculator, createPredecessorMap(params, "predecessors"));
        }
        if (params.availableKeys().contains("distances")) {
            setDistances(calculator, createDistanceMap(params, "distances"));
        }

        return calculator;
    }

    private void assertMapsCorrect(JsonParameterSet params, BellmanFordPathCalculator<Integer> calculator, Context.Builder<?> context) {
        context.add("actual predecessors", calculator.predecessors.toString());
        context.add("actual distances", calculator.distances.toString());

        assertMapEquals(createPredecessorMap(params, "expectedPredecessors"), calculator.predecessors, context, "predecessor");
        assertMapEquals(createDistanceMap(params, "expectedDistances"), calculator.distances, context, "distance");
    }

}
