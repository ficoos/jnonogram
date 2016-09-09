package org.bs.jnonogram.core;

import org.bs.jnonogram.core.jax.Block;

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

    public CellKind[][] getSolution() { return _solution; }

    public enum CellKind {
        White ("White"),
        Black ("Black"),
        Unknown ("Unknown");

        private final String name;
        private CellKind(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
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

    public final void updateSatisfiedConstraints()
    {
        updateSatisfiedConstraints(_rowConstraints, _columnConstraints, _cells);
    }

    public final void updateSatisfiedConstraints(NonogramConstraint[] rowConstraints, NonogramConstraint[] columnConstraints, CellKind[][] cells)
    {
        for(int i=0; i < cells.length; i++)
        {
            updateSatisfiedColumnConstraints(columnConstraints, cells, i);
        }

        for(int i=0; i < cells[0].length; i++)
        {
            updateSatisfiedRowConstraints(rowConstraints, cells, i);
        }
    }


    public final void updateSatisfiedColumnConstraints(NonogramConstraint[] rowConstraints, CellKind[][] cells, int rowIndexToUpdate)
    {
        int arrSize = cells[0].length;
        CellKind[] rowSlice = new CellKind[arrSize];
        for (int i=0; i < arrSize; i++)
        {
            rowSlice[i] = cells[rowIndexToUpdate][i];
        }

        updateSatisfiedConstraints(rowSlice, rowConstraints[rowIndexToUpdate]);
    }

    public final void updateSatisfiedRowConstraints(NonogramConstraint[] columnConstraints, CellKind[][] cells, int columnIndexToUpdate)
    {
        int arrSize = cells.length;
        CellKind[] columSlice = new CellKind[arrSize];
        for (int i=0; i < arrSize; i++)
        {
            columSlice[i] = cells[i][columnIndexToUpdate];
        }

        updateSatisfiedConstraints(columSlice, columnConstraints[columnIndexToUpdate]);
    }

    private void updateSatisfiedConstraints(CellKind[] cells, NonogramConstraint constraint) {
        for (int i= 0; i < constraint.count(); i++)
        {
            constraint.getSlice(i).setSatisfied(false);
        }

        ArrayList<Block> blockArray =  devideIntoBlocks(cells);

        boolean satisfactionChanged = true;
        while (satisfactionChanged)
        {
            satisfactionChanged = false;
            for (Block block : blockArray) {
                int numOfSliceThatSatisfyBlock = 0;
                int indexOfLastSatisfiedSlice = 0 ;
                for (int i= 0; i < constraint.count(); i++) {

                    if(constraint.getSlice(i).isSatisfied())
                    {
                        continue;
                    }

                    if (isBlockSatisfySlice(constraint, i, block)) {
                        numOfSliceThatSatisfyBlock = (blockArray.size() == constraint.count()) ? 1 :numOfSliceThatSatisfyBlock + 1;
                        indexOfLastSatisfiedSlice = i;
                    }
                }

                if(numOfSliceThatSatisfyBlock == 1) {
                    constraint.getSlice(indexOfLastSatisfiedSlice).setSatisfied(true);
                    satisfactionChanged = true;
                }

            }

        }
    }

    private ArrayList<Block> devideIntoBlocks(CellKind[] cells) {
        int cellsSize = cells.length;
        ArrayList<Block> blockArray = new ArrayList<>();
        for (int i = 0; i < cells.length; i++)
        {
            int start, end;
            if(cells[i] == CellKind.Black)
            {
                start = i;
                while(i < cellsSize && cells[i] == CellKind.Black){
                    i++;
                }
                end = i;


                if(start != 0 && cells[start -1] != CellKind.White)
                {
                    continue;
                }


                if(end < cellsSize && cells[end] != CellKind.White)
                {
                    continue;
                }

                end -= 1;

                blockArray.add(new Block(start, end, cellsSize));
            }
        }

        return blockArray;
    }
    private boolean isBlockSatisfySlice(NonogramConstraint constraint, int sliceIndex, Block block) {

        if (block.getBlockSize() != constraint.getSlice(sliceIndex).getSize())
        {
            return false;
        }

        int minimumNumOfCellsOnRight = 0, minimumNumOfCellsOnLeft = 0;

        for (int i = sliceIndex+1; i < constraint.count(); i++)
        {
            minimumNumOfCellsOnRight += constraint.getSlice(i).getSize() + 1;
        }

        for (int i = sliceIndex-1; i >= 0 ; i--)
        {
            minimumNumOfCellsOnLeft += constraint.getSlice(i).getSize() + 1;
        }

        return block.getNumOfCellsOnLeft() >= minimumNumOfCellsOnLeft
                && block.getNumOfCellsOnRight() >= minimumNumOfCellsOnRight;
    }

}
