package org.bs.jnonogram.core;

import org.bs.jnonogram.util.UndoableAction;
import sun.invoke.empty.Empty;

import java.util.List;

public class NonogramMoveActionComposite implements UndoableAction {
    private final List<NonogramMoveAction> _moves;
    private final String _comment;
    private final Nonogram.CellKind _targetKind;

    public NonogramMoveActionComposite(List<NonogramMoveAction> moves, String description, Nonogram.CellKind targetKind) {
        _moves = moves;
        _comment = description;
        _targetKind = targetKind;
    }

    @Override
    public final void doAction() {
        for (NonogramMoveAction move : _moves){
            move.doAction();
        }
    }

    @Override
    public final void undoAction() {
        for (NonogramMoveAction move : _moves){
            move.undoAction();
        }
    }

    @Override
    public String toString() {
        String movesDescription = "";
        for (NonogramMoveAction move : _moves){
            movesDescription += move.toString() + ',';
        }
        if(movesDescription.length() != 0)
        {
            movesDescription = movesDescription.substring(0, movesDescription.length() -1);
        }
        return String.format("%s : %s : %s", movesDescription, _targetKind.toString(), _comment);
    }
}
