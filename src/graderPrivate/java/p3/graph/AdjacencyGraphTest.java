package p3.graph;

import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;
import p3.implementation.TestAdjacencyRepresentation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static p3.util.AssertionUtil.assertEquals;
import static p3.util.AssertionUtil.assertMapEquals;
import static p3.util.AssertionUtil.assertSame;
import static p3.util.AssertionUtil.assertSetEquals;
import static p3.util.AssertionUtil.assertTrue;
import static p3.util.ReflectionUtil.getIndexToNode;
import static p3.util.ReflectionUtil.getNodeToIndex;
import static p3.util.ReflectionUtil.getRepresentation;
import static p3.util.ReflectionUtil.getWeights;
import static p3.util.ReflectionUtil.setIndexToNode;
import static p3.util.ReflectionUtil.setNodeToIndex;
import static p3.util.ReflectionUtil.setWeights;

@TestForSubmission
public class AdjacencyGraphTest extends P3_TestBase {

    @Override
    public String getTestedClassName() {
        return "AdjacencyGraph";
    }

    @Override
    public List<String> getOptionalParams() {
        return List.of("nodes", "edges", "nodeToAdd");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/addNode.json")
    public void testAddNode(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "addNode");
        AdjacencyGraph<Integer> graph = createGraph(params, context, true);

        TestAdjacencyRepresentation representation = (TestAdjacencyRepresentation) getRepresentation(graph);
        representation.disableAddEdge();

        List<Integer> nodes = params.get("nodes");
        int nodeToAdd = params.get("nodeToAdd");

        boolean alreadyContains = nodes.contains(nodeToAdd);

        Map<Integer, Integer> expectedNodeToIndex = createNodeToIndexMap(params);
        if (!alreadyContains) expectedNodeToIndex.put(nodeToAdd, nodes.size());

        Map<Integer, Integer> expectedIndexToNode = createIndexToNodeMap(params);
        if (!alreadyContains) expectedIndexToNode.put(nodes.size(), nodeToAdd);

        context.add("expected nodeToIndex", expectedNodeToIndex);
        context.add("expected indexToNode", expectedIndexToNode);

        call(() -> graph.addNode(nodeToAdd), context, "addNode");

        context.add("actual nodeToIndex", getNodeToIndex(graph).toString());
        context.add("actual indexToNode", getIndexToNode(graph).toString());

        assertMapEquals(expectedNodeToIndex, getNodeToIndex(graph), context, "nodeToIndex");
        assertMapEquals(expectedIndexToNode, getIndexToNode(graph), context, "indexToNode");

        if (!alreadyContains) {
            checkVerify(() -> verify(representation).grow(), context, "representation.grow() should be called exactly once");
        } else {
            checkVerify(() -> verify(representation, never()).grow(), context, "representation.grow() should not be called");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/addEdge.json")
    public void testAddEdge(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "addEdge");
        AdjacencyGraph<Integer> graph = createGraph(params, context, true);

        ((TestAdjacencyRepresentation) getRepresentation(graph)).disableGrow();

        List<Integer> fromList = params.get("from");
        List<Integer> toList = params.get("to");
        List<Integer> weightList = params.get("weight");
        int edgesToAddCount = params.getInt("edgesToAddCount");

        Map<Integer, Integer> nodeToIndex = createNodeToIndexMap(params);
        Map<Integer, Integer> indexToNode = createIndexToNodeMap(params);

        Set<Edge<Integer>> expectedWeights = new HashSet<>();

        for (int i = 0; i < edgesToAddCount; i++) {
            int from = fromList.get(i);
            int to = toList.get(i);
            int weight = weightList.get(i);

            Edge<Integer> edgeToAdd = Edge.of(from, to, weight);
            expectedWeights.add(edgeToAdd);

            context.add("edgeToAdd", edgeToAdd);
            context.add("previous weights", getWeights(graph).toString());
            context.add("expected weights", expectedWeights.toString());

            ArgumentCaptor<Integer> fromCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Integer> toCaptor = ArgumentCaptor.forClass(Integer.class);
            doCallRealMethod().when(getRepresentation(graph)).addEdge(fromCaptor.capture(), toCaptor.capture());

            call(() -> graph.addEdge(edgeToAdd), context, "addEdge should not throw an exception");

            Map<Integer, Map<Integer, Integer>> actualWeights = getWeights(graph);
            context.add("actual weights", actualWeights.toString());

            assertMapEquals(nodeToIndex, getNodeToIndex(graph), context, "nodeToIndex");
            assertMapEquals(indexToNode, getIndexToNode(graph), context, "indexToNode");

            assertWeightsCorrect(expectedWeights, actualWeights, context);

            assertEquals(1, toCaptor.getAllValues().size(), context, "representation.addEdge should be called exactly once");
            assertEquals(nodeToIndex.get(from), fromCaptor.getValue(), context, "representation.addEdge has not been called with the correct from value");
            assertEquals(nodeToIndex.get(to), toCaptor.getValue(), context, "representation.addEdge has not been called with the correct to value");

            fromCaptor.getAllValues().clear();
            toCaptor.getAllValues().clear();

            // Test adding the same edge with a different weight

            int updatedWeight = weight * 10;
            Edge<Integer> updatedEdge = Edge.of(from, to, updatedWeight);
            expectedWeights.remove(edgeToAdd);
            expectedWeights.add(updatedEdge);

            context.add("edgeToAdd", updatedEdge);
            context.add("previous weights", actualWeights.toString());
            context.add("expected weights", expectedWeights.toString());

            call(() -> graph.addEdge(updatedEdge), context, "addEdge should not throw an exception when called a second time with the same edge but different weight");

            actualWeights = getWeights(graph);
            context.add("actual weights", actualWeights.toString());

            assertWeightsCorrect(expectedWeights, actualWeights, context);
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/getEdge.json")
    public void testGetEdge(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "getEdge");
        AdjacencyGraph<Integer> graph = createGraph(params, context);

        ((TestAdjacencyRepresentation) getRepresentation(graph)).disableGrow();

        Set<Edge<Integer>> edges = getEdges(params);
        Map<Integer, Integer> nodeToIndex = getNodeToIndex(graph);

        for (int from : nodeToIndex.keySet()) {
            for (int to : nodeToIndex.keySet()) {
                Edge<Integer> expected = edges.stream().filter(e -> e.from() == from && e.to() == to).findFirst().orElse(null);

                context.add("from", from);
                context.add("to", to);
                context.add("expected", expected);

                Edge<Integer> actual = callObject(() -> graph.getEdge(from, to), context, "getEdge should not throw an exception");

                context.add("actual", actual);

                assertEquals(expected, actual, context, "The method did not return the correct value");

                if (expected != null) {
                    assertEquals(expected.weight(), actual.weight(), context, "The returned edge does not have the correct weight");
                }
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/getOutgoingEdges.json")
    public void testGetOutgoingEdges(JsonParameterSet params) throws ReflectiveOperationException {
        Context.Builder<?> context = createContext(params, "getOutgoingEdges");
        AdjacencyGraph<Integer> graph = createGraph(params, context);

        ((TestAdjacencyRepresentation) getRepresentation(graph)).disableGrow();

        Set<Edge<Integer>> edges = getEdges(params);
        Map<Integer, Integer> nodeToIndex = getNodeToIndex(graph);

        for (int node : nodeToIndex.keySet()) {
            Set<Edge<Integer>> expected = edges.stream().filter(e -> e.from() == node).collect(Collectors.toSet());

            context.add("node", node);
            context.add("expected", expected);

            Set<Edge<Integer>> actual = callObject(() -> graph.getOutgoingEdges(node), context, "getOutgoingEdges should not throw an exception");

            context.add("actual", actual);

            assertSetEquals(expected, actual, context, "returned");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/constructor.json")
    public void testConstructor(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = getEdges(params);

        Context.Builder<?> context = createContext(params, "constructor");

        TestAdjacencyRepresentation representation = spy(new TestAdjacencyRepresentation(nodes.size()));
        AdjacencyGraph<Integer> graph = callObject(() -> new AdjacencyGraph<>(new HashSet<>(nodes), edges, size -> {
            assertEquals(nodes.size(), size, context, "The representation should be created with the correct size");
            return representation;
        }), context, "constructor");

        Map<Integer, Integer> actualNodeToIndex = getNodeToIndex(graph);
        Map<Integer, Integer> actualIndexToNode = getIndexToNode(graph);
        Map<Integer, Map<Integer, Integer>> actualWeights = getWeights(graph);

        context.add("actual nodeToIndex", actualNodeToIndex);
        context.add("actual indexToNode", actualIndexToNode);
        context.add("actual weights", actualWeights);

        assertSame(representation, getRepresentation(graph), context, "The representation should be set to the one returned by the factory");

        assertEquals(nodes.size(), actualNodeToIndex.size(), context, "nodeToIndex does not have the correct size");
        assertEquals(nodes.size(), actualIndexToNode.size(), context, "indexToNode does not have the correct size");

        for (int node : nodes) {
            assertTrue(actualNodeToIndex.containsKey(node), context, "nodeToIndex does not contain key: " + node);
            int index = actualNodeToIndex.get(node);
            assertTrue(index >= 0 && index < nodes.size(), context, "nodeToIndex does not contain a valid index for node: " + node + ". Index: " + index);
            assertTrue(actualIndexToNode.containsKey(index), context, "indexToNode does not contain key nodeToIndex.get(" + node + ")");
            assertEquals(node, actualIndexToNode.get(index), context, "indexToNode does not contain the correct node for key: " + index);
        }

        assertWeightsCorrect(edges, actualWeights, context);
    }

    private AdjacencyGraph<Integer> createGraph(JsonParameterSet params, Context.Builder<?> context) throws ReflectiveOperationException {
        return createGraph(params, context, false);
    }

    private AdjacencyGraph<Integer> createGraph(JsonParameterSet params,
                                                Context.Builder<?> context,
                                                boolean spyRepresentation) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = getEdges(params);

        Map<Integer, Integer> nodeToIndex = createNodeToIndexMap(params);
        Map<Integer, Integer> indexToNode = createIndexToNodeMap(params);

        TestAdjacencyRepresentation representation = spyRepresentation ? spy(new TestAdjacencyRepresentation(nodes.size())) : new TestAdjacencyRepresentation(nodes.size());

        AdjacencyGraph<Integer> graph = callObject(() -> new AdjacencyGraph<>(new HashSet<>(nodes), new HashSet<>(), size -> representation),
            context, "constructor");

        setNodeToIndex(graph, new HashMap<>(nodeToIndex));
        setIndexToNode(graph, new HashMap<>(indexToNode));

        for (Edge<Integer> edge : edges) {
            representation.addEdge(nodeToIndex.get(edge.from()), nodeToIndex.get(edge.to()));
        }

        if (!edges.isEmpty()) {
            overrideWeights(params, graph);
        }

        context.add("nodeToIndex", nodeToIndex);
        context.add("indexToNode", indexToNode);

        return graph;
    }

    private void assertWeightsCorrect(Set<Edge<Integer>> expected, Map<Integer, Map<Integer, Integer>> actual, Context.Builder<?> context) {
        for (Edge<Integer> edge : expected) {
            assertTrue(actual.containsKey(edge.from()), context, "weights does not contain key: " + edge.from());
            assertTrue(actual.get(edge.from()).containsKey(edge.to()), context, "weights.get(from) does not contain key: " + edge.to());
            assertEquals(edge.weight(), actual.get(edge.from()).get(edge.to()), context, "weights.get(from).get(to) does not contain the expected value");
        }
    }

    private Map<Integer, Integer> createNodeToIndexMap(JsonParameterSet params) {
        List<Integer> nodes = params.get("nodes");
        Map<Integer, Integer> nodeMap = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeMap.put(nodes.get(i), i);
        }
        return nodeMap;
    }

    private Map<Integer, Integer> createIndexToNodeMap(JsonParameterSet params) {
        List<Integer> nodes = params.get("nodes");
        Map<Integer, Integer> nodeMap = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeMap.put(i, nodes.get(i));
        }
        return nodeMap;
    }

    private void overrideWeights(JsonParameterSet params, AdjacencyGraph<Integer> graph) throws ReflectiveOperationException {
        Map<Integer, Map<Integer, Integer>> weights = new HashMap<>();

        for (Integer node : params.<List<Integer>>get("nodes")) {
            weights.put(node, new HashMap<>());
        }

        for (Edge<Integer> edge : getEdges(params)) {
            weights.get(edge.from()).put(edge.to(), edge.weight());
        }

        setWeights(graph, weights);
    }

}
