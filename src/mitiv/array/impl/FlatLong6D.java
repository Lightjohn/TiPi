/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014 the MiTiV project, http://mitiv.univ-lyon1.fr/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mitiv.array.impl;

import mitiv.array.Long1D;
import mitiv.array.Long5D;
import mitiv.array.Long6D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.random.LongGenerator;
import mitiv.array.ArrayUtils;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 6-dimensional arrays of long's.
 *
 * @author Éric Thiébaut.
 */
public class FlatLong6D extends Long6D {
    static final int order = COLUMN_MAJOR;
    final long[] data;
    final int dim1dim2;
    final int dim1dim2dim3;
    final int dim1dim2dim3dim4;
    final int dim1dim2dim3dim4dim5;

    public FlatLong6D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        super(dim1, dim2, dim3, dim4, dim5, dim6);
        data = new long[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatLong6D(int[] dims) {
        super(dims);
        data = new long[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatLong6D(Shape shape) {
        super(shape);
        data = new long[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatLong6D(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        super(dim1, dim2, dim3, dim4, dim5, dim6);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatLong6D(long[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatLong6D(long[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    @Override
    public void checkSanity() {
        if (data == null) {
           throw new NonConformableArrayException("Wrapped array is null.");
        }
        if (data.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    private void checkSize(long[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6) {
        return dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final long get(int i1, int i2, int i3, int i4, int i5, int i6) {
        return data[dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, long value) {
        data[dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(long value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(LongGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextLong();
        }
    }

    @Override
    public void increment(long value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(long value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(long value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(LongFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(LongScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public long[] flatten(boolean forceCopy) {
        if (forceCopy) {
            long[] result = new long[number];
            System.arraycopy(data, 0, result, 0, number);
            return result;
        } else {
            return data;
        }
    }

    @Override
    public Long5D slice(int idx) {
        if (idx == 0) {
            return new FlatLong5D(data, dim1, dim2, dim3, dim4, dim5);
        } else {
            return new StriddenLong5D(data,
                    dim1dim2dim3dim4dim5*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, // strides
                    dim1, dim2, dim3, dim4, dim5); // dimensions
        }
    }

    @Override
    public Long5D slice(int idx, int dim) {
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 6;
        }
        switch (dim) {
        case 0:
            return new StriddenLong5D(data,
                    idx, // offset
                    dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, // strides
                    dim2, dim3, dim4, dim5, dim6); // dimensions
        case 1:
            return new StriddenLong5D(data,
                    dim1*idx, // offset
                    1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, // strides
                    dim1, dim3, dim4, dim5, dim6); // dimensions
        case 2:
            return new StriddenLong5D(data,
                    dim1dim2*idx, // offset
                    1, dim1, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, // strides
                    dim1, dim2, dim4, dim5, dim6); // dimensions
        case 3:
            return new StriddenLong5D(data,
                    dim1dim2dim3*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, // strides
                    dim1, dim2, dim3, dim5, dim6); // dimensions
        case 4:
            return new StriddenLong5D(data,
                    dim1dim2dim3dim4*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4dim5, // strides
                    dim1, dim2, dim3, dim4, dim6); // dimensions
        case 5:
            return new StriddenLong5D(data,
                    dim1dim2dim3dim4dim5*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, // strides
                    dim1, dim2, dim3, dim4, dim5); // dimensions
        }
        throw new IndexOutOfBoundsException("Dimension index out of bounds.");
    }

    @Override
    public Long6D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, dim1dim2);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, dim1dim2dim3);
        CompiledRange cr5 = new CompiledRange(rng5, dim5, 0, dim1dim2dim3dim4);
        CompiledRange cr6 = new CompiledRange(rng6, dim6, 0, dim1dim2dim3dim4dim5);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing() && cr5.doesNothing() && cr6.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0 || cr2.getNumber() == 0 || cr3.getNumber() == 0 || cr4.getNumber() == 0 || cr5.getNumber() == 0 || cr6.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenLong6D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset() + cr5.getOffset() + cr6.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(), cr5.getStride(), cr6.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber(), cr5.getNumber(), cr6.getNumber());
    }

    @Override
    public Long6D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5, int[] sel6) {
        int[] idx1 = ArrayUtils.select(0, 1, dim1, sel1);
        int[] idx2 = ArrayUtils.select(0, dim1, dim2, sel2);
        int[] idx3 = ArrayUtils.select(0, dim1dim2, dim3, sel3);
        int[] idx4 = ArrayUtils.select(0, dim1dim2dim3, dim4, sel4);
        int[] idx5 = ArrayUtils.select(0, dim1dim2dim3dim4, dim5, sel5);
        int[] idx6 = ArrayUtils.select(0, dim1dim2dim3dim4dim5, dim6, sel6);
        return new SelectedLong6D(this.data, idx1, idx2, idx3, idx4, idx5, idx6);
    }

    @Override
    public Long1D as1D() {
        return new FlatLong1D(data, number);
    }

}

/*
 * Local Variables:
 * mode: Java
 * tab-width: 8
 * indent-tabs-mode: nil
 * c-basic-offset: 4
 * fill-column: 78
 * coding: utf-8
 * ispell-local-dictionary: "american"
 * End:
 */
