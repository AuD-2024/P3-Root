package p3.gui.dfs;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import p3.gui.AnimationState;

import java.util.Map;

public class DFSAnimationState<N> extends AnimationState {

    private final ObservableMap<N, Integer> discoveryTimes = FXCollections.observableHashMap();
    private final ObservableMap<N, Integer> finishTimes = FXCollections.observableHashMap();
    private final ObservableMap<N, N> predecessors = FXCollections.observableHashMap();
    private final SimpleIntegerProperty time = new SimpleIntegerProperty();
    private final SimpleBooleanProperty cyclic = new SimpleBooleanProperty();

    @Override
    public DFSInfoBox<N> createInfoBox() {
        return new DFSInfoBox<>(this);
    }

    public ObservableMap<N, Integer> getDiscoveryTimes() {
        return discoveryTimes;
    }

    public ObservableMap<N, Integer> getFinishTimes() {
        return finishTimes;
    }

    public ObservableMap<N, N> getPredecessors() {
        return predecessors;
    }

    public ObservableIntegerValue getTime() {
        return time;
    }

    public SimpleBooleanProperty getCyclic() {
        return cyclic;
    }

    public void updateDiscoveryTimes(Map<N, Integer> discoveryTimes) {
        this.discoveryTimes.clear();
        this.discoveryTimes.putAll(discoveryTimes);
    }

    public void updateFinishTimes(Map<N, Integer> finishTimes) {
        this.finishTimes.clear();
        this.finishTimes.putAll(finishTimes);
    }

    public void updatePredecessors(Map<N, N> predecessors) {
        this.predecessors.clear();
        this.predecessors.putAll(predecessors);
    }

    public void updateTime(int time) {
        this.time.set(time);
    }

    public void updateCyclic(boolean cyclic) {
        this.cyclic.set(cyclic);
    }
}
