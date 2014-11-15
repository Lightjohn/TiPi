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

import mitiv.array.impl.FlatLong9D;
import mitiv.array.impl.StriddenLong9D;
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.Traits;
import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.base.indexing.Range;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.LongGenerator;


/**
 * Define class for comprehensive 9-dimensional arrays of long's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Long9D extends Array9D implements LongArray {

    protected Long9D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        super(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8,dim9);
    }

    protected Long9D(int[] dims) {
        super(dims);
    }

    protected Long9D(Shape shape) {
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
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param i5 - The index along the 5th dimension.
     * @param i6 - The index along the 6th dimension.
     * @param i7 - The index along the 7th dimension.
     * @param i8 - The index along the 8th dimension.
     * @param i9 - The index along the 9th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4,i5,i6,i7,i8,i9)}.
     */
    public abstract long get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9);

    /**
     * Set the value at a given position.
     * @param i1    - The index along the 1st dimension.
     * @param i2    - The index along the 2nd dimension.
     * @param i3    - The index along the 3rd dimension.
     * @param i4    - The index along the 4th dimension.
     * @param i5    - The index along the 5th dimension.
     * @param i6    - The index along the 6th dimension.
     * @param i7    - The index along the 7th dimension.
     * @param i8    - The index along the 8th dimension.
     * @param i9    - The index along the 9th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4,i5,i6,i7,i8,i9)}.
     */
    public abstract void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, long value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void fill(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, get(i1,i2,i3,i4,i5,i6,i7,i8,i9) + value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, get(i1,i2,i3,i4,i5,i6,i7,i8,i9) + value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decrement(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, get(i1,i2,i3,i4,i5,i6,i7,i8,i9) - value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, get(i1,i2,i3,i4,i5,i6,i7,i8,i9) - value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scale(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, get(i1,i2,i3,i4,i5,i6,i7,i8,i9) * value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, get(i1,i2,i3,i4,i5,i6,i7,i8,i9) * value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void map(LongFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, function.apply(get(i1,i2,i3,i4,i5,i6,i7,i8,i9)));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, function.apply(get(i1,i2,i3,i4,i5,i6,i7,i8,i9)));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fill(LongGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, generator.nextLong());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, generator.nextLong());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scan(LongScanner scanner)  {
        boolean initialized = false;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                if (initialized) {
                                                    scanner.update(get(i1,i2,i3,i4,i5,i6,i7,i8,i9));
                                                } else {
                                                    scanner.initialize(get(i1,i2,i3,i4,i5,i6,i7,i8,i9));
                                                    initialized = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                if (initialized) {
                                                    scanner.update(get(i1,i2,i3,i4,i5,i6,i7,i8,i9));
                                                } else {
                                                    scanner.initialize(get(i1,i2,i3,i4,i5,i6,i7,i8,i9));
                                                    initialized = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* Note that the following default implementation of the "flatten" method
     * is always returning a copy of the contents whatever the value of the
     * "forceCopy" argument.
     * @see devel.eric.array.base.LongArray#flatten(boolean)
     */
    @Override
    public long[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        long[] out = new long[number];
        int i = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            out[++i] = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public long[] flatten() {
        return flatten(false);
    }

    @Override
    public long min() {
        long minValue = get(0,0,0,0,0,0,0,0,0);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                if (skip) {
                                                    skip = false;
                                                } else {
                                                    long value = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                                    if (value < minValue) {
                                                        minValue = value;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                if (skip) {
                                                    skip = false;
                                                } else {
                                                    long value = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                                    if (value < minValue) {
                                                        minValue = value;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return minValue;
    }

    @Override
    public long max() {
        long maxValue = get(0,0,0,0,0,0,0,0,0);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                if (skip) {
                                                    skip = false;
                                                } else {
                                                    long value = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                                    if (value > maxValue) {
                                                        maxValue = value;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                if (skip) {
                                                    skip = false;
                                                } else {
                                                    long value = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                                    if (value > maxValue) {
                                                        maxValue = value;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return maxValue;
    }

    @Override
    public long[] getMinAndMax() {
        long[] result = new long[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(long[] mm) {
        long minValue = get(0,0,0,0,0,0,0,0,0);
        long maxValue = minValue;
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                if (skip) {
                                                    skip = false;
                                                } else {
                                                    long value = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
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
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                if (skip) {
                                                    skip = false;
                                                } else {
                                                    long value = get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
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
                                }
                            }
                        }
                    }
                }
            }
        }
        mm[0] = minValue;
        mm[1] = maxValue;
    }

    @Override
    public long sum() {
        long totalValue = 0;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                totalValue += get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                totalValue += get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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
     * Convert instance into a Byte9D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Byte9D whose values has been converted into byte's
     *         from those of {@code this}.
     */
    @Override
    public Byte9D toByte() {
        byte[] out = new byte[number];
        int i = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            out[++i] = (byte)get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Byte9D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }
    /**
     * Convert instance into a Short9D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Short9D whose values has been converted into short's
     *         from those of {@code this}.
     */
    @Override
    public Short9D toShort() {
        short[] out = new short[number];
        int i = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            out[++i] = (short)get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Short9D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }
    /**
     * Convert instance into an Int9D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return An Int9D whose values has been converted into int's
     *         from those of {@code this}.
     */
    @Override
    public Int9D toInt() {
        int[] out = new int[number];
        int i = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            out[++i] = (int)get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Int9D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }
    /**
     * Convert instance into a Long9D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Long9D whose values has been converted into long's
     *         from those of {@code this}.
     */
    @Override
    public Long9D toLong() {
        return this;
    }
    /**
     * Convert instance into a Float9D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Float9D whose values has been converted into float's
     *         from those of {@code this}.
     */
    @Override
    public Float9D toFloat() {
        float[] out = new float[number];
        int i = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            out[++i] = (float)get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Float9D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }
    /**
     * Convert instance into a Double9D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Double9D whose values has been converted into double's
     *         from those of {@code this}.
     */
    @Override
    public Double9D toDouble() {
        double[] out = new double[number];
        int i = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            out[++i] = (double)get(i1,i2,i3,i4,i5,i6,i7,i8,i9);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Double9D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    @Override
    public Long9D copy() {
        return new FlatLong9D(flatten(true), shape);
    }

    @Override
    public void assign(ShapedArray arr) {
        if (! getShape().equals(arr.getShape())) {
            throw new NonConformableArrayException("Source and destination must have the same shape.");
        }
        Long9D src;
        if (arr.getType() == Traits.LONG) {
            src = (Long9D)arr;
        } else {
            src = (Long9D)arr.toLong();
        }
        // FIXME: do assignation and conversion at the same time
        if (getOrder() == ROW_MAJOR && src.getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            for (int i9 = 0; i9 < dim9; ++i9) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, src.get(i1,i2,i3,i4,i5,i6,i7,i8,i9));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, src.get(i1,i2,i3,i4,i5,i6,i7,i8,i9));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, (long)src.get(++i));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (vec.getType() == Traits.FLOAT) {
            FloatShapedVector src = (FloatShapedVector)vec;
            for (int i9 = 0; i9 < dim9; ++i9) {
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                set(i1,i2,i3,i4,i5,i6,i7,i8,i9, (long)src.get(++i));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalTypeException();
        }
    }


    /*=======================================================================*/
    /* ARRAY FACTORIES */

    @Override
    public Long9D create() {
        return new FlatLong9D(getShape());
    }

    /**
     * Create a 9D array of long's with given dimensions.
     * <p>
     * This method creates a 9D array of long's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A new 9D array of long's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Long9D create(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return new FlatLong9D(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8,dim9);
    }

    /**
     * Create a 9D array of long's with given shape.
     * <p>
     * This method creates a 9D array of long's with zero offset, contiguous
     * elements and column-major order.
     * @param dims - The list of dimensions of the 9D array (all dimensions
     *               must at least 1).  This argument is not referenced by
     *               the returned object and its contents can be modified
     *               after calling this method.
     * @return A new 9D array of long's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Long9D create(int[] dims) {
        return new FlatLong9D(dims);
    }

    /**
     * Create a 9D array of long's with given shape.
     * <p>
     * This method creates a 9D array of long's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The shape of the 9D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 9D array of long's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Long9D create(Shape shape) {
        return new FlatLong9D(shape);
    }

    /**
     * Wrap an existing array in a 9D array of long's with given dimensions.
     * <p>
     * The returned 9D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8,i9) = data[i1 + dim1*(i2 + dim2*(i3 + dim3*(i4 + dim4*(i5 + dim5*(i6 + dim6*(i7 + dim7*(i8 + dim8*i9)))))))]</pre>
     * with {@code arr} the returned 9D array.
     * @param data - The data to wrap in the 9D array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A 9D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Long9D wrap(long[] data, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return new FlatLong9D(data, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8,dim9);
    }

    /**
     * Wrap an existing array in a 9D array of long's with given shape.
     * <p>
     * The returned 9D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8,i9) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*(i5 + shape[4]*(i6 + shape[5]*(i7 + shape[6]*(i8 + shape[7]*i9)))))))]</pre>
     * with {@code arr} the returned 9D array.
     * @param data - The data to wrap in the 9D array.
     * @param dims - The list of dimensions of the 9D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 9D array of long's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Long9D wrap(long[] data, int[] dims) {
        return new FlatLong9D(data, dims);
    }

    /**
     * Wrap an existing array in a 9D array of long's with given shape.
     * <p>
     * The returned 9D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8,i9) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*(i5 + shape[4]*(i6 + shape[5]*(i7 + shape[6]*(i8 + shape[7]*i9)))))))]</pre>
     * with {@code arr} the returned 9D array.
     * @param data       - The data to wrap in the 9D array.
     * @param shape      - The shape of the 9D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 9D array of long's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Long9D wrap(long[] data, Shape shape) {
        return new FlatLong9D(data, shape);
    }

    /**
     * Wrap an existing array in a 9D array of long's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 9D array of dimensions {{@code dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8,dim9}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8,i9) = data[offset + stride1*i1 + stride2*i2 + stride3*i3 + stride4*i4 + stride5*i5 + stride6*i6 + stride7*i7 + stride8*i8 + stride9*i9]</pre>
     * with {@code arr} the returned 9D array.
     * @param data    - The array to wrap in the 9D array.
     * @param offset  - The offset in {@code data} of element (0,0,0,0,0,0,0,0,0) of
     *                  the 9D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @param stride6 - The stride along the 6th dimension.
     * @param stride7 - The stride along the 7th dimension.
     * @param stride8 - The stride along the 8th dimension.
     * @param stride9 - The stride along the 9th dimension.
     * @param dim1    - The 1st dimension of the 9D array.
     * @param dim2    - The 2nd dimension of the 9D array.
     * @param dim3    - The 3rd dimension of the 9D array.
     * @param dim4    - The 4th dimension of the 9D array.
     * @param dim5    - The 5th dimension of the 9D array.
     * @param dim6    - The 6th dimension of the 9D array.
     * @param dim7    - The 7th dimension of the 9D array.
     * @param dim8    - The 8th dimension of the 9D array.
     * @param dim9    - The 9th dimension of the 9D array.
     * @return A 9D array sharing the elements of <b>data</b>.
     */
    public static Long9D wrap(long[] data,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6, int stride7, int stride8, int stride9, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return new StriddenLong9D(data, offset, stride1,stride2,stride3,stride4,stride5,stride6,stride7,stride8,stride9, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8,dim9);
    }

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.  The same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     * @return A Long8D view on the given slice of the array.
     */
    public abstract Long8D slice(int idx);

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
     * @return A Long8D view on the given slice of the array.
     */
    public abstract Long8D slice(int idx, int dim);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param rng1 - The range of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param rng2 - The range of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     * @param rng3 - The range of indices to select along 3rd dimension
     *               (or {@code null} to select all.
     * @param rng4 - The range of indices to select along 4th dimension
     *               (or {@code null} to select all.
     * @param rng5 - The range of indices to select along 5th dimension
     *               (or {@code null} to select all.
     * @param rng6 - The range of indices to select along 6th dimension
     *               (or {@code null} to select all.
     * @param rng7 - The range of indices to select along 7th dimension
     *               (or {@code null} to select all.
     * @param rng8 - The range of indices to select along 8th dimension
     *               (or {@code null} to select all.
     * @param rng9 - The range of indices to select along 9th dimension
     *               (or {@code null} to select all.
     *
     * @return A Long9D view for the given ranges of the array.
     */
    public abstract Long9D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8, Range rng9);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param idx1 - The list of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param idx2 - The list of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     * @param idx3 - The list of indices to select along 3rd dimension
     *               (or {@code null} to select all.
     * @param idx4 - The list of indices to select along 4th dimension
     *               (or {@code null} to select all.
     * @param idx5 - The list of indices to select along 5th dimension
     *               (or {@code null} to select all.
     * @param idx6 - The list of indices to select along 6th dimension
     *               (or {@code null} to select all.
     * @param idx7 - The list of indices to select along 7th dimension
     *               (or {@code null} to select all.
     * @param idx8 - The list of indices to select along 8th dimension
     *               (or {@code null} to select all.
     * @param idx9 - The list of indices to select along 9th dimension
     *               (or {@code null} to select all.
     *
     * @return A Long9D view for the given index selections of the
     *         array.
     */
    public abstract Long9D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6, int[] idx7, int[] idx8, int[] idx9);

    /**
     * Get a view of the array as a 1D array.
     *
     * @return A 1D view of the array.
     */
    @Override
    public abstract Long1D as1D();

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