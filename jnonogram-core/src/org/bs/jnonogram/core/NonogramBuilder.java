package org.bs.jnonogram.core;

import java.util.ArrayList;

public class NonogramBuilder {

    private final NonogramConstraint[] _rowConstraints;
    private final NonogramConstraint[] _columnConstraints;
    private final ArrayList<CellPosition> _solution;

    public NonogramBuilder(int rows, int columns) {
        _rowConstraints = new NonogramConstraint[rows];
        _columnConstraints = new NonogramConstraint[columns];
        _solution = new ArrayList<>();
    }

    public final NonogramBuilder addSlice(SliceOrientation orientation, int index, Iterable<Integer> sizes) {
        switch (orientation) {
            case Column:
                _columnConstraints[index] = new NonogramConstraint(sizes);
                break;
            case Row:
                _rowConstraints[index] = new NonogramConstraint(sizes);
                break;
        }

        return this;
    }


    public final NonogramBuilder addSolutionBlock(int column, int row) {
        return this.addSolutionBlock(new CellPosition(column, row));
    }

    public final NonogramBuilder addSolutionBlock(CellPosition position) {
        this._solution.add(position);
        return this;
    }

    public final Nonogram build() {
        Nonogram nonogram = new Nonogram(_rowConstraints, _columnConstraints, _solution);
        return nonogram;
    }
}
