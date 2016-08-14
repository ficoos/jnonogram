package org.bs.jnonogram.util;

import java.util.List;
import java.util.Stack;

public class UndoRedoStack<T extends UndoableAction> {
    private final UndoStack<T> _undoStack = new UndoStack<>();
    private final Stack<T> _redoStack = new Stack<>();

    public void pushAction(T action) {
        _undoStack.pushAction(action);
        _redoStack.clear();
    }

    public boolean canUndo() {
        return _undoStack.canUndo();
    }

    public void undoAction() {
        T action = _undoStack.undoAction();
        if (action != null) {
            _redoStack.push(action);
        }
    }

    public void redoAction() {
        if (_redoStack.isEmpty()) {
            return;
        }

        T action = _redoStack.pop();
        _undoStack.pushAction(action);
    }

    public List<T> getActionStack() {
        return _undoStack.getActionStack();
    }
}
