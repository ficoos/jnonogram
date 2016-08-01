package org.bs.jnonogram.core;

import java.util.ArrayList;

public final class Nonogram implements ReadOnlyNonogram {
    private final CellKind[][] _cells;
    private final NonogramConstraint[] _rowConstraints;
    private final NonogramConstraint[] _columnConstraints;
    private final CellKind[][] _solution;

    @Override
    public NonogramConstraint[] getRowConstraints() {
        return _rowConstraints;
    }

    @Override
    public NonogramConstraint[] getColumnConstraints() {
        return _columnConstraints;
    }

    public enum CellKind {
        White,
        Black,
        Unknown
    }

    public Nonogram(
            NonogramConstraint[] rowConstraints,
            NonogramConstraint[] columnConstraints,
            Iterable<CellPosition> solution) {
        _columnCount = columnConstraints.length;
        _rowCount = rowConstraints.length;
        _cells = new CellKind[_columnCount][_rowCount];
        _solution = new CellKind[_columnCount][_rowCount];
        for (CellPosition position : solution) {
            _solution[position.getColumn()][position.getRow()] = CellKind.Black;
        }

        for (int column = 0; column < _columnCount; column++) {
            for (int row = 0; row < _rowCount; row++) {
                if (_solution[column][row] == null) {
                    _solution[column][row] = CellKind.White;
                }
            }
        }

        _rowConstraints = rowConstraints;
        _columnConstraints = columnConstraints;
        reset();
    }

    public final void reset() {
        for (int column = 0; column < _cells.length; column++) {
            for (int row = 0; row < _cells[column].length; row++) {
                _cells[column][row] = CellKind.Unknown;
            }
        }
    }

    @Override
    public final int getScore() {
        int correct = 0;
        for (int column = 0; column < _columnCount; column++) {
            for (int row = 0; row < _rowCount; row++) {
                if (_cells[column][row].equals(_solution[column][row])) {
                    correct++;
                }
            }
        }

        return (correct * 100) / (_rowCount * _columnCount);
    }

    private final int _columnCount;

    @Override
    public final int getColumnCount() {
        return _columnCount;
    }

    private final int _rowCount;

    @Override
    public final int getRowCount() {
        return _rowCount;
    }

    @Override
    public final CellKind getCellAt(int column, int row)
    {
        return _cells[column][row];
    }

    @Override
    public final CellKind getCellAt(CellPosition position)
    {
        return getCellAt(position.getColumn(), position.getRow());
    }

    public final void setCellAt(int x, int y, CellKind kind) {
        _cells[x][y] = kind;
    }

    public final void setCellAt(CellPosition position, CellKind kind) {
        setCellAt(position.getColumn(), position.getRow(), kind);
    }

    public final Nonogram clone() {
        ArrayList<CellPosition> solution = new ArrayList<>();
        for (int column = 0; column < _columnCount; column++) {
            for (int row = 0; row < _rowCount; row++) {
                if (_solution[column][row] == CellKind.Black) {
                    solution.add(new CellPosition(column, row));
                }
            }
        }

        Nonogram nonogram = new Nonogram(
                this._rowConstraints.clone(),
                this._columnConstraints.clone(),
                solution);
        for (int x = 0; x < this._columnCount; x++) {
            for (int y = 0; y < this._rowCount; y++) {
                nonogram._cells[x][y] = this._cells[x][y];
            }
        }

        return  nonogram;
    }
}
