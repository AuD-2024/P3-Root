package p3.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * A box for displaying information about the current state of the animation represented by an {@link AnimationState}.
 * It automatically updates its content when the state changes.
 */
public class InfoBox extends VBox {

    private final AnimationState state;

    /**
     * Constructs a new info box.
     *
     * @param state The state of the animation to display.
     */
    public InfoBox(AnimationState state) {
        super(5);

        this.state = state;

        setPadding(new Insets(5));
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        setMaxWidth(300);

        getChildren().addAll(
            new Label("Stacktrace:"),
            createStackTraceTableView(),
            createExecutingLabel(),
            createOperationLabel()
        );
    }

    public void showException(Exception e) {
        Label exceptionLabel = new Label(e.getClass().getSimpleName() + " thrown: " + ": " + e.getMessage());
        exceptionLabel.setWrapText(true);
        getChildren().add(exceptionLabel);

        state.setStackTrace(e.getStackTrace());
    }

    private Label createOperationLabel() {
        return createBoundLabel("Current Operation: ", state.getOperation());
    }

    private Label createExecutingLabel() {
        return createBoundLabel("Currently Executing: ", state.getExecuting());
    }

    protected Label createBoundLabel(String descriptor, ObservableValue<?> boundProperty) {
        Label label = new Label();
        label.textProperty().bind(new SimpleStringProperty(descriptor).concat(boundProperty));
        label.setWrapText(true);
        return label;
    }

    protected <E> Label createBoundLabel(String descriptor, ObservableList<E> observableList) {
        SimpleStringProperty text = new SimpleStringProperty();

        observableList.addListener((ListChangeListener<E>) change -> {
            text.set(observableList.toString());
        });

        return createBoundLabel(descriptor, text);
    }

    protected <K> TableView<MapEntries<K>> mapsToTableView(List<ObservableMap<K, ?>> maps, String keyColumnHeader, List<String> valueColumnHeaders) {

        ObservableList<MapEntries<K>> entries = FXCollections.observableArrayList();
        TableView<MapEntries<K>> tableView = new TableView<>(entries);

        TableColumn<MapEntries<K>, K> keyColumn = new TableColumn<>(keyColumnHeader);
        keyColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().key));
        tableView.getColumns().add(keyColumn);

        MapChangeListener<K, Object> listener = change -> {
            entries.clear();

            for (K key : change.getMap().keySet()) {
                List<Object> values = new ArrayList<>();
                for (ObservableMap<K, ?> map : maps) {
                    values.add(map.get(key));
                }
                entries.add(new MapEntries<>(key, values));
            }
        };

        for (int i = 0; i < maps.size(); i++) {
            ObservableMap<K, ?> map = maps.get(i);
            map.addListener(listener);
            TableColumn<MapEntries<K>, Object> valueColumn = new TableColumn<>(valueColumnHeaders.get(i));
            int finalI = i;
            valueColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().values.get(finalI)));
            tableView.getColumns().add(valueColumn);
        }

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        tableView.maxWidth(290);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(30));

        return tableView;
    }

    protected static class MapEntries<K> {

        K key;
        List<Object> values;

        public MapEntries(K key, List<Object> values) {
            this.key = key;
            this.values = values;
        }

    }

    private TableView<StackTraceElement> createStackTraceTableView() {

        TableView<StackTraceElement> tableView = new TableView<>(state.getStackTrace());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);

        tableView.maxWidth(290);

        TableColumn<StackTraceElement, Integer> indexColumn = new TableColumn<>("#");
        indexColumn.setCellValueFactory(data -> new SimpleIntegerProperty(state.getStackTrace().indexOf(data.getValue()) + 1).asObject());
        indexColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<StackTraceElement, String> nameColumn = new TableColumn<>("Class");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClassName().substring(data.getValue().getClassName().lastIndexOf('.') + 1)));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        TableColumn<StackTraceElement, String> methodColumn = new TableColumn<>("Method");
        methodColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMethodName()));
        methodColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        TableColumn<StackTraceElement, Integer> lineColumn = new TableColumn<>("Line");
        lineColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLineNumber()).asObject());
        lineColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        tableView.getColumns().addAll(List.of(indexColumn, nameColumn, methodColumn, lineColumn));

        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(30));

        return tableView;
    }

}
