package p3.gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point in executing the GUI.
 */
public class MyApplication extends Application {

    /**
     * The current {@link GraphAnimationScene} that is displayed. This is used to stop the animation when the
     * application is closed.
     */
    public static GraphAnimationScene<?> currentScene = null;

    @Override
    public void start(Stage primaryStage) {
        GraphAnimationScene<Integer> graphAnimationScene = new GraphAnimationScene<>(primaryStage, Integer::parseInt);
        currentScene = graphAnimationScene;

        primaryStage.setScene(graphAnimationScene);
        primaryStage.setTitle("Graph Animation");

        primaryStage.show();
    }

    @Override
    public void stop() {
        if (currentScene != null) currentScene.finishCurrentAnimation();
    }

}
