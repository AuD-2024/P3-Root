package p3.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import p3.graph.Graph;

import java.util.function.Function;

/**
 * A scene for displaying a {@link Graph} and performing {@linkplain Animation animations} on it.
 */
public class GraphAnimationScene<N> extends Scene {

    private final BorderPane root;

    private final GraphPane<N> graphPane = new GraphPane<>();
    private final ControlBox<N> controlBox;
    private final VBox rightBox;

    private InfoBox infoBox;

    private Graph<N> graph;

    private Animation<N> currentAnimation;
    private Thread animationThread;

    private boolean animate = true;

    /**
     * Constructs a new animation scene.
     */
    public GraphAnimationScene(Stage primaryStage, Function<String, N> inputParser) {
        super(new BorderPane());
        root = (BorderPane) getRoot();

        this.graph = Graph.empty();

        root.setPrefSize(700, 700);

        root.setCenter(graphPane);

        controlBox = new ControlBox<>(primaryStage, this);
        root.setBottom(controlBox);

        OperationBox<N> operationBox = new OperationBox<>(this, inputParser);
        LoadGraphBox<N> loadGraphBox = new LoadGraphBox<>(this, inputParser);
        rightBox = new VBox(5, operationBox, loadGraphBox);
        rightBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        root.setRight(rightBox);


        graphPane.setGraph(graph);
    }

    public void finishCurrentAnimation() {
        if (animationThread != null) {
            currentAnimation.finishWithNextStep();
            synchronized (currentAnimation) {
                currentAnimation.notify();
            }
        }
        root.setRight(rightBox);
    }

    public void startAnimation(Animation<N> animation) {
        finishCurrentAnimation();
        currentAnimation = animation;

        graphPane.resetAllEdges();
        graphPane.resetAllNodes();

        if (animate) {
            animation.turnOnAnimation();
        } else {
            animation.turnOffAnimation();
        }

        AnimationState animationState = animation.getAnimationState();
        infoBox = animationState.createInfoBox();
        root.setRight(infoBox);
        controlBox.enableNextStepButton();

        animationThread = new Thread(() -> {
            try {
                currentAnimation.start();

                Platform.runLater(() -> {
                    root.setRight(rightBox);
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
                Platform.runLater(() -> infoBox.showException(e));
            }

            Platform.runLater(controlBox::disableNextStepButton);

            currentAnimation = null;
            animationThread = null;
        });

        animationThread.start();
    }

    public void turnOffAnimation() {
        animate = false;
        if (currentAnimation != null) currentAnimation.turnOffAnimation();
    }

    public void turnOnAnimation() {
        animate = true;
        if (currentAnimation != null) currentAnimation.turnOnAnimation();
    }

    public GraphPane<N> getGraphPane() {
        return graphPane;
    }

    public Graph<N> getGraph() {
        return graph;
    }

    public void setGraph(Graph<N> graph) {
        this.graph = graph;
        graphPane.setGraph(graph);
    }

    public Animation<N> getCurrentAnimation() {
        return currentAnimation;
    }

}
