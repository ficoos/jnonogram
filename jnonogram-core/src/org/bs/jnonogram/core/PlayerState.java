package org.bs.jnonogram.core;

import org.bs.jnonogram.util.UndoRedoStack;
import org.bs.jnonogram.util.UndoableAction;

public class PlayerState {
    private final Nonogram _nonogram;
    private final UndoRedoStack<UndoableAction> _undoRedoStack;
    private final PlayerInfo _playerInfo;
    private int _moveCount;
    private int _undoCount;

    public PlayerState(Nonogram nonogram, PlayerInfo playerInfo) {
        _moveCount = 0;
        _undoCount = 0;
        _nonogram = nonogram;
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
        return _nonogram;
    }

    public PlayerInfo getPlayerInfo() {
        return _playerInfo;
    }

    public void applyMove(NonogramMove move) {
        _undoRedoStack.pushAction(new NonogramMoveAction(_nonogram, move));
        _moveCount++;
    }

    public int getMoveCount() {
        return _moveCount;
    }

    public int getUndoCount() {
        return _undoCount;
    }

    public int getScore() {
        return _nonogram.getScore();
    }
}
