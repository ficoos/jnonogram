package org.bs.jnonogram.core;

import org.bs.jnonogram.util.UndoableAction;

public class NonogramMoveAction implements UndoableAction {
    private final Nonogram.CellKind[] _backup;
    private final NonogramMove _move;
    private final Nonogram _nonogram;

    public NonogramMoveAction(Nonogram nonogram, NonogramMove move) {
        _nonogram = nonogram;
        _move = move;
        _backup = new Nonogram.CellKind[_move.getSize()];
    }

    @Override
    public final void doAction() {
        final int[] i = {0};
        _move.forEachCell(cellPosition -> {
            _backup[i[0]] = _nonogram.getCellAt(cellPosition);
            _nonogram.setCellAt(cellPosition, _move.getTargetKind());
            i[0]++;
        });
        _nonogram.updateSatisfiedConstraints();
    }

    @Override
    public final void undoAction() {
        final int[] i = {0};
        _move.forEachCell(cellPosition -> {
            _nonogram.setCellAt(cellPosition, _backup[i[0]]);
            i[0]++;
        });
        _nonogram.updateSatisfiedConstraints();
    }

    @Override
    public String toString() {
        return _move.toString();
    }
}
