package p3.gui;

import javafx.scene.paint.Color;

/**
 * Settings class for changing the appearance of the displayed tree.
 */
public class GraphStyle {

    // --- Node --- //

    public static final double NODE_DIAMETER = 15;
    public static final Color DEFAULT_NODE_FILL_COLOR = Color.DARKGRAY;
    public static final Color DEFAULT_NODE_STROKE_COLOR = Color.BLACK;

    // --- Highlighting --- //

    public static final boolean ADD_DASHES = true;
    public static final Color HIGHLIGHT_COLOR = Color.BLUE;

    // --- Stroke --- //

    public static final int NODE_STROKE_WIDTH = 2;
    public static final int EDGE_STROKE_WIDTH = 2;

    // --- Grid --- //

    public static final float GRID_FIVE_TICKS_WIDTH = .125f;
    public static final float GRID_TEN_TICKS_WIDTH = .25f;
    public static final Color GRID_LINE_COLOR = Color.LIGHTGRAY;

    // --- Text --- //

    public static final Color TEXT_COLOR = Color.GRAY;

    // --- Default --- //

    public static final Color DEFAULT_EDGE_COLOR = Color.BLACK;
}
