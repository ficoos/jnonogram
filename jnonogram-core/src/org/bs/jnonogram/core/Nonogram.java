package org.bs.jnonogram.core;

import javafx.beans.property.MapProperty;
import javafx.beans.property.MapPropertyBase;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import org.bs.jnonogram.core.jax.Block;

import java.util.ArrayList;
import java.util.HashMap;

public final class Nonogram implements ReadOnlyNonogram {
    private final MapPropertyBase<CellPosition, CellKind> _cellsProperty;
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

    public final MapProperty<CellPosition, CellKind> cellsProperty() {
        return _cellsProperty;
    }

    public Nonogram(
            NonogramConstraint[] rowConstraints,
            NonogramConstraint[] columnConstraints,
            Iterable<CellPosition> solution) {
        _columnCount = columnConstraints.length;
        _rowCount = rowConstraints.length;
        _cellsProperty = new SimpleMapProperty<>(this, "Cells", FXCollections.observableMap(new HashMap<>()));
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
        for (int column = 0; column < _columnCount; column++) {
            for (int row = 0; row < _rowCount; row++) {
                CellPosition position = new CellPosition(column, row);
                if (!_cellsProperty.containsKey(position)) {
                    _cellsProperty.put(position, CellKind.Unknown);
                }
                else {
                    _cellsProperty.replace(position, CellKind.Unknown);
                }
            }
        }
    }

    @Override
    public final int getScore() {
        int correct = 0;
        for (int column = 0; column < _columnCount; column++) {
            for (int row = 0; row < _rowCount; row++) {
                CellPosition position = new CellPosition(column, row);
                if (_cellsProperty.get(position).equals(_solution[column][row])) {
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

        return getCellAt(new CellPosition(column, row));
    }

    @Override
    public final CellKind getCellAt(CellPosition position)
    {
        return _cellsProperty.get(position);
    }

    public final void setCellAt(int x, int y, CellKind kind) {
        setCellAt(new CellPosition(x, y), kind);
    }

    public final void setCellAt(CellPosition position, CellKind kind) {
        _cellsProperty.replace(position, kind);
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
                NonogramConstraint.clone(this._rowConstraints),
                NonogramConstraint.clone(this._columnConstraints),
                solution);
        for (int x = 0; x < this._columnCount; x++) {
            for (int y = 0; y < this._rowCount; y++) {
                nonogram.setCellAt(x, y, this.getCellAt(x, y));
            }
        }

        return  nonogram;
    }

    public final Task<Boolean> updateSatisfiedConstraints()
    {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                updateTitle("Loading..");
                updateMessage("Collecting state");
                updateProgress(0, 4);
                Thread.sleep(100);

                updateProgress(1, 4);
                Thread.sleep(100);
                CellKind[][] cells = new CellKind[_columnCount][_rowCount];
                for (int row = 0; row < _rowCount; row++) {
                    for (int column = 0; column < _columnCount; column++) {
                        cells[column][row] = getCellAt(column, row);
                    }
                }
                updateProgress(2, 4);
                Thread.sleep(100);

                updateMessage("Updating satisfied constraints");
                updateProgress(3, 4);
                Thread.sleep(200);

                updateSatisfiedConstraints(_rowConstraints, _columnConstraints, cells);

                updateMessage("Done");
                updateProgress(4, 4);
                Thread.sleep(100);
                return true;
            }
        };
    }

    public static void updateSatisfiedConstraints(NonogramConstraint[] rowConstraints, NonogramConstraint[] columnConstraints, CellKind[][] cells)
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


    public static void updateSatisfiedColumnConstraints(NonogramConstraint[] rowConstraints, CellKind[][] cells, int rowIndexToUpdate)
    {
        int arrSize = cells[0].length;
        CellKind[] rowSlice = new CellKind[arrSize];
        System.arraycopy(cells[rowIndexToUpdate], 0, rowSlice, 0, arrSize);

        updateSatisfiedConstraints(rowSlice, rowConstraints[rowIndexToUpdate]);
    }

    public static void updateSatisfiedRowConstraints(NonogramConstraint[] columnConstraints, CellKind[][] cells, int columnIndexToUpdate)
    {
        int arrSize = cells.length;
        CellKind[] columSlice = new CellKind[arrSize];
        for (int i=0; i < arrSize; i++)
        {
            columSlice[i] = cells[i][columnIndexToUpdate];
        }

        updateSatisfiedConstraints(columSlice, columnConstraints[columnIndexToUpdate]);
    }

    private static void updateSatisfiedConstraints(CellKind[] cells, NonogramConstraint constraint) {
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
                        if(blockArray.size() > constraint.count())
                        {
                            numOfSliceThatSatisfyBlock = 0;
                        }

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

    private static ArrayList<Block> devideIntoBlocks(CellKind[] cells) {
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

    private static boolean isBlockSatisfySlice(NonogramConstraint constraint, int sliceIndex, Block block) {
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
