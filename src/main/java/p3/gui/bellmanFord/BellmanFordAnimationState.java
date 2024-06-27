package p3.gui.bellmanFord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import p3.gui.AnimationState;

import java.util.Map;

public class BellmanFordAnimationState<N> extends AnimationState {

    private final ObservableMap<N, Integer> distances = FXCollections.observableHashMap();

    private final ObservableMap<N, N> predecessors = FXCollections.observableHashMap();

    @Override
    public BellmanFordInfoBox<N> createInfoBox() {
        return new BellmanFordInfoBox<>(this);
    }

    public ObservableMap<N, Integer> getDistances() {
        return distances;
    }

    public ObservableMap<N, N> getPredecessors() {
        return predecessors;
    }

    public void updateDistances(Map<N, Integer> distances) {
        this.distances.clear();
        this.distances.putAll(distances);
    }

    public void updatePredecessors(Map<N, N> predecessors) {
        this.predecessors.clear();
        this.predecessors.putAll(predecessors);
    }

}
