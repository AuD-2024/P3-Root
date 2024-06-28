package p3;

import p3.graph.Edge;
import p3.gui.MyApplication;

import java.util.function.ObjIntConsumer;

/**
 * Main entry point in executing the program.
 */
public class Main {


    /**
     * Main entry point in executing the program.
     *
     * <h2>GUI Guide:</h2>
     * <p>
     * After starting the gui an empty graph is displayed. You can add nodes and edges one by one clicking on the
     * "Add Node" and "Add Edge" buttons on the right after entering the respective values in the text fields.
     * Nodes are represented by integers and edges in the format {@code from,to,weight}. If a failed test outputs a
     * "gui String" you can load the corresponding graph by pasting the string into the text field for "Set Graph" and
     * then clicking the "Set Graph" button.
     * <p>
     * When a graph is loaded, you can view it in the center. On the top right you can enter input values and execute
     * the respective operation by clicking the buttons below.
     * <ul>
     *     <li>DFS: Invokes the {@link p3.solver.DFS#traverse(ObjIntConsumer)} method with the current graph.</li>
     *     <li>Prim: Invokes the {@link p3.solver.PrimMSTCalculator#calculateMST(Object)} method with the current graph
     *     and the value in the root text field as the parameter.</li>
     *     <li>BellmanFord: Invokes the {@link p3.solver.BellmanFordPathCalculator#calculatePath(Object, Object)} method
     *     with the current graph and the value in the start and end text fields as the parameters.
     * </ul>
     * <p>
     * When the "Animate" checkbox at the bottom left is selected, the program will stop before each invocation of
     * {@link p3.solver.DFS#visit(ObjIntConsumer, Object)}, {@link p3.solver.PrimMSTCalculator#processNode(Object)} and
     * {@link p3.solver.BellmanFordPathCalculator#relax(Edge)}. You can then continue the animation by clicking the
     * "Next Step" button at the bottom left. When stopped, the current stack trace, last performed operation and
     * the current state of the algorithm is shown at the right.
     * <p>
     * You can change the appearance and colors of the tree in the class {@link p3.gui.GraphStyle}.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(String[] args) {
        MyApplication.launch(MyApplication.class, args);
    }

}
