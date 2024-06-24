package p3.solver;

import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;
import p3.graph.Graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ObjIntConsumer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

public class DFSTest extends P3_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "dfs/traverse.json")
    public void testTraverse(JsonParameterSet params) {
        List<Integer> nodes = params.get("nodes");
        List<Integer> visited = params.get("visited");

        DFS<Integer> dfs = spy(new DFS<>(Graph.of(new HashSet<>(nodes), Set.of())));

        dfs.visited.addAll(visited);

        ArgumentCaptor<ObjIntConsumer<Integer>> visitedConsumerCaptor = ArgumentCaptor.forClass(ObjIntConsumer.class);
        ArgumentCaptor<Integer> visitedNodeCaptor = ArgumentCaptor.forClass(Integer.class);

        doNothing().when(dfs).init();
        doNothing().when(dfs).visit(visitedConsumerCaptor.capture(), visitedNodeCaptor.capture());

        Context context = contextBuilder()
            .subject("DFS.traverse")
            .add("nodes", nodes)
            .add("visited", visited)
            .build();

        ObjIntConsumer<Integer> emptyConsumer = (node, time) -> {};

        call(() -> dfs.traverse(emptyConsumer), context, result -> "DFS.traverse should not throw an exception");

        InOrder inorder = inOrder(dfs);

        checkVerify(() -> inorder.verify(dfs).init(), context, "init() should be called exactly once");

        int expectedVisitedCount = nodes.size() - visited.size();
        checkVerify(() -> inorder.verify(dfs, times(expectedVisitedCount)).visit(any(), any()), context,
            "visit() should be called exactly %d times after init()".formatted(expectedVisitedCount));

        for (int node : nodes) {
            if (!visited.contains(node)) {
                assertTrue(visitedNodeCaptor.getAllValues().contains(node), context,
                    result -> "visit should be invoked with the node %d".formatted(node));
            }
        }

        for (int i = 0; i < expectedVisitedCount; i++) {
            assertSame(emptyConsumer, visitedConsumerCaptor.getAllValues().get(i), context,
                result -> "The consumer passed to visit() should be the same as the one passed to traverse()");
        }

        checkVisitedCorrect(dfs, visited, context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "dfs/init.json")
    public void testInit(JsonParameterSet params) {
        List<Integer> nodes = params.get("nodes");
        List<Integer> visited = params.get("visited");
        int time = params.getInt("time");
        boolean cyclic = params.getBoolean("cyclic");

        DFS<Integer> dfs = new DFS<>(Graph.of(new HashSet<>(nodes), Set.of()));

        dfs.visited.addAll(visited);
        dfs.time = time;
        dfs.cyclic = cyclic;

        Context.Builder<?> context = contextBuilder()
            .subject("DFS.init")
            .add("nodes", nodes)
            .add("previous visited", visited)
            .add("previous time", time)
            .add("previous cyclic", cyclic);

        call(dfs::init, context.build(), result -> "DFS.init should not throw an exception");

        context.add("actual visited", dfs.visited)
            .add("actual time", dfs.time)
            .add("actual cyclic", dfs.cyclic);

        assertTrue(dfs.visited.isEmpty(), context.build(), result -> "The visited set should be empty after calling init()");
        assertEquals(0, dfs.time, context.build(), result -> "The time should be reset to 0 after calling init()");
        assertFalse(dfs.cyclic, context.build(), result -> "The cyclic flag should be reset to false after calling init()");
    }

    private void checkVisitedCorrect(DFS<Integer> dfs, List<Integer> visited, Context context) {

        assertEquals(visited.size(), dfs.visited.size(), context,
            result -> "The size of the visited set is not correct");

        for (int node : visited) {
            assertTrue(dfs.visited.contains(node), context,
                result -> "The visited set should contain the node %d".formatted(node));
        }
    }

}
