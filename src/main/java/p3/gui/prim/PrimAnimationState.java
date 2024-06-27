package p3.gui.prim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import p3.gui.AnimationState;

import java.util.Map;
import java.util.Set;

public class PrimAnimationState<N> extends AnimationState {

    private final ObservableMap<N, N> predecessors = FXCollections.observableHashMap();
    private final ObservableMap<N, Integer> keys = FXCollections.observableHashMap();
    private final ObservableList<N> remainingNodes = FXCollections.observableArrayList();

    @Override
    public PrimInfoBox<N> createInfoBox() {
        return new PrimInfoBox<>(this);
    }

    public ObservableMap<N, N> getPredecessors() {
        return predecessors;
    }

    public ObservableMap<N, Integer> getKeys() {
        return keys;
    }

    public ObservableList<N> getRemainingNodes() {
        return remainingNodes;
    }

    public void updatePredecessors(Map<N, N> predecessors) {
        this.predecessors.clear();
        this.predecessors.putAll(predecessors);
    }

    public void updateKeys(Map<N, Integer> keys) {
        this.keys.clear();
        this.keys.putAll(keys);
    }

    public void updateRemainingNodes(Set<N> remainingNodes) {
        this.remainingNodes.clear();
        this.remainingNodes.addAll(remainingNodes);
    }
}
