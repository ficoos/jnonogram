package org.bs.jnonogram.core;

import java.util.function.Consumer;

public final class NonogramMove {
    private SliceOrientation _orientation = SliceOrientation.Column;
    private CellPosition _origin = new CellPosition(0,0);
    private int _size = 0;
    private Nonogram.CellKind _targetKind = Nonogram.CellKind.Black;
    private String _comment = "";

    public final SliceOrientation getOrientation() {
        return _orientation;
    }

    public final void setOrientation(SliceOrientation orientation) {
        this._orientation = orientation;
    }

    public final CellPosition getOrigin() {
        return _origin;
    }

    public final void setOrigin(CellPosition origin) {
        this._origin = origin;
    }

    public final int getSize() {
        return _size;
    }

    public final void setSize(int size) {
        this._size = size;
    }

    @Override
    public String toString() {
        return String.format(
                "(%d,%d)",
                (_origin.getColumn() + 1),
                (_origin.getRow() +1));
    }

    public Nonogram.CellKind getTargetKind() {
        return _targetKind;
    }

    public void setTargetKind(Nonogram.CellKind targetKind) {
        this._targetKind = targetKind;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        this._comment = comment;
    }

    public void forEachCell(Consumer<CellPosition> consumer) {
        int dr = 0;
        int dc = 0;
        if (_orientation.equals(SliceOrientation.Column)) {
            dr = 1;
        } else {
            dc = 1;
        }

        int row = _origin.getRow();
        int column = _origin.getColumn();
        for (int i = 0; i < _size; i++) {
            consumer.accept(new CellPosition(column, row));
            row += dr;
            column += dc;
        }
    }
}
