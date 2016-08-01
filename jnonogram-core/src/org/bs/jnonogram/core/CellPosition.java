package org.bs.jnonogram.core;

public class CellPosition {
    private int column;
    private int row;

    public CellPosition(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public CellPosition clone() {
        return new CellPosition(this.column, this.row);
    }
}
