package p3;

import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.graph.AdjacencyGraph;
import p3.graph.Edge;
import p3.implementation.TestAdjacencyRepresentation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertSame;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.call;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.callObject;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

public class AdjacencyGraphTest extends P3_TestBase {

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/addNode.json")
    public void testAddNode(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        int nodeToAdd = params.get("nodeToAdd");

        boolean alreadyContains = nodes.contains(nodeToAdd);

        Map<Integer, Integer> nodeToIndex = createNodeToIndexMap(nodes);
        Map<Integer, Integer> indexToNode = createIndexToNodeMap(nodes);

        Map<Integer, Integer> expectedNodeToIndex = new HashMap<>(nodeToIndex);
        if (!alreadyContains) expectedNodeToIndex.put(nodeToAdd, nodes.size());

        Map<Integer, Integer> expectedIndexToNode = new HashMap<>(indexToNode);
        if (!alreadyContains) expectedIndexToNode.put(nodes.size(), nodeToAdd);

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyGraph.addNode")
            .add("nodes", nodes)
            .add("nodeToAdd", nodeToAdd)
            .add("previous nodeToIndex", nodeToIndex.toString())
            .add("previous indexToNode", indexToNode.toString())
            .add("expected nodeToIndex", expectedNodeToIndex.toString())
            .add("expected indexToNode", expectedIndexToNode.toString());

        TestAdjacencyRepresentation representation = spy(new TestAdjacencyRepresentation(nodes.size()));
        representation.disableAddEdge();

        AdjacencyGraph<Integer> graph = new AdjacencyGraph<>(new HashSet<>(nodes), Set.of(), size -> representation);

        setNodeToIndex(graph, nodeToIndex);
        setIndexToNode(graph, indexToNode);

        call(() -> graph.addNode(nodeToAdd), context.build(), result -> "addNode should not throw an exception");

        context.add("actual nodeToIndex", getNodeToIndex(graph).toString())
            .add("actual indexToNode", getIndexToNode(graph).toString());

        assertMapsEqual(expectedNodeToIndex, getNodeToIndex(graph), context.build(), "nodeToIndex");
        assertMapsEqual(expectedIndexToNode, getIndexToNode(graph), context.build(), "indexToNode");

        if (!alreadyContains) {
            checkVerify(() -> verify(representation).grow(), context.build(), "representation.grow() should be called exactly once");
        } else {
            checkVerify(() -> verify(representation, never()).grow(), context.build(), "representation.grow() should not be called");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/addEdge.json")
    public void testAddEdge(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        List<Integer> fromList = params.get("from");
        List<Integer> toList = params.get("to");
        List<Integer> weightList = params.get("weight");
        int edgesToAddCount = params.getInt("edgesToAddCount");


        Map<Integer, Integer> nodeToIndex = createNodeToIndexMap(nodes);
        Map<Integer, Integer> indexToNode = createIndexToNodeMap(nodes);

        TestAdjacencyRepresentation representation = spy(new TestAdjacencyRepresentation(nodes.size()));
        representation.disableGrow();

        AdjacencyGraph<Integer> graph = callObject(() -> new AdjacencyGraph<>(new HashSet<>(nodes), Set.of(), size -> representation),
            contextBuilder().add("nodes", nodes).add("edges", Set.of()).build(), result -> "The constructor should not throw an exception");

        setNodeToIndex(graph, new HashMap<>(nodeToIndex));
        setIndexToNode(graph, new HashMap<>(indexToNode));

        Set<Edge<Integer>> expectedWeights = new HashSet<>();

        for (int i = 0; i < edgesToAddCount; i++) {
            int from = fromList.get(i);
            int to = toList.get(i);
            int weight = weightList.get(i);

            Edge<Integer> edgeToAdd = Edge.of(from, to, weight);
            expectedWeights.add(edgeToAdd);

            Context.Builder<?> context = contextBuilder()
                .subject("AdjacencyGraph.addNode")
                .add("nodes", nodes)
                .add("edgeToAdd", edgeToAdd)
                .add("nodeToIndex", nodeToIndex.toString())
                .add("indexToNode", indexToNode.toString())
                .add("previous weights", getWeights(graph).toString())
                .add("expected weights", expectedWeights.toString());

            ArgumentCaptor<Integer> fromCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Integer> toCaptor = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(representation).addEdge(fromCaptor.capture(), toCaptor.capture());

            call(() -> graph.addEdge(edgeToAdd), context.build(), result -> "addEdge should not throw an exception");

            Map<Integer, Map<Integer, Integer>> actualWeights = getWeights(graph);
            context.add("actual weights", actualWeights.toString());

            assertMapsEqual(nodeToIndex, getNodeToIndex(graph), context.build(), "nodeToIndex");
            assertMapsEqual(indexToNode, getIndexToNode(graph), context.build(), "indexToNode");

            assertWeightsCorrect(expectedWeights, actualWeights, context.build());

            assertEquals(1, toCaptor.getAllValues().size(), context.build(), result -> "representation.addEdge should be called exactly once");
            assertEquals(from, fromCaptor.getValue(), context.build(), result -> "representation.addEdge has not been called with the correct from value");
            assertEquals(to, toCaptor.getValue(), context.build(), result -> "representation.addEdge has not been called with the correct to value");

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

            call(() -> graph.addEdge(updatedEdge), context.build(), result -> "addEdge should not throw an exception when called a second time with the same edge but different weight");

            actualWeights = getWeights(graph);
            context.add("actual weights", actualWeights.toString());

            assertWeightsCorrect(expectedWeights, actualWeights, context.build());
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/getEdge.json")
    public void testGetEdge(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));

        Map<Integer, Integer> nodeToIndex = createNodeToIndexMap(nodes);
        Map<Integer, Integer> indexToNode = createIndexToNodeMap(nodes);

        TestAdjacencyRepresentation representation = spy(new TestAdjacencyRepresentation(nodes.size()));
        representation.disableGrow();

        for (Edge<Integer> edge : edges) {
            representation.addEdge(edge.from(), edge.to());
        }

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyGraph.getEdge")
            .add("nodes", nodes)
            .add("edges", edges);

        AdjacencyGraph<Integer> graph = callObject(() -> new AdjacencyGraph<>(new HashSet<>(nodes), new HashSet<>(edges), size -> representation),
            context.build(), result -> "The constructor should not throw an exception");

        setNodeToIndex(graph, new HashMap<>(nodeToIndex));
        setIndexToNode(graph, new HashMap<>(indexToNode));


        context.add("nodeToIndex", nodeToIndex)
            .add("indexToNode", indexToNode);


        for (int from : nodeToIndex.keySet()) {
            for (int to : nodeToIndex.keySet()) {
                Edge<Integer> expected = edges.stream().filter(e -> e.from() == from && e.to() == to).findFirst().orElse(null);

                context.add("from", from)
                    .add("to", to)
                    .add("expected", expected);

                Edge<Integer> actual = callObject(() -> graph.getEdge(from, to), context.build(), result -> "getEdge should not throw an exception");

                context.add("actual", actual);

                assertEquals(expected, actual, context.build(), result -> "The method did not return the correct value");

                if (expected != null) {
                    assertEquals(expected.weight(), actual.weight(), context.build(), result -> "The returned edge does not have the correct weight");
                }
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/getOutgoingEdges.json")
    public void getOutgoingEdgesTest(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));

        Map<Integer, Integer> nodeToIndex = createNodeToIndexMap(nodes);
        Map<Integer, Integer> indexToNode = createIndexToNodeMap(nodes);

        TestAdjacencyRepresentation representation = spy(new TestAdjacencyRepresentation(nodes.size()));
        representation.disableGrow();

        for (Edge<Integer> edge : edges) {
            representation.addEdge(edge.from(), edge.to());
        }

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyGraph.getOutgoingEdges")
            .add("nodes", nodes)
            .add("edges", edges);

        AdjacencyGraph<Integer> graph = callObject(() -> new AdjacencyGraph<>(new HashSet<>(nodes), new HashSet<>(edges), size -> representation)
            , context.build(), result -> "The constructor should not throw an exception");
        ;

        setNodeToIndex(graph, new HashMap<>(nodeToIndex));
        setIndexToNode(graph, new HashMap<>(indexToNode));

        context.add("nodeToIndex", nodeToIndex)
            .add("indexToNode", indexToNode);

        for (int node : nodeToIndex.keySet()) {
            Set<Edge<Integer>> expected = edges.stream().filter(e -> e.from() == node).collect(Collectors.toSet());

            context.add("node", node)
                .add("expected", expected);

            Set<Edge<Integer>> actual = callObject(() -> graph.getOutgoingEdges(node), context.build(), result -> "getOutgoingEdges should not throw an exception");

            context.add("actual", actual);

            assertNotNull(actual, context.build(), result -> "The method should not return null");
            assertEquals(expected.size(), actual.size(), context.build(), result -> "The returned set does not have the correct size");

            for (Edge<Integer> edge : expected) {
                assertTrue(actual.contains(edge), context.build(), result -> "The returned set does not contain the expected edge: " + edge);
                assertEquals(edge.weight(), actual.stream().filter(e -> e.equals(edge)).findFirst().get().weight(), context.build(),
                    result -> "The returned set contains an edge with the correct from and to values but it has a wrong weight");
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "adjacencygraph/constructor.json")
    public void testConstructor(JsonParameterSet params) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = listToEdgeSet(params.get("edges"));

        Context.Builder<?> context = contextBuilder()
            .subject("AdjacencyGraph.constructor")
            .add("nodes", nodes)
            .add("edges", edges);

        TestAdjacencyRepresentation representation = mock(TestAdjacencyRepresentation.class);

        AdjacencyGraph<Integer> graph = callObject(() -> new AdjacencyGraph<>(new HashSet<>(nodes), edges, size -> {
            assertEquals(nodes.size(), size, context.build(), result -> "The representation should be created with the correct size");
            return representation;
        }), context.build(), result -> "The constructor should not throw an exception");

        Map<Integer, Integer> actualNodeToIndex = getNodeToIndex(graph);
        Map<Integer, Integer> actualIndexToNode = getIndexToNode(graph);
        Map<Integer, Map<Integer, Integer>> actualWeights = getWeights(graph);

        context.add("actual nodeToIndex", actualNodeToIndex)
            .add("actual indexToNode", actualIndexToNode)
            .add("actual weights", actualWeights);

        assertSame(representation, getRepresentation(graph), context.build(), result -> "The representation should be set to the one returned by the factory");

        assertEquals(nodes.size(), actualNodeToIndex.size(), context.build(), result -> "nodeToIndex does not have the correct size");
        assertEquals(nodes.size(), actualIndexToNode.size(), context.build(), result -> "indexToNode does not have the correct size");

        for (int node : nodes) {
            assertTrue(actualNodeToIndex.containsKey(node), context.build(), result -> "nodeToIndex does not contain key: " + node);
            int index = actualNodeToIndex.get(node);
            assertTrue(index >= 0 && index < nodes.size(), context.build(),
                result -> "nodeToIndex does not contain a valid index for node: " + node + ". Index: " + index);
            assertTrue(actualIndexToNode.containsKey(index), context.build(), result -> "indexToNode does not contain key nodeToIndex.get(" + node + ")");
            assertEquals(node, actualIndexToNode.get(index), context.build(), result -> "indexToNode does not contain the correct node for key: " + index);
        }

        assertWeightsCorrect(edges, actualWeights, context.build());
    }


    private void assertWeightsCorrect(Set<Edge<Integer>> expected, Map<Integer, Map<Integer, Integer>> actual, Context context) {
        for (Edge<Integer> edge : expected) {
            assertTrue(actual.containsKey(edge.from()), context,
                result -> "weights does not contain key: " + edge.from());
            assertTrue(actual.get(edge.from()).containsKey(edge.to()), context,
                result -> "weights.get(from) does not contain key: " + edge.to());
            assertEquals(edge.weight(), actual.get(edge.from()).get(edge.to()), context,
                result -> "weights.get(from).get(to) does not contain the expected value");
        }
    }

    private void assertMapsEqual(Map<Integer, Integer> expected, Map<Integer, Integer> actual, Context context, String mapName) {
        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertTrue(actual.containsKey(entry.getKey()), context,
                result -> mapName + " does not contain key with value: " + entry.getKey());
            assertEquals(entry.getValue(), actual.get(entry.getKey()), context,
                result -> mapName + " does not contain the expected value for key: " + entry.getKey());
        }
    }

    private Map<Integer, Integer> createNodeToIndexMap(List<Integer> nodes) {
        Map<Integer, Integer> nodeMap = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeMap.put(nodes.get(i), i);
        }
        return nodeMap;
    }

    private Map<Integer, Integer> createIndexToNodeMap(List<Integer> nodes) {
        Map<Integer, Integer> nodeMap = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeMap.put(i, nodes.get(i));
        }
        return nodeMap;
    }

}