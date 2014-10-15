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

import mitiv.array.Byte1D;
import mitiv.array.ByteScalar;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 1-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public class FlatByte1D extends Byte1D {
    static final int order = COLUMN_MAJOR;
    final byte[] data;

    public FlatByte1D(int dim1) {
        super(dim1);
        data = new byte[number];
    }

    public FlatByte1D(int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
        data = new byte[number];
    }

    public FlatByte1D(byte[] arr, int dim1) {
        super(dim1);
        checkSize(arr);
        data = arr;
    }

    public FlatByte1D(byte[] arr, int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
        checkSize(arr);
        data = arr;
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

    private void checkSize(byte[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1) {
        return i1;
    }

    @Override
    public final byte get(int i1) {
        return data[i1];
    }

    @Override
    public final void set(int i1, byte value) {
        data[i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(byte value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(ByteGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextByte();
        }
    }

    @Override
    public void increment(byte value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(byte value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(byte value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(ByteFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] *= function.apply(data[j]);
        }
    }

    @Override
    public void scan(ByteScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public byte[] flatten(boolean forceCopy) {
        if (forceCopy) {
            byte[] out = new byte[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        } else {
            return data;
        }
    }
    @Override
    public ByteScalar slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteScalar slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte1D view(Range rng1) {
        // TODO Auto-generated method stub
        return null;
    }

   @Override
    public Byte1D view(int[] idx1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte1D as1D() {
        return this;
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
