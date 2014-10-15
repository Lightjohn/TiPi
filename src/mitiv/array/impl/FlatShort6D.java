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

import mitiv.array.Short1D;
import mitiv.array.Short5D;
import mitiv.array.Short6D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.random.ShortGenerator;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 6-dimensional arrays of short's.
 *
 * @author Éric Thiébaut.
 */
public class FlatShort6D extends Short6D {
    static final int order = COLUMN_MAJOR;
    final short[] data;
    final int dim1dim2;
    final int dim1dim2dim3;
    final int dim1dim2dim3dim4;
    final int dim1dim2dim3dim4dim5;

    public FlatShort6D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        super(dim1,dim2,dim3,dim4,dim5,dim6);
        data = new short[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatShort6D(int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
        data = new short[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatShort6D(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        super(dim1,dim2,dim3,dim4,dim5,dim6);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
    }

    public FlatShort6D(short[] arr, int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
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

    private void checkSize(short[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6) {
        return dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final short get(int i1, int i2, int i3, int i4, int i5, int i6) {
        return data[dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, short value) {
        data[dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(short value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(ShortGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextShort();
        }
    }

    @Override
    public void increment(short value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(short value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(short value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(ShortFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] *= function.apply(data[j]);
        }
    }

    @Override
    public void scan(ShortScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public short[] flatten(boolean forceCopy) {
        if (forceCopy) {
            short[] out = new short[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        } else {
            return data;
        }
    }
    @Override
    public Short5D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Short5D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Short6D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6) {
        // TODO Auto-generated method stub
        return null;
    }

   @Override
    public Short6D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Short1D as1D() {
        return new FlatShort1D(data, number);
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
