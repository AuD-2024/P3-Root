package p3.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Stores the current state of the animation, e.g., the last performed operation, the current executing operation, and the
 * current stack trace
 */
public abstract class AnimationState {

    private final StringProperty operation = new SimpleStringProperty();
    private final StringProperty executing = new SimpleStringProperty();
    private final ObservableList<StackTraceElement> stackTrace = FXCollections.observableArrayList();

    public StringProperty getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation.set(operation);
    }

    public StringProperty getExecuting() {
        return executing;
    }

    public void setExecuting(String executing) {
        this.executing.set(executing);
    }

    public ObservableList<StackTraceElement> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        ArrayList<StackTraceElement> strippedStackTrace = new ArrayList<>();

        for (int i = 1; i < stackTrace.length - 1; i++) {
            if (!stackTrace[i].getClassName().contains("Animation")) {
                strippedStackTrace.add(stackTrace[i]);
            }
        }

        this.stackTrace.setAll(strippedStackTrace);
    }

    public abstract InfoBox createInfoBox();
}
