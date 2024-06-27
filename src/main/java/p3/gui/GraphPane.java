package p3.gui;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import p3.graph.Edge;
import p3.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static p3.gui.GraphStyle.ADD_DASHES;
import static p3.gui.GraphStyle.DEFAULT_EDGE_COLOR;
import static p3.gui.GraphStyle.DEFAULT_NODE_FILL_COLOR;
import static p3.gui.GraphStyle.DEFAULT_NODE_STROKE_COLOR;
import static p3.gui.GraphStyle.EDGE_STROKE_WIDTH;
import static p3.gui.GraphStyle.GRID_FIVE_TICKS_WIDTH;
import static p3.gui.GraphStyle.GRID_LINE_COLOR;
import static p3.gui.GraphStyle.GRID_TEN_TICKS_WIDTH;
import static p3.gui.GraphStyle.HIGHLIGHT_COLOR;
import static p3.gui.GraphStyle.NODE_DIAMETER;
import static p3.gui.GraphStyle.NODE_STROKE_WIDTH;
import static p3.gui.GraphStyle.TEXT_COLOR;

/**
 * A {@link Pane} that displays an {@link p3.graph.Graph}.
 * <p>
 * It allows to zoom in and out, drag the displayed tree, center it, and highlights nodes and edges.
 */
public class GraphPane<N> extends Pane {

    private static final double SCALE_IN = 1.1;
    private static final double SCALE_OUT = 1 / SCALE_IN;
    private static final double MAX_SCALE = 10000;
    private static final double MIN_SCALE = 3;

    private final AtomicReference<Point2D> lastPoint = new AtomicReference<>();
    private Affine transformation = new Affine();

    private final Text positionText = new Text();

    private final Map<N, LabeledNode> nodes = new HashMap<>();
    private final Map<N, Location> nodeLocations = new HashMap<>();

    private final Map<Edge<N>, Arrow> edges = new HashMap<>();
    private final Map<N, Map<N, Edge<N>>> nodesToEdge = new HashMap<>();

    private final List<Line> grid = new ArrayList<>();

    private boolean centered = true;

    /**
     * Creates a new, empty {@link GraphPane}.
     */
    public GraphPane() {
        this(null);
    }

    /**
     * Creates a new {@link GraphPane} that initially displays the tree with the given graph node.
     *
     * @param graph the graph to display.
     */
    public GraphPane(Graph<N> graph) {
        // avoid division by zero when scale = 1
        transformation.appendScale(MIN_SCALE, MIN_SCALE);

        initListeners();
        drawGrid();
        drawPositionText();
        positionText.setFill(TEXT_COLOR);

        if (graph != null) setGraph(graph);
    }

    /**
     * Sets the tree to display to the given one.
     *
     * @param graph The graph node of the tree to display.
     */
    public void setGraph(Graph<N> graph) {
        clear();
        calculateNodeLocations(graph);

        for (N node : graph.getNodes()) {
            addNode(node);
        }

        for (Edge<N> edge : graph.getEdges()) {
            addEdge(edge);
        }

        for (LabeledNode node : nodes.values()) {
            node.ellipse().toFront();
            node.text().toFront();
        }

        redrawMap();
        center();
    }

    // --- Edge Handling --- //

    /**
     * Highlights the given {@linkplain Edge edge} by changing its color and stroke settings.
     *
     * @param edge The edge to highlight.
     */
    public void highlightEdge(Edge<N> edge) {
        setEdgeColor(edge, HIGHLIGHT_COLOR);
        if (ADD_DASHES) setEdgeDash(edge, 50, 10.0);
    }

    /**
     * Highlights the edge between the given nodes by changing its color and stroke settings.
     * @param from The source node of the edge.
     * @param to The target node of the edge.
     * @throws IllegalArgumentException If no edge between the given nodes exists.
     */
    public void highlightEdge(N from, N to) {
        highlightEdge(getEdge(from, to));
    }

    /**
     * Updates the color used to draw the given {@linkplain Edge edge}.
     *
     * @param color The new color.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void setEdgeColor(Edge<N> edge, Color color) {
        getEdgeArrow(edge).setStroke(color);
    }

    /**
     * Updates the dashing settings of the stroke used to draw the given {@linkplain Edge edge}.
     *
     * @param edge       The edge to update.
     * @param dashLength The length of the individual dashes.
     * @param gapLength  The length of the gaps between the dashes.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void setEdgeDash(Edge<N> edge, double dashLength, double gapLength) {
        getEdgeArrow(edge).setStrokeDashArray(dashLength, gapLength);
    }

    /**
     * Resets the color used to draw the given {@linkplain Edge edge} to the default color ({@link GraphStyle#DEFAULT_EDGE_COLOR})
     * and removes any dashing settings of the stroke.
     *
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void resetEdge(Edge<N> edge) {
        Arrow arrow = getEdgeArrow(edge);
        arrow.clearStrokeDashArray();
        arrow.setStroke(DEFAULT_EDGE_COLOR);
    }

    /**
     * Resets the color used to draw all edges in this {@link GraphPane} to the default color ({@link GraphStyle#DEFAULT_EDGE_COLOR})
     * and removes any dashing settings of the stroke.
     */
    public void resetAllEdges() {
        for (Edge<N> edge : edges.keySet()) {
            resetEdge(edge);
        }
    }

    /**
     * Updates the position of all {@linkplain Edge edges} on this {@link GraphPane}.
     */
    public void redrawEdges() {
        for (Edge<N> edge : edges.keySet()) {
            redrawEdge(edge);
        }
    }

    private void redrawEdge(Edge<N> edge) {
        Point2D transformedPointA = transform(getLocation(edge.from()));
        Point2D transformedPointB = transform(getLocation(edge.to()));

        Arrow arrow = edges.get(edge);

        arrow.setStartX(transformedPointA.getX());
        arrow.setStartY(transformedPointA.getY());

        arrow.setEndX(transformedPointB.getX());
        arrow.setEndY(transformedPointB.getY());
    }

    // --- Node Handling --- //

    /**
     * Highlights the given node by changing its color and stroke settings.
     *
     * @param node The node to highlight.
     */
    public void highlightNode(N node) {
        setNodeStrokeColor(node, HIGHLIGHT_COLOR);
        if (ADD_DASHES) setNodeDash(node, 10.0, 10.0);
    }

    /**
     * Updates the color used to fill the given node.
     *
     * @param node  The node to update.
     * @param color The new color.
     * @throws IllegalArgumentException If the given node is not part of this {@link GraphPane}.
     */
    public void setNodeFillColor(N node, Color color) {
        getLabeledNode(node).setFillColor(color);
    }

    /**
     * Updates the color used to draw the circumference of given node.
     *
     * @param node  The node to update.
     * @param color The new color.
     * @throws IllegalArgumentException If the given node is not part of this {@link GraphPane}.
     */
    public void setNodeStrokeColor(N node, Color color) {
        getLabeledNode(node).setStrokeColor(color);
    }

    /**
     * Updates the dashing settings of the stroke used to draw the given {@linkplain Edge edge}.
     *
     * @param node       The node to update.
     * @param dashLength The length of the individual dashes.
     * @param gapLength  The length of the gaps between the dashes.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void setNodeDash(N node, double dashLength, double gapLength) {
        getLabeledNode(node).ellipse().getStrokeDashArray().setAll(dashLength, gapLength);
    }


    /**
     * Resets the color used to draw the given node to the default color ({@link GraphStyle#DEFAULT_NODE_FILL_COLOR})
     * and removes any dashing settings of the stroke.
     *
     * @param node The node to update.
     * @throws IllegalArgumentException If the given node is not part of this {@link GraphPane}.
     */
    public void resetNode(N node) {
        LabeledNode labeledNode = getLabeledNode(node);
        labeledNode.ellipse().getStrokeDashArray().clear();
        labeledNode.setStrokeColor(DEFAULT_NODE_FILL_COLOR);
        labeledNode.setFillColor(DEFAULT_NODE_FILL_COLOR);
    }

    /**
     * Resets the color used to draw all nodes in this {@link GraphPane} to the default color ({@link GraphStyle#DEFAULT_NODE_FILL_COLOR})
     * and removes any dashing settings of the stroke.
     */
    public void resetAllNodes() {
        for (N node : nodes.keySet()) {
            resetNode(node);
        }
    }

    /**
     * Updates the position of all nodes on this {@link GraphPane}.
     */
    public void redrawNodes() {
        for (N node : nodes.keySet()) {
            redrawNode(node);
        }
    }

    /**
     * Updates the position of the given node.
     *
     * @param node The node to update.
     * @throws IllegalArgumentException If the given node is not part of this {@link GraphPane}.
     */
    public void redrawNode(N node) {
        if (!nodes.containsKey(node)) {
            throw new IllegalArgumentException("The given node is not part of this TreePane");
        }

        Point2D transformedMidPoint = transform(midPoint(node));

        LabeledNode labeledNode = nodes.get(node);

        labeledNode.ellipse().setCenterX(transformedMidPoint.getX());
        labeledNode.ellipse().setCenterY(transformedMidPoint.getY());

        labeledNode.text().setX(transformedMidPoint.getX() - labeledNode.text().getLayoutBounds().getWidth() / 2.0);
        labeledNode.text().setY(transformedMidPoint.getY() + labeledNode.text().getLayoutBounds().getHeight() / 4.0);
    }

    // --- Other Util --- //

    /**
     * Removes all components from this {@link GraphPane}.
     */
    public void clear() {
        nodes.clear();
        nodeLocations.clear();
        nodesToEdge.clear();
        edges.clear();

        getChildren().clear();
    }

    /**
     * Updates the position of all components on this {@link GraphPane}.
     */
    public void redrawMap() {
        redrawEdges();
        redrawNodes();
    }

    /**
     * Tries to center this {@link GraphPane} as good as possible such that each node is visible while keeping the zoom factor as high as possible.
     */
    public void center() {

        if (getHeight() == 0.0 || getWidth() == 0.0) {
            return;
        }

        if (nodes.isEmpty()) {
            transformation.appendScale(20, 20);
            redrawGrid();
            return;
        }

        double maxX = nodeLocations.values().stream().mapToDouble(Location::x).max().orElse(0);

        double maxY = nodeLocations.values().stream().mapToDouble(Location::y).max().orElse(0);

        double minX = nodeLocations.values().stream().mapToDouble(Location::x).min().orElse(0);

        double minY = nodeLocations.values().stream().mapToDouble(Location::y).min().orElse(0);

        if (minX == maxX) {
            minX = minX - 1;
            maxX = maxX + 1;
        }

        if (minY == maxY) {
            minY = minY - 1;
            maxY = maxY + 1;
        }

        double widthPadding = (maxX - minX) / 10;
        double heightPadding = (maxY - minY) / 10;

        double width = maxX - minX + 2 * widthPadding;
        double height = maxY - minY + 2 * heightPadding;

        Affine inverse = new Affine();

        inverse.appendTranslation(minX - widthPadding, minY - heightPadding);
        inverse.appendScale(width / getWidth(), height / getHeight());

        try {
            transformation = inverse.createInverse();
        } catch (NonInvertibleTransformException e) {
            throw new IllegalStateException("transformation is not invertible");
        }

        redrawGrid();
        redrawMap();

        centered = true;
    }

    /**
     * Increases the zoom of the current treePane, i.e. the size of the covered area is decreased.
     */
    public void zoomIn() {
        zoom(getWidth() / 2.0, getHeight() / 2.0, SCALE_IN);
    }

    /**
     * Decreases the zoom of the current treePane, i.e. the size of the covered area is increased.
     */
    public void zoomOut() {
        zoom(getWidth() / 2.0, getHeight() / 2.0, SCALE_OUT);
    }


    // --- Private Methods --- //

    private void initListeners() {

        setOnMouseDragged(event -> {
                Point2D point = new Point2D(event.getX(), event.getY());
                Point2D diff = getDifference(point, lastPoint.get());

                transformation.appendTranslation(diff.getX() / getTransformScaleX(), diff.getY() / getTransformScaleY());

                redrawMap();
                redrawGrid();
                updatePositionText(point);

                lastPoint.set(point);

                centered = false;
            }
        );

        setOnScroll(event -> {
            if (event.getDeltaY() == 0) {
                return;
            }

            zoom(event.getX(), event.getY(), event.getDeltaY() > 0 ? SCALE_IN : SCALE_OUT);

            centered = false;
        });

        setOnMouseMoved(event -> {
            Point2D point = new Point2D(event.getX(), event.getY());
            lastPoint.set(point);
            updatePositionText(point);
        });

        widthProperty().addListener((obs, oldValue, newValue) -> {
            setClip(new Rectangle(0, 0, getWidth(), getHeight()));

            if (getWidth() != 0.0 && getHeight() != 0.0) {
                redrawGrid();
                redrawMap();
                if (centered) center();
            }

            drawPositionText();
        });

        heightProperty().addListener((obs, oldValue, newValue) -> {
            setClip(new Rectangle(0, 0, getWidth(), getHeight()));

            if (getWidth() != 0.0 && getHeight() != 0.0) {
                redrawGrid();
                redrawMap();
                if (centered) center();
            }

            drawPositionText();
        });
    }

    private void zoom(double x, double y, double scale) {
        if (((getTransformScaleX() < MIN_SCALE || getTransformScaleY() < MIN_SCALE) && scale < 1)
            || ((getTransformScaleX() > MAX_SCALE || getTransformScaleY() > MAX_SCALE) && scale > 1)) {
            return;
        }

        Point2D point = inverseTransform(x, y);
        transformation.appendScale(scale, scale, point.getX(), point.getY());

        redrawMap();
        redrawGrid();
    }

    private void addEdge(Edge<N> edge) {
        edges.put(edge, drawEdge(edge));
        nodesToEdge.computeIfAbsent(edge.from(), k -> new HashMap<>()).put(edge.to(), edge);
    }

    private Arrow drawEdge(Edge<N> edge) {
        Location from = getLocation(edge.from());
        Location to = getLocation(edge.to());

        Point2D transformedA = transform(from);
        Point2D transformedB = transform(to);

        Arrow arrow = new Arrow(transformedA.getX(), transformedA.getY(), transformedB.getX(), transformedB.getY());

        arrow.setStroke(DEFAULT_EDGE_COLOR);
        arrow.setStrokeWidth(EDGE_STROKE_WIDTH);

        getChildren().add(arrow);

        return arrow;
    }

    private void addNode(N node) {
        nodes.put(node, drawNode(node));
    }

    private LabeledNode drawNode(N node) {
        Point2D transformedPoint = transform(getLocation(node));

        Ellipse ellipse = new Ellipse(transformedPoint.getX(), transformedPoint.getY(), NODE_DIAMETER, NODE_DIAMETER);

        ellipse.setFill(DEFAULT_NODE_FILL_COLOR);
        ellipse.setStroke(DEFAULT_NODE_STROKE_COLOR);
        ellipse.setStrokeWidth(NODE_STROKE_WIDTH);

        setMouseTransparent(false);

        Text text = new Text(transformedPoint.getX(), transformedPoint.getY(), node.toString());
        text.setStroke(TEXT_COLOR);

        getChildren().addAll(ellipse, text);

        return new LabeledNode(ellipse, text);
    }

    private void drawGrid() {

        int stepX = (int) (getTransformScaleX() / 2);
        int stepY = (int) (getTransformScaleY() / 2);

        if (stepX < 1 || stepY < 1) {
            return;
        }

        int offsetX = (int) getTransformTranslateX();
        int offsetY = (int) getTransformTranslateY();

        // Vertical Lines
        for (int i = 0, x = offsetX % (stepX * 5); x <= getWidth(); i++, x += stepX) {
            Float strokeWidth = getStrokeWidth(i, offsetX % (stepX * 10) > stepX * 5);
            if (strokeWidth == null) continue;
            Line line = new Line(x, 0, x, getHeight());
            line.setStrokeWidth(strokeWidth);
            line.setStroke(GRID_LINE_COLOR);
            getChildren().add(line);
            grid.add(line);
        }

        // Horizontal Lines
        for (int i = 0, y = offsetY % (stepY * 5); y <= getHeight(); i++, y += stepY) {
            Float strokeWidth = getStrokeWidth(i, offsetY % (stepY * 10) > stepY * 5);
            if (strokeWidth == null) continue;

            var line = new Line(0, y, getWidth(), y);
            line.setStrokeWidth(strokeWidth);
            line.setStroke(GRID_LINE_COLOR);
            getChildren().add(line);
            grid.add(line);
        }
    }

    private Float getStrokeWidth(int i, boolean inverted) {
        float strokeWidth;
        if (i % 10 == 0) {
            strokeWidth = inverted ? GRID_TEN_TICKS_WIDTH : GRID_FIVE_TICKS_WIDTH;
        } else if (i % 5 == 0) {
            strokeWidth = inverted ? GRID_FIVE_TICKS_WIDTH : GRID_TEN_TICKS_WIDTH;
        } else {
            return null;
        }
        return strokeWidth;
    }

    private Edge<N> getEdge(N source, N target) {
        if (nodesToEdge.get(source) == null || nodesToEdge.get(source).get(target) == null) {
            throw new IllegalArgumentException("The given edge is not part of this GraphPane");
        }
        return nodesToEdge.get(source).get(target);
    }

    private Arrow getEdgeArrow(Edge<N> edge) {
        Arrow arrow = edges.get(edge);
        if (arrow == null) {
            throw new IllegalArgumentException("The given edge is not part of this GraphPane");
        }
        return arrow;
    }

    private LabeledNode getLabeledNode(N node) {
        LabeledNode labeledNode = nodes.get(node);

        if (labeledNode == null) {
            throw new IllegalArgumentException("The given node is not part of this GraphPane");
        }

        return labeledNode;
    }

    private Location getLocation(N node) {
        return nodeLocations.getOrDefault(node, Location.ORIGIN);
    }

    private Point2D locationToPoint2D(Location location) {
        return new Point2D(location.x(), location.y());
    }

    private Point2D getDifference(Point2D p1, Point2D p2) {
        return new Point2D(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    private Point2D midPoint(Location location) {
        return new Point2D(location.x(), location.y());
    }

    private Point2D midPoint(N node) {
        return midPoint(getLocation(node));
    }

    private void redrawGrid() {
        getChildren().removeAll(grid);
        grid.clear();
        drawGrid();
    }

    private void drawPositionText() {
        positionText.setX(getWidth() - positionText.getLayoutBounds().getWidth());
        positionText.setY(getHeight());
        positionText.setText("(-, -)");
        if (!getChildren().contains(positionText)) {
            getChildren().add(positionText);
        }
    }

    private void updatePositionText(Point2D point) {
        Point2D inverse = inverseTransform(point);
        positionText.setText("(%d, %d)".formatted((int) inverse.getX(), (int) inverse.getY()));
        positionText.setX(getWidth() - positionText.getLayoutBounds().getWidth());
        positionText.setY(getHeight());
    }

    private Point2D inverseTransform(Point2D point) {
        return inverseTransform(point.getX(), point.getY());
    }

    private Point2D inverseTransform(double x, double y) {
        try {
            return transformation.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            throw new IllegalStateException("transformation is not invertible");
        }
    }

    private Point2D transform(Point2D point) {
        return transformation.transform(point);
    }

    private Point2D transform(Location location) {
        return transformation.transform(locationToPoint2D(location));
    }

    private double getTransformScaleX() {
        return transformation.getMxx();
    }

    private double getTransformScaleY() {
        return transformation.getMyy();
    }

    private double getTransformTranslateX() {
        return transformation.getTx();
    }

    private double getTransformTranslateY() {
        return transformation.getTy();
    }

    /**
     * Calculates a location for each node in the given graph and stores it in the {@link #nodeLocations} map.
     * <p>
     * The algorithm is based on the Fruchterman-Reingold algorithm for force-directed graph drawing.
     * The implementation is based on: https://github.com/Benjoyo/ForceDirectedPlacement
     *
     * @param graph The graph to calculate the node locations for.
     */
    private void calculateNodeLocations(Graph<N> graph) {

        final int iterations = 500;

        final double width = 100;
        final double height = 100;

        final double area = height * height;
        final double forceConstant = 0.4 * Math.sqrt(area / graph.getNodes().size());
        final double coolingRate = 0.01;

        double temperature = width / 10;

        Random random = new Random(0);

        //init with random locations
        for (N node : graph.getNodes()) {
            nodeLocations.put(node, new Location(random.nextDouble() * width, random.nextDouble() * height));
        }

        for (int i = 0; i < iterations; i++) {
            Map<N, Location> forces = new HashMap<>();

            // initialize forces
            for (N node : graph.getNodes()) {
                forces.put(node, Location.ORIGIN);
            }

            // add repulsive forces between all nodes
            for (N node : graph.getNodes()) {
                for (N other : graph.getNodes()) {
                    if (node.equals(other)) {
                        continue;
                    }

                    Location deltaPos = nodeLocations.get(node).sub(nodeLocations.get(other));
                    double length = deltaPos.length();
                    Location normalized = deltaPos.normalize();
                    Location force = normalized.mul((forceConstant * forceConstant) / length);

                    forces.put(node, forces.get(node).add(force));
                }
            }

            // add forces created by edges
            Set<Edge<N>> handledEdges = new HashSet<>();
            for (Edge<N> edge : graph.getEdges()) {

                // avoid handling the same edge twice
                if (handledEdges.contains(Edge.of(edge.to(), edge.from(), edge.weight()))) {
                    continue;
                }
                handledEdges.add(edge);

                // ignore self-loops
                if (edge.from().equals(edge.to())) {
                    continue;
                }

                Location deltaPos = nodeLocations.get(edge.from()).sub(nodeLocations.get(edge.to()));
                double length = deltaPos.length();
                Location normalized = deltaPos.normalize();
                Location force = normalized.mul((length * length) / forceConstant);

                forces.put(edge.from(), forces.get(edge.from()).sub(force));
                forces.put(edge.to(), forces.get(edge.to()).add(force));
            }

            // apply forces to locations
            for (Map.Entry<N, Location> forceEntry : forces.entrySet()) {
                Location force = forceEntry.getValue();
                Location scaled = force.normalize().mul(Math.min(force.length(), temperature));

                Location newLocation = nodeLocations.get(forceEntry.getKey()).add(scaled).bounded(width, 0, height, 0);
                nodeLocations.put(forceEntry.getKey(), newLocation);
            }

            // cool down
            temperature = Math.max(temperature * (1 - coolingRate), 1);
        }

    }

    private record LabeledNode(Ellipse ellipse, Text text) {

        public void setStrokeColor(Color strokeColor) {
            ellipse.setStroke(strokeColor);
        }

        public void setFillColor(Color fillColor) {
            ellipse.setFill(fillColor);
        }

    }

    //TODO add weight labels to edges
    // https://stackoverflow.com/questions/41353685/how-to-draw-arrow-javafx-pane
    private static class Arrow extends Group {

        private final Line line;
        private final Line arrow1;
        private final Line arrow2;

        public Arrow() {
            this(new Line(), new Line(), new Line());
        }

        public Arrow(double startX, double startY, double endX, double endY) {
            this();
            setStartX(startX);
            setStartY(startY);
            setEndX(endX);
            setEndY(endY);
        }

        private static final double arrowLength = 20;
        private static final double arrowWidth = 7;

        private Arrow(Line line, Line arrow1, Line arrow2) {
            super(line, arrow1, arrow2);
            this.line = line;
            this.arrow1 = arrow1;
            this.arrow2 = arrow2;
            InvalidationListener updater = o -> {

                Location lineVector = new Location(getEndX(), getEndY()).sub(new Location(getStartX(), getStartY()));
                Location nodeOffset = lineVector.normalize().mul(NODE_DIAMETER / 2 + NODE_STROKE_WIDTH);

                double ex = getEndX() - nodeOffset.x();
                double ey = getEndY() - nodeOffset.y();
                double sx = getStartX();
                double sy = getStartY();

                arrow1.setEndX(ex);
                arrow1.setEndY(ey);
                arrow2.setEndX(ex);
                arrow2.setEndY(ey);

                if (ex == sx && ey == sy) {
                    // arrow parts of length 0
                    arrow1.setStartX(ex);
                    arrow1.setStartY(ey);
                    arrow2.setStartX(ex);
                    arrow2.setStartY(ey);
                } else {
                    double hypot = Math.hypot(sx - ex, sy - ey);
                    double factor = arrowLength / hypot;
                    double factorO = arrowWidth / hypot;

                    // part in direction of main line
                    double dx = (sx - ex) * factor;
                    double dy = (sy - ey) * factor;

                    // part ortogonal to main line
                    double ox = (sx - ex) * factorO;
                    double oy = (sy - ey) * factorO;

                    arrow1.setStartX(ex + dx - oy);
                    arrow1.setStartY(ey + dy + ox);
                    arrow2.setStartX(ex + dx + oy);
                    arrow2.setStartY(ey + dy - ox);
                }
            };

            // add updater to properties
            startXProperty().addListener(updater);
            startYProperty().addListener(updater);
            endXProperty().addListener(updater);
            endYProperty().addListener(updater);
            updater.invalidated(null);

            arrow1.strokeProperty().bind(line.strokeProperty());
            arrow2.strokeProperty().bind(line.strokeProperty());

            arrow1.strokeWidthProperty().bind(line.strokeWidthProperty());
            arrow2.strokeWidthProperty().bind(line.strokeWidthProperty());
        }

        // start/end properties

        public final void setStartX(double value) {
            line.setStartX(value);
        }

        public final double getStartX() {
            return line.getStartX();
        }

        public final DoubleProperty startXProperty() {
            return line.startXProperty();
        }

        public final void setStartY(double value) {
            line.setStartY(value);
        }

        public final double getStartY() {
            return line.getStartY();
        }

        public final DoubleProperty startYProperty() {
            return line.startYProperty();
        }

        public final void setEndX(double value) {
            line.setEndX(value);
        }

        public final double getEndX() {
            return line.getEndX();
        }

        public final DoubleProperty endXProperty() {
            return line.endXProperty();
        }

        public final void setEndY(double value) {
            line.setEndY(value);
        }

        public final double getEndY() {
            return line.getEndY();
        }

        public final DoubleProperty endYProperty() {
            return line.endYProperty();
        }

        public void setStroke(Color color) {
            line.setStroke(color);
        }

        public void setStrokeWidth(double width) {
            line.setStrokeWidth(width);
        }

        public void setStrokeDashArray(Double... dashes) {
            line.getStrokeDashArray().setAll(dashes);
            arrow1.getStrokeDashArray().setAll(dashes);
            arrow2.getStrokeDashArray().setAll(dashes);
        }

        public void clearStrokeDashArray() {
            line.getStrokeDashArray().clear();
            arrow1.getStrokeDashArray().clear();
            arrow2.getStrokeDashArray().clear();
        }

    }

    /**
     * A data class for storing a location in a 2D plane.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    private record Location(double x, double y) {

        public static final Location ORIGIN = new Location(0, 0);

        public Location add(Location other) {
            return new Location(x + other.x, y + other.y);
        }

        public Location sub(Location other) {
            return new Location(x - other.x, y - other.y);
        }

        public Location mul(double factor) {
            return new Location(x * factor, y * factor);
        }

        public Location div(double factor) {
            return new Location(x / factor, y / factor);
        }

        public double length() {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        public Location normalize() {
            return div(length());
        }

        public Location bounded(double xMax, double xMin, double yMax, double yMin) {
            return new Location(Math.min(xMax, Math.max(xMin, x)), Math.min(yMax, Math.max(yMin, y)));
        }

    }
}
