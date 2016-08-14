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

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CellPosition))
        {
            return  false;
        }

        CellPosition otherPosition = (CellPosition) other;

        return otherPosition.getRow() == getRow() && otherPosition.getColumn() == getColumn();
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + row;
        hashCode = 31 * hashCode + column;
        return hashCode;
    }
}
