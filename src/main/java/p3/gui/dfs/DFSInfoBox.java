package p3.gui.dfs;

import p3.gui.InfoBox;

import java.util.List;

public class DFSInfoBox<N> extends InfoBox {

    public DFSInfoBox(DFSAnimationState<N> state) {
        super(state);

        getChildren().addAll(
            mapsToTableView(List.of(state.getDiscoveryTimes(), state.getFinishTimes(), state.getPredecessors()),
                "Node", List.of("Disc. Time", "Finish Time", "Predecessor")),
            createBoundLabel("Time: ", state.getTime()),
            createBoundLabel("Cyclic: ", state.getCyclic())
        );
    }
}
