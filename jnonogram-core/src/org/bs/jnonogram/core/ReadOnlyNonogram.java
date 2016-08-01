package org.bs.jnonogram.core;

public interface ReadOnlyNonogram {
    int getColumnCount();
    int getRowCount();

    Nonogram.CellKind getCellAt(int x, int y);

    NonogramConstraint[] getRowConstraints();

    NonogramConstraint[] getColumnConstraints();

    Nonogram.CellKind getCellAt(CellPosition position);

    int getScore();
}
