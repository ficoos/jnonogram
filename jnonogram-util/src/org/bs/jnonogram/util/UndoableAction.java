package org.bs.jnonogram.util;

public interface UndoableAction {
    void doAction();
    void undoAction();
}
