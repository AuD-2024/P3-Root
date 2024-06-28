package p3.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import p3.gui.bellmanFord.BellmanFordAnimation;
import p3.gui.dfs.DFSAnimation;
import p3.gui.prim.PrimAnimation;

import java.util.function.Function;

/**
 * A box for triggering optionally animated operations on the displayed {@link p3.graph.Graph}.
 *
 * @param <N> The type of the elements in the graph.
 */
@SuppressWarnings("DuplicatedCode")
public class OperationBox<N> extends VBox {

    private final TextField rootTextField = new TextField();
    private final TextField startTextField = new TextField();
    private final TextField endTextField = new TextField();

    private final BooleanProperty valueIsValid = new SimpleBooleanProperty();
    private final BooleanProperty startIsValid = new SimpleBooleanProperty();
    private final BooleanProperty endIsValid = new SimpleBooleanProperty();

    /**
     * Constructs a new operation box.
     *
     * @param animationScene The scene containing the graph to operate on.
     * @param inputParser    A function for parsing the input strings to the type of the elements in the graph.
     */
    public OperationBox(GraphAnimationScene<N> animationScene, Function<String, N> inputParser) {
        super(5);

        setPadding(new Insets(5));
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        initValidInputBindings(inputParser);

        HBox buttons = new HBox(5,
            createDFSButton(animationScene),
            createPrimButton(animationScene, inputParser),
            createBellmanFordButton(animationScene, inputParser));

        buttons.setAlignment(Pos.CENTER);

        getChildren().addAll(createInputGrid(), buttons);
    }

    private void initValidInputBindings(Function<String, N> inputParser) {
        rootTextField.textProperty().addListener((obs, oldValue, newValue) -> valueIsValid.setValue(isInputValid(newValue, inputParser)));
        startTextField.textProperty().addListener((obs, oldValue, newValue) -> startIsValid.setValue(isInputValid(newValue, inputParser)));
        endTextField.textProperty().addListener((obs, oldValue, newValue) -> endIsValid.setValue(isInputValid(newValue, inputParser)));
    }

    private boolean isInputValid(String str, Function<String, N> inputParser) {
        try {
            inputParser.apply(str);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Button createDFSButton(GraphAnimationScene<N> animationScene) {
        Button dfsButton = new Button("DFS");

        dfsButton.setOnAction(event -> animationScene.startAnimation(new DFSAnimation<>(animationScene)));

        return dfsButton;
    }

    private Button createBellmanFordButton(GraphAnimationScene<N> animationScene, Function<String, N> inputParser) {
        Button bellmanFordButton = new Button("BellmanFord");

        bellmanFordButton.setOnAction(event -> animationScene.startAnimation(new BellmanFordAnimation<>(
            animationScene, inputParser.apply(startTextField.getText()), inputParser.apply(endTextField.getText()))));

        bellmanFordButton.disableProperty().bind(startIsValid.and(endIsValid).not());

        return bellmanFordButton;
    }

    private Button createPrimButton(GraphAnimationScene<N> animationScene, Function<String, N> inputParser) {
        Button primButton = new Button("Prim");

        primButton.setOnAction(event -> animationScene.startAnimation(new PrimAnimation<>(animationScene, inputParser.apply(rootTextField.getText()))));

        primButton.disableProperty().bind(valueIsValid.not());

        return primButton;
    }

    private GridPane createInputGrid() {
        GridPane inputGrid = new GridPane();

        inputGrid.setHgap(10);
        inputGrid.setVgap(5);
        inputGrid.setPadding(new Insets(5, 5, 5, 5));

        inputGrid.add(new Label("Prim Root:"), 0, 0);
        inputGrid.add(rootTextField, 1, 0);

        inputGrid.add(new Label("BellmanFord Start:"), 0, 1);
        inputGrid.add(startTextField, 1, 1);

        inputGrid.add(new Label("BellmanFord End:"), 0, 2);
        inputGrid.add(endTextField, 1, 2);

        return inputGrid;
    }

}
