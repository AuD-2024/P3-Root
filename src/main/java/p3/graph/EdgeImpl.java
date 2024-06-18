package p3.graph;

import java.util.Objects;

/**
 * A basic implementation of an {@link Edge}.
 * @param from The first node in the edge.
 * @param to The second node in the edge.
 * @param weight The weight of the edge.
 * @param <N> The type of the nodes in the graph.
 */
record EdgeImpl<N>(Node<N> from, Node<N> to, int weight) implements Edge<N> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeImpl<?> edge = (EdgeImpl<?>) o;
        return ((Objects.equals(from, edge.from) && Objects.equals(to, edge.to)) ||
            (Objects.equals(from, edge.to) && Objects.equals(to, edge.from)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
