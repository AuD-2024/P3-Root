package p3.util;

import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.Map;
import java.util.Set;

public class AssertionUtil {

    public static <K, V> void assertMapEquals(Map<K, V> expected, Map<K, V> actual, Context.Builder<?> context, String mapName) {
        assertEquals(expected.size(), actual.size(), context, "The size of the %s map is not correct".formatted(mapName));

        for (Map.Entry<K, V> entry : expected.entrySet()) {
            assertTrue(actual.containsKey(entry.getKey()), context, "The %s map should contain key %s".formatted(mapName, entry.getKey()));
            assertEquals(entry.getValue(), actual.get(entry.getKey()), context, "The %s map contains the wrong value for key %s".formatted(mapName, entry.getKey()));
        }
    }

    public static <E> void assertSetEquals(Set<E> expected, Set<E> actual, Context.Builder<?> context, String setName) {
        assertEquals(expected.size(), actual.size(), context, "The size of the %s set is not correct".formatted(setName));

        for (E element : expected) {
            assertTrue(actual.contains(element), context, "The %s set does not contain the element %s".formatted(setName, element));
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
