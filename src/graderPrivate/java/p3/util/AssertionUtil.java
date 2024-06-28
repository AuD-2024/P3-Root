package p3.util;

import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.graph.Edge;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssertionUtil {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K, V> void assertMapEquals(Map<K, V> expected, Map<K, V> actual, Context.Builder<?> context, String mapName) {
        assertNotNull(actual, context, "The %s map should not be null".formatted(mapName));
        assertEquals(expected.size(), actual.size(), context, "The size of the %s map is not correct".formatted(mapName));

        for (Map.Entry<K, V> entry : expected.entrySet()) {
            assertTrue(actual.containsKey(entry.getKey()), context, "The %s map should contain key %s".formatted(mapName, entry.getKey()));
            assertEquals(entry.getValue(), actual.get(entry.getKey()), context, "The %s map contains the wrong value for key %s".formatted(mapName, entry.getKey()));

            if (entry.getValue() instanceof Edge expectedEdge) {
                assertEquals(expectedEdge.weight(), ((Edge<Integer>) entry.getValue()).weight(), context,
                        "The %s map contains the edge %s but it has the wrong weight".formatted(mapName, expectedEdge));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static <E> void assertSetEquals(Set<E> expected, Set<E> actual, Context.Builder<?> context, String setName) {
        assertNotNull(actual, context, "The %s set should not be null".formatted(setName));
        assertEquals(expected.size(), actual.size(), context, "The size of the %s set is not correct".formatted(setName));

        for (E element : expected) {
            assertTrue(actual.contains(element), context, "The %s set does not contain the element %s".formatted(setName, element));

            if (element instanceof Edge expectedEdge) {
                assertEquals(expectedEdge.weight(), actual.stream().map(Edge.class::cast).filter(e -> e.equals(expectedEdge)).findFirst().get().weight(),
                        context, "The %s set contains the edge %s but it has the wrong weight".formatted(setName, expectedEdge));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static <E> void assertListEquals(List<E> expected, List<E> actual, Context.Builder<?> context, String listName) {
        assertNotNull(actual, context, "The %s list should not be null".formatted(listName));
        assertEquals(expected.size(), actual.size(), context, "The size of the %s list is not correct".formatted(listName));

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i), context, "The %s list contains the wrong value at index %d".formatted(listName, i));

            if (expected.get(i) instanceof Edge expectedEdge) {
                assertEquals(expectedEdge.weight(), ((Edge) actual.get(i)).weight(), context,
                        "The %s list contains the edge %s but it has the wrong weight".formatted(listName, expectedEdge));
            }
        }
    }

    public static void assertEquals(Object expected, Object actual, Context.Builder<?> context, String message) {
        Assertions2.assertEquals(expected, actual, context.build(), result -> message);
    }

    public static void assertTrue(boolean condition, Context.Builder<?> context, String message) {
        Assertions2.assertTrue(condition, context.build(), result -> message);
    }

    public static void assertFalse(boolean condition, Context.Builder<?> context, String message) {
        Assertions2.assertFalse(condition, context.build(), result -> message);
    }

    public static void assertNull(Object object, Context.Builder<?> context, String message) {
        Assertions2.assertNull(object, context.build(), result -> message);
    }

    public static void assertNotNull(Object object, Context.Builder<?> context, String message) {
        Assertions2.assertNotNull(object, context.build(), result -> message);
    }

    public static void assertSame(Object expected, Object actual, Context.Builder<?> context, String message) {
        Assertions2.assertSame(expected, actual, context.build(), result -> message);
    }

    public static void fail(Context.Builder<?> context, String message) {
        Assertions2.fail(context.build(), result -> message);
    }

}
