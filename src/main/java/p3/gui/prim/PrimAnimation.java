package p3.gui.prim;

import javafx.application.Platform;
import p3.graph.Edge;
import p3.graph.Graph;
import p3.gui.Animation;
import p3.gui.AnimationState;
import p3.gui.GraphAnimationScene;
import p3.gui.GraphPane;
import p3.solver.PrimMSTCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrimAnimation<N> extends Animation<N> {

    private final PrimAnimationClass prim;
    private final PrimAnimationState<N> animationState = new PrimAnimationState<>();

    private final N root;

    public PrimAnimation(GraphAnimationScene<N> animationScene, N root) {
        super(animationScene);
        prim = new PrimAnimationClass(animationScene.getGraph());
        this.root = root;
    }

    @Override
    public void start() {
        Platform.runLater(() -> animationState.setExecuting("calculateMST(%s)".formatted(root)));
        Graph<N> mst = prim.calculateMST(root);

        animationScene.getGraphPane().resetAllEdges();
        animationScene.getGraphPane().resetAllNodes();

        for (Edge<N> edge : mst.getEdges()) {
            animationScene.getGraphPane().highlightEdge(edge);
        }
    }

    @Override
    public AnimationState getAnimationState() {
        return animationState;
    }

    private void updateVisualization(StackTraceElement[] stackTrace) {
        Platform.runLater(() -> {

            animationState.updatePredecessors(prim.getPredecessors());
            animationState.updateKeys(prim.getKeys());
            animationState.updateRemainingNodes(prim.getRemainingNodes());

            animationState.setStackTrace(stackTrace);

            GraphPane<N> graphPane = animationScene.getGraphPane();
            graphPane.resetAllEdges();
            graphPane.resetAllNodes();

            for (Map.Entry<N, N> entry : prim.getPredecessors().entrySet()) {
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

    private class PrimAnimationClass extends PrimMSTCalculator<N> {

        public PrimAnimationClass(Graph<N> graph) {
            super(graph);
        }

        @Override
        protected void processNode(N node) {
            Platform.runLater(() -> animationState.setOperation("processNode(%s)".formatted(node)));
            if (isAnimating()) {
                updateVisualization(Thread.currentThread().getStackTrace());
                waitUntilNextStep();
            }

            super.processNode(node);
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

        public Map<N, Integer> getKeys() {
            Map<N, Integer> keys = new HashMap<>();
            for (N node : graph.getNodes()) {
                keys.put(node, super.keys.get(node));
            }
            return keys;
        }

        public Set<N> getRemainingNodes() {
            return super.remainingNodes;
        }
    }
}
