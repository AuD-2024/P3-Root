package p3.gui.bellmanFord;

import javafx.application.Platform;
import p3.graph.Edge;
import p3.graph.Graph;
import p3.gui.Animation;
import p3.gui.AnimationState;
import p3.gui.GraphAnimationScene;
import p3.gui.GraphPane;
import p3.solver.BellmanFordPathCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BellmanFordAnimation<N> extends Animation<N> {

    private final BellmanFordAnimationClass bellmanFord;
    private final BellmanFordAnimationState<N> animationState = new BellmanFordAnimationState<>();

    private final N start;
    private final N end;

    public BellmanFordAnimation(GraphAnimationScene<N> animationScene, N start, N end) {
        super(animationScene);
        bellmanFord = new BellmanFordAnimationClass(animationScene.getGraph());
        this.start = start;
        this.end = end;
    }

    @Override
    public void start() {
        Platform.runLater(() -> animationState.setExecuting("calculatePath(%s, %s)".formatted(start, end)));
        List<N> path = bellmanFord.calculatePath(start, end);

        animationScene.getGraphPane().resetAllEdges();
        animationScene.getGraphPane().resetAllNodes();

        for (int i = 1; i < path.size(); i++) {
            animationScene.getGraphPane().highlightEdge(path.get(i - 1), path.get(i));
        }
    }

    @Override
    public AnimationState getAnimationState() {
        return animationState;
    }

    private void updateVisualization(StackTraceElement[] stackTrace) {
        Platform.runLater(() -> {

            animationState.updatePredecessors(bellmanFord.getPredecessors());
            animationState.updateDistances(bellmanFord.getDistances());

            animationState.setStackTrace(stackTrace);

            GraphPane<N> graphPane = animationScene.getGraphPane();
            graphPane.resetAllEdges();
            graphPane.resetAllNodes();

            for (Map.Entry<N, N> entry : bellmanFord.getPredecessors().entrySet()) {
                N node = entry.getKey();
                N predecessor = entry.getValue();
                if (predecessor != null) {
                    graphPane.highlightEdge(predecessor, node);
                    graphPane.highlightNode(node);
                    graphPane.highlightNode(predecessor);
                }
            }
        });
    }

    private class BellmanFordAnimationClass extends BellmanFordPathCalculator<N> {

        public BellmanFordAnimationClass(Graph<N> graph) {
            super(graph);
        }

        @Override
        protected void relax(Edge<N> edge) {
            Platform.runLater(() -> getAnimationState().setOperation("relax(%s)".formatted(edge)));
            if (isAnimating()) {
                updateVisualization(Thread.currentThread().getStackTrace());
                waitUntilNextStep();
            }

            super.relax(edge);
        }

        public Map<N, N> getPredecessors() {
            Map<N, N> predecessors = new HashMap<>();
            for (N node : graph.getNodes()) {
                if (super.predecessors.get(node) != null) {
                    predecessors.put(node, super.predecessors.get(node));
                }
            }
            return predecessors;
        }

        public Map<N, Integer> getDistances() {
            Map<N, Integer> distances = new HashMap<>();
            for (N node : graph.getNodes()) {
                if (super.distances.get(node) != null) {
                    distances.put(node, super.distances.get(node));
                }
            }
            return distances;
        }
    }
}
