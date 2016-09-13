package org.bs.jnonogram.util;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Collections;
import java.util.List;

public class UndoStack <T extends UndoableAction> {
    private ListProperty<T> _stack = new SimpleListProperty<T>(this, "Stack", FXCollections.observableArrayList());

    public final void clear() {
        _stack.clear();
    }

    public final void pushAction(T action) {
        action.doAction();
        _stack.add(action);
    }

    public final T undoAction() {
        if (_stack.isEmpty()) {
            return null;
        }

        T action = _stack.remove(_stack.size() -1);
        action.undoAction();
        return action;
    }

    public boolean canUndo() {
        return _stack.size() > 0;
    }

    public List<T> getActionStack() {
        return Collections.unmodifiableList(_stack);
    }

    public ReadOnlyListProperty<T> actionStackProperty() {
        return _stack;
    }
}
