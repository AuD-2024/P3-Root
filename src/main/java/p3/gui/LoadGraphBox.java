package p3.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import p3.graph.Edge;
import p3.graph.Graph;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class LoadGraphBox<N> extends VBox {

    private final TextField addNodeTextField = new TextField();
    private final TextField addEdgeTextField = new TextField();
    private final TextField setGraphTextField = new TextField();

    private final Button addNodeButton = new Button("Add Node");
    private final Button addEdgeButton = new Button("Add Edge");
    private final Button setGraphButton = new Button("Set Graph");

    public LoadGraphBox(GraphAnimationScene<N> animationScene, Function<String, N> inputParser) {
        super(5);

        setPadding(new Insets(5));
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        getChildren().addAll(createInputGrid());

        initButtonActions(animationScene, inputParser);
    }

    private void initButtonActions(GraphAnimationScene<N> animationScene, Function<String, N> inputParser) {

        addNodeButton.setOnAction(event -> {
            Graph<N> previousGraph = animationScene.getGraph();
            Set<N> nodes = new HashSet<>(previousGraph.getNodes());
            Set<Edge<N>> edges = new HashSet<>(previousGraph.getEdges());

            nodes.add(inputParser.apply(addNodeTextField.getText()));

            animationScene.setGraph(Graph.of(nodes, edges));
        });

        addEdgeButton.setOnAction(event -> {
            Graph<N> previousGraph = animationScene.getGraph();
            Set<N> nodes = new HashSet<>(previousGraph.getNodes());
            Set<Edge<N>> edges = new HashSet<>(previousGraph.getEdges());

            parseEdge(addEdgeTextField.getText(), inputParser);

            animationScene.setGraph(Graph.of(nodes, edges));
        });

        setGraphButton.setOnAction(event -> {
            Set<N> nodes = new HashSet<>();
            Set<Edge<N>> edges = new HashSet<>();

            String[] stringEdges = setGraphTextField.getText().split(";");
            for (String stringEdge : stringEdges) {
                Edge<N> edge = parseEdge(stringEdge, inputParser);
                edges.add(edge);
                nodes.add(edge.from());
                nodes.add(edge.to());
            }

            animationScene.setGraph(Graph.of(nodes, edges));
        });
    }

    private Edge<N> parseEdge(String edgeString, Function<String, N> inputParser) {
        String[] edgeNodes = edgeString.split(",");
        return Edge.of(inputParser.apply(edgeNodes[0]), inputParser.apply(edgeNodes[1]), Integer.parseInt(edgeNodes[2]));
    }

    private GridPane createInputGrid() {
        GridPane inputGrid = new GridPane();

        inputGrid.setHgap(10);
        inputGrid.setVgap(5);
        inputGrid.setPadding(new Insets(5, 5, 5, 5));

        inputGrid.add(addNodeButton, 0, 0);
        inputGrid.add(addNodeTextField, 1, 0);

        inputGrid.add(addEdgeButton, 0, 1);
        inputGrid.add(addEdgeTextField, 1, 1);

        inputGrid.add(setGraphButton, 0, 2);
        inputGrid.add(setGraphTextField, 1, 2);

        return inputGrid;
    }

}
