package p3.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * The initial scene for loading a {@link p3.graph.Graph} from a string representation.
 */
public class LoadTreeScene extends Scene {

    /**
     * Constructs a new load tree scene.
     *
     * @param primaryStage The stage used to display the scene.
     */
    public LoadTreeScene(Stage primaryStage) {
        super(new BorderPane());
        BorderPane root = (BorderPane) getRoot();

        root.setPrefSize(500, 150);

        VBox vBox = new VBox();

        Text description = new Text("Enter string representation of tree or leave empty for empty tree:");

        TextField textField = new TextField();
        textField.setMaxWidth(description.getLayoutBounds().getWidth());

        Button loadRBTreeButton = new Button("Load Red-Black Tree");
        Button loadBSTButton = new Button("Load Simple Binary Search Tree");

        HBox buttonBox = new HBox(loadRBTreeButton, loadBSTButton);
        buttonBox.setSpacing(5);

//        loadRBTreeButton.setOnAction(event -> loadScene(primaryStage, loadRBTree(textField.getText())));
//        loadBSTButton.setOnAction(event -> loadScene(primaryStage, loadBST(textField.getText())));
//
//        textField.textProperty().addListener((obs, oldValue, newValue) -> {
//            loadBSTButton.setDisable(!checkInputValid(this::loadBST, newValue));
//            loadRBTreeButton.setDisable(!checkInputValid(this::loadRBTree, newValue));
//        });

        vBox.getChildren().addAll(description, textField, buttonBox);

        root.setCenter(vBox);

        Text title = new Text("Load Binary Tree");
        title.setFont(new Font(40));
        root.setTop(title);

        BorderPane.setAlignment(title, Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);
    }

    private boolean checkInputValid(Consumer<String> treeLoader, String input) {
        try {
            treeLoader.accept(input);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

//    private TreeAndParser<?> loadBST(String text) {
//        if (text.isEmpty()) {
//            return new TreeAndParser<>(new SimpleBinarySearchTreeAnimation<Integer>(), Integer::parseInt);
//        } else if (text.chars().anyMatch(Character::isDigit)) {
//            Function<String, Integer> inputParser = Integer::parseInt;
//            return new TreeAndParser<>(TreeParser.parseBST(text, inputParser, new SimpleBinarySearchTreeAnimation<>()), inputParser);
//        } else {
//            Function<String, String> inputParser = Function.identity();
//            return new TreeAndParser<>(TreeParser.parseBST(text, inputParser, new SimpleBinarySearchTreeAnimation<>()), inputParser);
//        }
//    }
//
//    private TreeAndParser<?> loadRBTree(String text) {
//        if (text.isEmpty()) {
//            return new TreeAndParser<>(new RBTreeAnimation<Integer>(), Integer::parseInt);
//        } else if (text.chars().anyMatch(Character::isDigit)) {
//            Function<String, Integer> inputParser = Integer::parseInt;
//            return new TreeAndParser<>(TreeParser.parseRBTree(text, inputParser, new RBTreeAnimation<>()), inputParser);
//        } else {
//            Function<String, String> inputParser = Function.identity();
//            return new TreeAndParser<>(TreeParser.parseRBTree(text, inputParser, new RBTreeAnimation<>()), inputParser);
//        }
//    }
//
//    private <T extends Comparable<T>> void loadScene(Stage primaryStage, TreeAndParser<T> treeAndParser) {
//        GraphAnimationScene<T> animationScene = new GraphAnimationScene<>(primaryStage, treeAndParser.tree, treeAndParser.inputParser);
//
//        MyApplication.currentScene = animationScene;
//
//        treeAndParser.tree.init(animationScene);
//
//        primaryStage.setScene(animationScene);
//        primaryStage.show();
//    }
//
//    private record TreeAndParser<T extends Comparable<T>>(AnimatedBinaryTree<T> tree, Function<String, T> inputParser) {
//    }

}
