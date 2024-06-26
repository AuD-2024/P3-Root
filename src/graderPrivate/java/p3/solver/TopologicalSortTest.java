package p3.solver;

import org.junit.jupiter.params.ParameterizedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import p3.P3_TestBase;
import p3.graph.Graph;

import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static p3.util.AssertionUtil.assertListEquals;
import static p3.util.ReflectionUtil.setDFS;

public class TopologicalSortTest extends P3_TestBase {

    @Override
    public String getTestedClassName() {
        return "TopologicalSort";
    }

    @Override
    public List<String> getOptionalParams() {
        return List.of("cyclic", "finishingTimes", "expected");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "topologicalSort/sortNonCyclic.json")
    public void testSortNonCyclic(JsonParameterSet params) throws ReflectiveOperationException {
        TopologicalSort<Integer> topologicalSort = createTopologicalSort(params);
        Context.Builder<?> context = createContext(params, "sort");

        List<Integer> actual = callObject(topologicalSort::sort, context, "sort");

        context.add("actual", actual);

        assertListEquals(params.get("expected"), actual, context, "returned");
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "topologicalSort/sortCyclic.json")
    public void testSortCyclic(JsonParameterSet params) throws ReflectiveOperationException {
        TopologicalSort<Integer> topologicalSort = createTopologicalSort(params);
        Context.Builder<?> context = createContext(params, "sort");

        try {
            topologicalSort.sort();
        } catch (CycleException ignored) {

        } catch (AssertionError e) {
            throw e;
        } catch (Throwable e) {
            Assertions2.call(() -> {
                throw e;
            }, context.build(), result -> "TopologicalSort.sort threw an unexpected exception of type %s".formatted(e.getClass().getSimpleName()));
        }

    }

    @SuppressWarnings("unchecked")
    private TopologicalSort<Integer> createTopologicalSort(JsonParameterSet params) throws ReflectiveOperationException {
        DFS<Integer> dfs = mock(DFS.class);

        when(dfs.isCyclic()).thenReturn(params.getBoolean("cyclic"));

        doAnswer(invocation -> {
            ObjIntConsumer<Integer> consumer = invocation.getArgument(0);

            params.<Map<String, Object>>get("finishingTimes").forEach((key, value) -> {
                consumer.accept(Integer.parseInt(key), (int) value);
            });

            return null;
        }).when(dfs).traverse(any());

        TopologicalSort<Integer> topologicalSort = new TopologicalSort<>(Graph.empty());
        setDFS(topologicalSort, dfs);

        return topologicalSort;
    }

}
