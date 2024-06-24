package p3.solver;

import p3.graph.Graph;

import java.util.LinkedList;
import java.util.List;

public class TopologicalSort<N> {
    protected final GraphTraverser<N> dfs;

    public TopologicalSort(Graph<N> graph) {
        this.dfs = DFS.FACTORY.create(graph);
    }

    public List<N> sort() {
        LinkedList<N> sortedNodes = new LinkedList<>();
        dfs.traverse((node, ignored) -> {
            if (sortedNodes.contains(node)) {
                throw new CycleException("Topological Graph is not acyclic");
            }

            sortedNodes.addFirst(node);
        });

        return sortedNodes;
    }
}
