package p3.gui.prim;

import p3.gui.InfoBox;

import java.util.List;

public class PrimInfoBox<N> extends InfoBox {

    public PrimInfoBox(PrimAnimationState<N> state) {
        super(state);

        getChildren().addAll(
            mapsToTableView(List.of(state.getPredecessors(), state.getKeys()),
                "Node", List.of("Predecessor", "Key")),
            createBoundLabel("Remaining nodes: ", state.getRemainingNodes())

        );
    }

}
