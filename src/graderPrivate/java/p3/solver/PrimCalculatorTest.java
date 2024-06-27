package p3.solver;

import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;
import p3.graph.Edge;
import p3.graph.Graph;
import p3.implementation.TestGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static p3.util.AssertionUtil.assertEquals;
import static p3.util.AssertionUtil.assertMapEquals;
import static p3.util.AssertionUtil.assertNotNull;
import static p3.util.AssertionUtil.assertSetEquals;
import static p3.util.AssertionUtil.fail;
import static p3.util.ReflectionUtil.setKeys;
import static p3.util.ReflectionUtil.setPredecessors;
import static p3.util.ReflectionUtil.setRemainingNodes;

@TestForSubmission
public class PrimCalculatorTest extends P3_TestBase {

    @Override
    public String getTestedClassName() {
        return "PrimMSTCalculator";
    }

    @Override
    public List<String> getOptionalParams() {
        return List.of("nodes", "edges", "root", "node", "remainingNodes", "predecessors", "keys",
            "expectedPredecessors", "expectedKeys", "expectedRemainingNodes", "expected", "expectedProcessNodeOrder",
            "expectedEdges", "mayReturnAny");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "prim/init.json")
    public void testInit(JsonParameterSet params) throws ReflectiveOperationException {
        PrimMSTCalculator<Integer> calculator = createCalculator(params);
        Context.Builder<?> context = createContext(params, "init");

        call(() -> calculator.init(params.getInt("root")), context, "init");

        assertMapsCorrect(params, calculator, context);
        assertSetEquals(new HashSet<>(params.get("nodes")), calculator.remainingNodes, context, "remainingNodes");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "prim/processNode.json")
    public void testProcessNode(JsonParameterSet params) throws ReflectiveOperationException {
        PrimMSTCalculator<Integer> calculator = createCalculator(params);
        Context.Builder<?> context = createContext(params, "processNode");

        call(() -> calculator.processNode(params.getInt("node")), context, "processNode");

        assertMapsCorrect(params, calculator, context);
        assertSetEquals(new HashSet<>(params.get("remainingNodes")), calculator.remainingNodes, context, "remainingNodes");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "prim/extractMin.json")
    public void testExtractMin(JsonParameterSet params) throws ReflectiveOperationException {
        PrimMSTCalculator<Integer> calculator = createCalculator(params);
        Context.Builder<?> context = createContext(params, "extractMin");

        Integer actual = callObject(calculator::extractMin, context, "extractMin");

        context.add("actual", actual);
        context.add("actual remainingNodes", calculator.remainingNodes);

        if (params.getBoolean("mayReturnAny")) {
            assertNotNull(actual, context, "extractMin should not return null");
            Set<Integer> expectedSet = new HashSet<>(params.get("remainingNodes"));
            expectedSet.remove(actual);
            assertSetEquals(expectedSet, calculator.remainingNodes, context, "remainingNodes");
        } else {
            assertEquals(params.getInt("expected"), actual, context, "extractMin did not return the expected node");
            assertSetEquals(new HashSet<>(params.get("expectedRemainingNodes")), calculator.remainingNodes, context, "remainingNodes");
        }

        assertMapsCorrect(params, calculator, context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "prim/calculateMST.json")
    public void testCalculateMST(JsonParameterSet params) throws ReflectiveOperationException {
        PrimMSTCalculator<Integer> calculator = createCalculator(params, true);
        Context.Builder<?> context = createContext(params, "calculateMST");

        List<Integer> nodes = params.get("nodes");
        List<Integer> remainingNodes = new ArrayList<>(params.get("expectedProcessNodeOrder"));
        Set<Edge<Integer>> edges = new HashSet<>();
        List<Integer> processNodeOrder = params.get("expectedProcessNodeOrder");

        ArgumentCaptor<Integer> initRootCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> processNodeCaptor = ArgumentCaptor.forClass(Integer.class);

        doNothing().when(calculator).init(initRootCaptor.capture());
        doNothing().when(calculator).processNode(processNodeCaptor.capture());
        doAnswer(invocation -> {
            if (remainingNodes.isEmpty()) {
                fail(context, "extractMin has been called but there are no remaining nodes");
            }

            Integer removed = remainingNodes.remove(0);
            calculator.remainingNodes.remove(removed);
            return removed;
        }).when(calculator).extractMin();
        doReturn(edges).when(calculator).calculateMSTEdges();

        Graph<Integer> actual = callObject(() -> calculator.calculateMST(params.getInt("root")), context, "calculateMST");

        InOrder inOrder = inOrder(calculator);

        checkVerify(() -> inOrder.verify(calculator).init(anyInt()), context, "init should be called exactly once");
        assertEquals(params.getInt("root"), initRootCaptor.getValue(), context, "The root node should be the argument of the init method");

        checkVerify(() -> inOrder.verify(calculator, times(nodes.size())).processNode(anyInt()), context, "processNode should be called for each node exactly once");
        for (int i = 0; i < nodes.size(); i++) {
            assertEquals(processNodeOrder.get(i), processNodeCaptor.getAllValues().get(i), context,
                "The %dth invocation of processNode should be called with the node %d".formatted(i + 1, processNodeOrder.get(i)));
        }

        checkVerify(() -> inOrder.verify(calculator).calculateMSTEdges(), context, "calculateMSTEdges should be called exactly once");

        assertSetEquals(new HashSet<>(params.get("nodes")), actual.getNodes(), context, "returned graph nodes");
        assertSetEquals(edges, actual.getEdges(), context, "returned graph edges");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "prim/calculateMSTEdges.json")
    public void testCalculateMSTEdges(JsonParameterSet params) throws ReflectiveOperationException {
        PrimMSTCalculator<Integer> calculator = createCalculator(params);
        Context.Builder<?> context = createContext(params, "calculateMSTEdges");

        Set<Edge<Integer>> actual = callObject(calculator::calculateMSTEdges, context, "calculateMSTEdges");

        assertSetEquals(getEdges(params, "expectedEdges"), actual, context, "returned");
    }

    private PrimMSTCalculator<Integer> createCalculator(JsonParameterSet params) throws ReflectiveOperationException {
        return createCalculator(params, false);
    }

    private PrimMSTCalculator<Integer> createCalculator(JsonParameterSet params, boolean spy) throws ReflectiveOperationException {
        List<Integer> nodes = params.get("nodes");
        Set<Edge<Integer>> edges = getEdges(params);

        Graph<Integer> graph = new TestGraph<>(nodes, edges);
        PrimMSTCalculator<Integer> calculator = spy ? spy(new PrimMSTCalculator<>(graph)) : new PrimMSTCalculator<>(graph);

        Set<Integer> remainingNodes = params.availableKeys().contains("remainingNodes") ? new HashSet<>(params.get("remainingNodes")) : new HashSet<>();

        setPredecessors(calculator, createPredecessorMap(params, "predecessors"));
        setKeys(calculator, createKeysMap(params, "keys"));
        setRemainingNodes(calculator, remainingNodes);

        return calculator;
    }

    private void assertMapsCorrect(JsonParameterSet params, PrimMSTCalculator<Integer> calculator, Context.Builder<?> context) {
        context.add("actual predecessors", calculator.predecessors);
        context.add("actual keys", calculator.keys);

        Map<Integer, Integer> expectedPredecessors = createPredecessorMap(params,
            params.availableKeys().contains("expectedPredecessors") ? "expectedPredecessors" : "predecessors");
        Map<Integer, Integer> expectedKeys = createKeysMap(params,
            params.availableKeys().contains("expectedKeys") ? "expectedKeys" : "keys");

        assertMapEquals(expectedPredecessors, calculator.predecessors, context, "predecessors");
        assertMapEquals(expectedKeys, calculator.keys, context, "keys");
    }

}
