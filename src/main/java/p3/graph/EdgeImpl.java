package p3.graph;

import java.util.Objects;

/**
 * A basic implementation of an {@link Edge}.
 *
 * @param from   The node that this edge starts at.
 * @param to     The node that this edge end at.
 * @param weight The weight of this edge.
 * @param <N>    The type of the nodes in the graph.
 */
record EdgeImpl<N>(N from, N to, int weight) implements Edge<N> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeImpl<?> edge = (EdgeImpl<?>) o;
        return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
