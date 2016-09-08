package org.bs.jnonogram.core;

import java.util.ArrayList;
import java.util.Iterator;

public final class NonogramConstraint implements Iterable<NonogramConstraint.Slice> {
    public class Slice {
        private final int _size;
        private boolean _isSatisfied;
        public Slice(int size) {
            _size = size;
            _isSatisfied = false;
        }

        public int getSize() {
            return _size;
        }

        public boolean isSatisfied() {
            return _isSatisfied;
        }

        public void setSatisfied(boolean value) {
            _isSatisfied = value;

        }

        public final Slice clone()
        {
            Slice clone = new Slice(this._size);
            clone.setSatisfied(this.isSatisfied());

            return clone;
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

    private ArrayList<Slice> _slices;

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

    public NonogramConstraint()
    {
        _slices = null;
    }

    public final NonogramConstraint clone()
    {
        NonogramConstraint clone = new NonogramConstraint();
        ArrayList<Slice> cloneSlices = new ArrayList<>();
        for (Slice slice: _slices) {
            cloneSlices.add(slice.clone());
        }
        clone._slices = cloneSlices;

        return clone;
    }

    public static final NonogramConstraint[] clone(NonogramConstraint[] original)
    {
        NonogramConstraint[] clone = new NonogramConstraint[original.length];

        for (int i = 0; i < original.length ; i++) {
            clone[i] = original[i].clone();
        }

        return clone;
    }


}
