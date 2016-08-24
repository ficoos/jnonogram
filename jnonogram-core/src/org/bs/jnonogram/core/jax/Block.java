package org.bs.jnonogram.core.jax;

/**
 * Created by barsasson on 23/08/2016.
 */
public class Block {
    private final int _totalSize;
    private final int _startIndex;
    private final int _endIndex;

    public int getStartIndex() {
        return _startIndex;
    }
    public int getEndIndex() {
        return _endIndex;
    }
    public int getNumOfCellsOnLeft() {
        return _startIndex;
    }
    public int getNumOfCellsOnRight() {
        return _totalSize - (_endIndex +1);
    }
    public int getBlockSize() {
        return _endIndex - _startIndex + 1;
    }


    public Block(int startIndex, int endIndex, int totalSize) {
        this._startIndex = startIndex;
        this._endIndex = endIndex;
        this._totalSize = totalSize;
    }
}
