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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static Set<Edge<Integer>> getEdges(JsonParameterSet params) {
        return getEdges(params, "edges");
    }

    public static Set<Edge<Integer>> getEdges(JsonParameterSet params, String key) {

        if (!params.availableKeys().contains(key)) {
            return Set.of();
        }

        List<Integer> nodes = params.get("nodes");
        List<List<Integer>> edges = params.get(key);

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

    public static <T> Map<Integer, T> nodeListToMap(JsonParameterSet params, String valuesKey, Function<Object, T> valueMapper) {
        List<Object> list = params.get(valuesKey);
        return createNodeMap(params, i -> valueMapper.apply(list.get(i)));
    }

    public static <T> Map<Integer, T> createNodeMap(JsonParameterSet params, Function<Integer, T> indexToValue) {
        List<Integer> nodes = params.get("nodes");

        Map<Integer, T> map = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            map.put(nodes.get(i), indexToValue.apply(i));
        }

        return map;
    }

    public static <T> Map<Integer, T> mapToNodeMap(JsonParameterSet params, String mapKey, Function<Object, T> valueMapper) {
        return params.<Map<String, Object>>get(mapKey).entrySet().stream()
            .collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), e -> valueMapper.apply(e.getValue())));
    }

    public static Map<Integer, Integer> createPredecessorMap(JsonParameterSet params, String valuesKey) {
        return nodeListToMap(params, valuesKey, value -> value == null ? null : (Integer) value);
    }

    public static Map<Integer, Integer> createDistanceMap(JsonParameterSet params, String valuesKey) {
        return nodeListToMap(params, valuesKey, value -> value == null ? Integer.MAX_VALUE : (Integer) value);
    }

    public static Map<Integer, Integer> createKeysMap(JsonParameterSet params, String valuesKey) {
        return nodeListToMap(params, valuesKey, value -> {
            if (value == null) return Integer.MAX_VALUE;
            if ((Integer) value == -1) return Integer.MIN_VALUE;
            return (Integer) value;
        });
    }

}
