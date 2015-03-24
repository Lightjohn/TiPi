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

package mitiv.array;

import mitiv.array.impl.FlatFloat2D;
import mitiv.array.impl.StriddenFloat2D;
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.Traits;
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.base.indexing.Range;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.FloatGenerator;


/**
 * Define class for comprehensive 2-dimensional arrays of float's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Float2D extends Array2D implements FloatArray {

    protected Float2D(int dim1, int dim2) {
        super(dim1,dim2);
    }

    protected Float2D(int[] dims) {
        super(dims);
    }

    protected Float2D(Shape shape) {
        super(shape);
    }

    @Override
    public final int getType() {
        return type;
    }

    /**
     * Query the value stored at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @return The value stored at position {@code (i1,i2)}.
     */
    public abstract float get(int i1, int i2);

    /**
     * Set the value at a given position.
     * @param i1    - The index along the 1st dimension.
     * @param i2    - The index along the 2nd dimension.
     * @param value - The value to store at position {@code (i1,i2)}.
     */
    public abstract void set(int i1, int i2, float value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void fill(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, value);
                }
            }
        }
    }

    @Override
    public void increment(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, get(i1,i2) + value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, get(i1,i2) + value);
                }
            }
        }
    }

    @Override
    public void decrement(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, get(i1,i2) - value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, get(i1,i2) - value);
                }
            }
        }
    }

    @Override
    public void scale(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, get(i1,i2) * value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, get(i1,i2) * value);
                }
            }
        }
    }

    @Override
    public void map(FloatFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, function.apply(get(i1,i2)));
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, function.apply(get(i1,i2)));
                }
            }
        }
    }

    @Override
    public void fill(FloatGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, generator.nextFloat());
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, generator.nextFloat());
                }
            }
        }
    }

    @Override
    public void scan(FloatScanner scanner)  {
        boolean initialized = false;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    if (initialized) {
                        scanner.update(get(i1,i2));
                    } else {
                        scanner.initialize(get(i1,i2));
                        initialized = true;
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (initialized) {
                        scanner.update(get(i1,i2));
                    } else {
                        scanner.initialize(get(i1,i2));
                        initialized = true;
                    }
                }
            }
        }
    }

    /* Note that the following default implementation of the "flatten" method
     * is always returning a copy of the contents whatever the value of the
     * "forceCopy" argument.
     * @see devel.eric.array.base.FloatArray#flatten(boolean)
     */
    @Override
    public float[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        float[] out = new float[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = get(i1,i2);
            }
        }
        return out;
    }

    @Override
    public float[] flatten() {
        return flatten(false);
    }

    @Override
    public float min() {
        float minValue = get(0,0);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    if (skip) {
                        skip = false;
                    } else {
                        float value = get(i1,i2);
                        if (value < minValue) {
                            minValue = value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (skip) {
                        skip = false;
                    } else {
                        float value = get(i1,i2);
                        if (value < minValue) {
                            minValue = value;
                        }
                    }
                }
            }
        }
        return minValue;
    }

    @Override
    public float max() {
        float maxValue = get(0,0);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    if (skip) {
                        skip = false;
                    } else {
                        float value = get(i1,i2);
                        if (value > maxValue) {
                            maxValue = value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (skip) {
                        skip = false;
                    } else {
                        float value = get(i1,i2);
                        if (value > maxValue) {
                            maxValue = value;
                        }
                    }
                }
            }
        }
        return maxValue;
    }

    @Override
    public float[] getMinAndMax() {
        float[] result = new float[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(float[] mm) {
        float minValue = get(0,0);
        float maxValue = minValue;
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    if (skip) {
                        skip = false;
                    } else {
                        float value = get(i1,i2);
                        if (value < minValue) {
                            minValue = value;
                        }
                        if (value > maxValue) {
                            maxValue = value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (skip) {
                        skip = false;
                    } else {
                        float value = get(i1,i2);
                        if (value < minValue) {
                            minValue = value;
                        }
                        if (value > maxValue) {
                            maxValue = value;
                        }
                    }
                }
            }
        }
        mm[0] = minValue;
        mm[1] = maxValue;
    }

    @Override
    public float sum() {
        float totalValue = 0;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    totalValue += get(i1,i2);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    totalValue += get(i1,i2);
                }
            }
        }
        return totalValue;
    }

    @Override
    public double average() {
        return (double)sum()/(double)number;
    }

    /**
     * Convert instance into a Byte2D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Byte2D whose values has been converted into byte's
     *         from those of {@code this}.
     */
    @Override
    public Byte2D toByte() {
        byte[] out = new byte[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = (byte)get(i1,i2);
            }
        }
        return Byte2D.wrap(out, dim1, dim2);
    }
    /**
     * Convert instance into a Short2D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Short2D whose values has been converted into short's
     *         from those of {@code this}.
     */
    @Override
    public Short2D toShort() {
        short[] out = new short[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = (short)get(i1,i2);
            }
        }
        return Short2D.wrap(out, dim1, dim2);
    }
    /**
     * Convert instance into an Int2D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return An Int2D whose values has been converted into int's
     *         from those of {@code this}.
     */
    @Override
    public Int2D toInt() {
        int[] out = new int[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = (int)get(i1,i2);
            }
        }
        return Int2D.wrap(out, dim1, dim2);
    }
    /**
     * Convert instance into a Long2D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Long2D whose values has been converted into long's
     *         from those of {@code this}.
     */
    @Override
    public Long2D toLong() {
        long[] out = new long[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = (long)get(i1,i2);
            }
        }
        return Long2D.wrap(out, dim1, dim2);
    }
    /**
     * Convert instance into a Float2D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Float2D whose values has been converted into float's
     *         from those of {@code this}.
     */
    @Override
    public Float2D toFloat() {
        return this;
    }
    /**
     * Convert instance into a Double2D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Double2D whose values has been converted into double's
     *         from those of {@code this}.
     */
    @Override
    public Double2D toDouble() {
        double[] out = new double[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = (double)get(i1,i2);
            }
        }
        return Double2D.wrap(out, dim1, dim2);
    }

    @Override
    public Float2D copy() {
        return new FlatFloat2D(flatten(true), shape);
    }

    @Override
    public void assign(ShapedArray arr) {
        if (! getShape().equals(arr.getShape())) {
            throw new NonConformableArrayException("Source and destination must have the same shape.");
        }
        Float2D src;
        if (arr.getType() == Traits.FLOAT) {
            src = (Float2D)arr;
        } else {
            src = (Float2D)arr.toFloat();
        }
        // FIXME: do assignation and conversion at the same time
        if (getOrder() == ROW_MAJOR && src.getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, src.get(i1,i2));
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, src.get(i1,i2));
                }
            }
        }
    }

    @Override
    public void assign(ShapedVector vec) {
        if (! getShape().equals(vec.getShape())) {
            throw new NonConformableArrayException("Source and destination must have the same shape.");
        }
        // FIXME: much too slow and may be skipped if data are identical (and array is flat)
        int i = -1;
        if (vec.getType() == Traits.DOUBLE) {
            DoubleShapedVector src = (DoubleShapedVector)vec;
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, (float)src.get(++i));
                }
            }
        } else if (vec.getType() == Traits.FLOAT) {
            FloatShapedVector src = (FloatShapedVector)vec;
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, (float)src.get(++i));
                }
            }
        } else {
            throw new IllegalTypeException();
        }
    }


    /*=======================================================================*/
    /* ARRAY FACTORIES */

    @Override
    public Float2D create() {
        return new FlatFloat2D(getShape());
    }

    /**
     * Create a 2D array of float's with given dimensions.
     * <p>
     * This method creates a 2D array of float's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A new 2D array of float's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float2D create(int dim1, int dim2) {
        return new FlatFloat2D(dim1,dim2);
    }

    /**
     * Create a 2D array of float's with given shape.
     * <p>
     * This method creates a 2D array of float's with zero offset, contiguous
     * elements and column-major order.
     * @param dims - The list of dimensions of the 2D array (all dimensions
     *               must at least 1).  This argument is not referenced by
     *               the returned object and its contents can be modified
     *               after calling this method.
     * @return A new 2D array of float's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float2D create(int[] dims) {
        return new FlatFloat2D(dims);
    }

    /**
     * Create a 2D array of float's with given shape.
     * <p>
     * This method creates a 2D array of float's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The shape of the 2D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 2D array of float's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float2D create(Shape shape) {
        return new FlatFloat2D(shape);
    }

    /**
     * Wrap an existing array in a 2D array of float's with given dimensions.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + dim1*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data - The data to wrap in the 2D array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A 2D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float2D wrap(float[] data, int dim1, int dim2) {
        return new FlatFloat2D(data, dim1,dim2);
    }

    /**
     * Wrap an existing array in a 2D array of float's with given shape.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + shape[0]*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data - The data to wrap in the 2D array.
     * @param dims - The list of dimensions of the 2D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 2D array of float's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float2D wrap(float[] data, int[] dims) {
        return new FlatFloat2D(data, dims);
    }

    /**
     * Wrap an existing array in a 2D array of float's with given shape.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + shape[0]*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data       - The data to wrap in the 2D array.
     * @param shape      - The shape of the 2D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 2D array of float's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float2D wrap(float[] data, Shape shape) {
        return new FlatFloat2D(data, shape);
    }

    /**
     * Wrap an existing array in a 2D array of float's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 2D array of dimensions {{@code dim1,dim2}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2) = data[offset + stride1*i1 + stride2*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data    - The array to wrap in the 2D array.
     * @param offset  - The offset in {@code data} of element (0,0) of
     *                  the 2D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param dim1    - The 1st dimension of the 2D array.
     * @param dim2    - The 2nd dimension of the 2D array.
     * @return A 2D array sharing the elements of <b>data</b>.
     */
    public static Float2D wrap(float[] data,
            int offset, int stride1, int stride2, int dim1, int dim2) {
        return new StriddenFloat2D(data, offset, stride1,stride2, dim1,dim2);
    }

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.  The same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     * @return A Float1D view on the given slice of the array.
     */
    public abstract Float1D slice(int idx);

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.
     * @param dim - The dimension to slice.  For these two arguments,
     *              the same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     *
     * @return A Float1D view on the given slice of the array.
     */
    public abstract Float1D slice(int idx, int dim);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param rng1 - The range of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param rng2 - The range of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     *
     * @return A Float2D view for the given ranges of the array.
     */
    public abstract Float2D view(Range rng1, Range rng2);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param idx1 - The list of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param idx2 - The list of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     *
     * @return A Float2D view for the given index selections of the
     *         array.
     */
    public abstract Float2D view(int[] idx1, int[] idx2);

    /**
     * Get a view of the array as a 1D array.
     *
     * @return A 1D view of the array.
     */
    @Override
    public abstract Float1D as1D();

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