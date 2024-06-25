package p3;

import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.callable.Callable;
import org.tudalgo.algoutils.tutor.general.callable.ObjectCallable;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import p3.graph.Edge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;
import static p3.util.AssertionUtil.fail;

@SkipAfterFirstFailedTest
public abstract class P3_TestBase {

    public abstract String getTestedClassName();

    public abstract List<String> getOptionalParams();

    public Context.Builder<?> createContext(JsonParameterSet params, String method) {
        return createContext(params, method, Map.of());
    }

    public Context.Builder<?> createContext(JsonParameterSet params, String method, Map<String, Object> additionalParams) {
        Context.Builder<?> context = contextBuilder()
            .subject("%s.%s".formatted(getTestedClassName(), method));

        for (Map.Entry<String, Object> entry : additionalParams.entrySet()) {
            context.add(entry.getKey(), entry.getValue());
        }

        for (String param : getOptionalParams()) {
            if (params.availableKeys().contains(param)) {
                context.add(param, params.get(param));
            }
        }

        return context;
    }

    public void call(Callable callable, Context.Builder<?> context, String name) {
        Assertions2.call(callable, context.build(), result -> "%s.%s should not throw an exception".formatted(getTestedClassName(), name));
    }

    public <T> T callObject(ObjectCallable<T> callable, Context.Builder<?> context, String name) {
        return Assertions2.callObject(callable, context.build(), result -> "%s.%s should not throw an exception".formatted(getTestedClassName(), name));
    }

    public static void checkVerify(Callable verifier, Context.Builder<?> context, String msg) {
        try {
            verifier.call();
        } catch (AssertionError e) {
            fail(context, msg + ". Original error message:\n" + e.getMessage());
        } catch (Throwable e) {
            fail(context, "Unexpected Exception:\n" + e.getMessage());
        }
    }

    public static Set<Edge<Integer>> listToEdgeSet(List<List<Integer>> edges, List<Integer> nodes) {
        Set<Edge<Integer>> edgeSet = new HashSet<>();

        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.get(i).size(); j++) {
                int weight = edges.get(i).get(j);
                if (weight == 0) continue;
                edgeSet.add(Edge.of(nodes.get(i), nodes.get(j), weight));
            }
        }

        return edgeSet;
    }

    public static Map<Integer, Integer> createPredecessorMap(List<Integer> predecessorList, List<Integer> nodes) {
        return nodeListToMap(predecessorList, nodes, Map.of(), null);
    }

    public static Map<Integer, Integer> createDistanceMap(List<Integer> distanceList, List<Integer> nodes) {
        return nodeListToMap(distanceList, nodes, Map.of(), Integer.MAX_VALUE);
    }

    public static Map<Integer, Integer> createKeysMap(List<Integer> keysList, List<Integer> nodes) {
        return nodeListToMap(keysList, nodes, Map.of(-1, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    private static Map<Integer, Integer> nodeListToMap(List<Integer> list,
                                                      List<Integer> nodes,
                                                      Map<Integer, Integer> replacements,
                                                      Integer nullReplacement) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Integer value = list.get(i);
            if (value == null)
                map.put(nodes.get(i), nullReplacement);
            else
                map.put(nodes.get(i), replacements.getOrDefault(value, value));
        }

        return map;
    }

}
