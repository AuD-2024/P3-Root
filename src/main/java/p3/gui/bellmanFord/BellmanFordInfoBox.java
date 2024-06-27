package p3.gui.bellmanFord;

import p3.gui.InfoBox;

import java.util.List;

public class BellmanFordInfoBox<N> extends InfoBox {

    public BellmanFordInfoBox(BellmanFordAnimationState<N> state) {
        super(state);

        getChildren().addAll(
            mapsToTableView(List.of(state.getDistances(), state.getPredecessors()),
                "Node", List.of("Distance", "Predecessor"))
        );
    }

}
