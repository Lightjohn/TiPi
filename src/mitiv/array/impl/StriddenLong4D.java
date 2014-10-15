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
import mitiv.array.Long3D;
import mitiv.array.Long4D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.random.LongGenerator;

/**
 * Stridden implementation of 4-dimensional arrays of long's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenLong4D extends Long4D {
    final int order;
    final long[] data;
    final int offset;
    final int stride1;
    final int stride2;
    final int stride3;
    final int stride4;

    public StriddenLong4D(long[] arr, int offset, int[] stride, int[] shape) {
        super(shape, true);
        if (stride.length != rank) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        stride2 = stride[1];
        stride3 = stride[2];
        stride4 = stride[3];
        this.order = Long4D.checkViewStrides(data.length, dim1, dim2, dim3, dim4, offset, stride1, stride2, stride3, stride4);
    }

    public StriddenLong4D(long[] arr, int offset, int stride1, int dim1, int stride2, int dim2, int stride3, int dim3, int stride4, int dim4) {
        super(dim1, dim2, dim3, dim4);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.stride3 = stride3;
        this.stride4 = stride4;
        this.order = Long4D.checkViewStrides(data.length, dim1, dim2, dim3, dim4, offset, stride1, stride2, stride3, stride4);
    }

    @Override
    public void checkSanity() {
        Long4D.checkViewStrides(data.length, dim1, dim2, dim3, dim4, offset, stride1, stride2, stride3, stride4);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1 && stride2 == dim1 && stride3 == dim2*stride2 && stride4 == dim3*stride3);
    }

    final int index(int i1, int i2, int i3, int i4) {
        return offset + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1;
    }

    @Override
    public final long get(int i1, int i2, int i3, int i4) {
        return data[offset + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, long value) {
        data[offset + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            data[j4] = value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            data[j1] = value;
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
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            data[j4] = generator.nextLong();
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            data[j1] = generator.nextLong();
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
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            data[j4] += value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            data[j1] += value;
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
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            data[j4] -= value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            data[j1] -= value;
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
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            data[j4] *= value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            data[j1] *= value;
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
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            data[j4] = function.apply(data[j4]);
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            data[j1] = function.apply(data[j1]);
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
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            if (initialized) {
                                scanner.update(data[j4]);
                            } else {
                                scanner.initialize(data[j4]);
                                initialized = true;
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = stride4*i4 + offset;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = stride3*i3 + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = stride2*i2 + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = stride1*i1 + j2;
                            if (initialized) {
                                scanner.update(data[j1]);
                            } else {
                                scanner.initialize(data[j1]);
                                initialized = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public long[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        long[] out = new long[number];
        int j = -1;
        for (int i4 = 0; i4 < dim4; ++i4) {
            int j4 = stride4*i4 + offset;
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + j4;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j3;
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        int j1 = stride1*i1 + j2;
                        out[++j] = data[j1];
                    }
                }
            }
        }
        return out;
    }
    @Override
    public Long3D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long3D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long4D view(Range rng1, Range rng2, Range rng3, Range rng4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long4D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long1D as1D() {
        // TODO Auto-generated method stub
        return null;
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
