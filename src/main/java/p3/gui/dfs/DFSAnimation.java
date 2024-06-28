package p3.gui.dfs;

import javafx.application.Platform;
import p3.graph.Graph;
import p3.gui.Animation;
import p3.gui.AnimationState;
import p3.gui.GraphAnimationScene;
import p3.solver.DFS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjIntConsumer;

public class DFSAnimation<N> extends Animation<N> {

    private final DFSAnimationClass dfs;
    private final DFSAnimationState<N> animationState = new DFSAnimationState<>();

    public DFSAnimation(GraphAnimationScene<N> animationScene) {
        super(animationScene);
        dfs = new DFSAnimationClass(animationScene.getGraph());
    }

    @Override
    public void start() {
        Platform.runLater(() -> animationState.setExecuting("dfs"));

        dfs.traverse((node, discoveryTime) -> {
            if (isAnimating()) {
                updateVisualization(Thread.currentThread().getStackTrace());
                waitUntilNextStep();
            }
        });

        updateVisualization(Thread.currentThread().getStackTrace());
    }

    private void updateVisualization(StackTraceElement[] stackTrace) {
        Platform.runLater(() -> {

            animationState.updateDiscoveryTimes(dfs.getDiscoveryTimes());
            animationState.updateFinishTimes(dfs.getFinishTimes());
            animationState.updatePredecessors(dfs.getPredecessors());
            animationState.updateTime(dfs.getTime());
            animationState.updateCyclic(dfs.getCyclic());

            animationState.setStackTrace(stackTrace);

            for (N node : animationScene.getGraph().getNodes()) {
                animationScene.getGraphPane().setNodeFillColor(node, dfs.getColor(node));
            }
        });
    }

    @Override
    public AnimationState getAnimationState() {
        return animationState;
    }

    private class DFSAnimationClass extends DFS<N> {

        public DFSAnimationClass(Graph<N> graph) {
            super(graph);
        }

        @Override
        protected void visit(ObjIntConsumer<N> consumer, N current) {
            Platform.runLater(() -> animationState.setOperation("visit(%s)".formatted(current)));

            if (isAnimating()) {
                updateVisualization(Thread.currentThread().getStackTrace());
                waitUntilNextStep();
            }

            super.visit(consumer, current);

            Platform.runLater(() -> animationState.setOperation("visit(%s)".formatted(current)));
        }

        @Override
        protected void init() {
            super.init();
            updateVisualization(Thread.currentThread().getStackTrace());
        }

        public javafx.scene.paint.Color getColor(N node) {
            // using switch does not compile because DFS.Color is not public
            DFS.Color color = colors.get(node);
            if (color == Color.BLACK) {
                return javafx.scene.paint.Color.BLACK;
            } else if (color == Color.GRAY) {
                return javafx.scene.paint.Color.DARKGRAY;
            } else {
                return javafx.scene.paint.Color.WHITE;
            }
        }

        public Map<N, Integer> getDiscoveryTimes() {
            Map<N, Integer> discoveryTimes = new HashMap<>();
            for (N node : graph.getNodes()) {
                discoveryTimes.put(node, super.discoveryTimes.get(node));
            }
            return discoveryTimes;
        }

        public Map<N, Integer> getFinishTimes() {
            Map<N, Integer> finishTimes = new HashMap<>();
            for (N node : graph.getNodes()) {
                finishTimes.put(node, super.finishTimes.get(node));
            }
            return finishTimes;
        }

        public Map<N, N> getPredecessors() {
            Map<N, N> predecessors = new HashMap<>();
            for (N node : graph.getNodes()) {
                predecessors.put(node, super.predecessors.get(node));
            }
            return predecessors;
        }

        public int getTime() {
            return time;
        }

        public boolean getCyclic() {
            return cyclic;
        }
    }

}
