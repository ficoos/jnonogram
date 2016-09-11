package org.bs.jnonogram.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableListValue;
import org.bs.jnonogram.util.UndoRedoStack;
import org.bs.jnonogram.util.UndoableAction;

import java.util.List;

public class PlayerState {
    private final ObjectProperty<Nonogram> _nonogramProperty = new SimpleObjectProperty<>(this, "Nonogram");
    private final UndoRedoStack<UndoableAction> _undoRedoStack;
    private final PlayerInfo _playerInfo;
    private int _moveCount;
    private int _undoCount;
    private Object moveList;

    public PlayerState(Nonogram nonogram, PlayerInfo playerInfo) {
        _moveCount = 0;
        _undoCount = 0;
        _nonogramProperty.setValue(nonogram);
        _playerInfo = playerInfo;
        _undoRedoStack = new UndoRedoStack<>();
    }

    public final void undoAction() {
        if (_undoRedoStack.canUndo()) {
            _undoRedoStack.undoAction();
            _undoCount++;
        }
    }

    public final void redoAction() { _undoRedoStack.redoAction(); }

    public final ReadOnlyNonogram getNonogram() {
        return _nonogramProperty.getValue();
    }

    public PlayerInfo getPlayerInfo() {
        return _playerInfo;
    }

    public void applyMove(NonogramMove move) {
        _undoRedoStack.pushAction(new NonogramMoveAction(_nonogramProperty.getValue(), move));
        _moveCount++;
    }

    public int getMoveCount() {
        return _moveCount;
    }

    public int getUndoCount() {
        return _undoCount;
    }

    public int getScore() {
        return _nonogramProperty.getValue().getScore();
    }

    public List<UndoableAction> getActionList() {
        return _undoRedoStack.getActionStack();
    }

    public ObjectProperty<Nonogram> nonogramProperty() {
        return _nonogramProperty;
    }

    public ObservableListValue<UndoableAction> actionListProperty() {
        return _undoRedoStack.actionListProperty();
    }
}
