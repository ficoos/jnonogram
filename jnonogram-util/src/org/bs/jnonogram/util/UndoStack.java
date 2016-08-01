package org.bs.jnonogram.util;

import java.util.Stack;

public class UndoStack <T extends UndoableAction> {
    private Stack<T> _stack = new Stack<>();

    public final void clear() {
        _stack.clear();
    }

    public final void pushAction(T action) {
        action.doAction();
        _stack.push(action);
    }

    public final T undoAction() {
        if (_stack.isEmpty()) {
            return null;
        }

        T action = _stack.pop();
        action.undoAction();
        return action;
    }

    public boolean canUndo() {
        return _stack.size() > 0;
    }
}
