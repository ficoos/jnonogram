package org.bs.jnonogram.core;

import java.util.ArrayList;
import java.util.Iterator;

public final class NonogramConstraint implements Iterable<NonogramConstraint.Slice> {
    public class Slice {
        private final int _size;
        private boolean _isSatisfied;
        public Slice(int size) {
            _size = size;
        }

        public int getSize() {
            return _size;
        }

        public boolean isSatisfied() {
            return _isSatisfied;
        }

        private void setSatisfied(boolean value) {
            _isSatisfied = value;

        }

    }

    @Override
    public Iterator<Slice> iterator() {
        return _slices.iterator();
    }

    public int count() {
        return _slices.size();
    }

    public Slice getSlice(int index) {
        return _slices.get(index);
    }

    private final ArrayList<Slice> _slices;

    public NonogramConstraint(int[] sliceSizes) {
        _slices = new ArrayList<>();
        for (int sliceSize: sliceSizes) {
            _slices.add(new Slice(sliceSize));
        }
    }

    public NonogramConstraint(Iterable<Integer> sliceSizes) {
        _slices = new ArrayList<>();
        for (int sliceSize: sliceSizes) {
            _slices.add(new Slice(sliceSize));
        }
    }
}
