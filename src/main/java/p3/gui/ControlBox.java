package p3.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * A pane for controlling the animation.
 * It, for example, contains a button for stepping through the animation and a button for centering the graph.
 */
public class ControlBox<N> extends HBox {

    private final Button nextStepButton;

    /**
     * Constructs a new control box.
     */
    public ControlBox(Stage primaryStage, GraphAnimationScene<N> animationScene) {
        super(5);

        setPadding(new Insets(5));
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        nextStepButton = new Button("Next Step");
        Button zoomOutButton = new Button("Zoom Out");
        Button zoomInButton = new Button("Zoom In");
        Button centerButton = new Button("Center Graph");
        CheckBox animationCheckBox = new CheckBox("Animate");

        animationCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                animationScene.turnOnAnimation();
            } else {
                animationScene.turnOffAnimation();
            }
        });

        animationCheckBox.setSelected(true);

        getChildren().addAll(nextStepButton, centerButton, zoomInButton, zoomOutButton, animationCheckBox);
        setAlignment(Pos.CENTER_LEFT);

        nextStepButton.setDisable(true);
        nextStepButton.setOnAction(event -> {
            synchronized (animationScene.getCurrentAnimation()) {
                animationScene.getCurrentAnimation().notify();
            }
        });

        centerButton.setOnAction(event -> animationScene.getGraphPane().center());
        zoomInButton.setOnAction(event -> animationScene.getGraphPane().zoomIn());
        zoomOutButton.setOnAction(event -> animationScene.getGraphPane().zoomOut());
    }

    public void enableNextStepButton() {
        nextStepButton.setDisable(false);
    }

    public void disableNextStepButton() {
        nextStepButton.setDisable(true);
    }

}
