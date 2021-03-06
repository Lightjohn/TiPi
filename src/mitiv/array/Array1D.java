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
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.indexing.Range;


/**
 * Define abstract class for mono-dimensional arrays of rank 1.
 *
 * @author Éric Thiébaut.
 */
public abstract class Array1D implements ShapedArray {
    protected final Shape shape;
    protected final int number;
    protected final int dim1;

    /*
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */
    protected Array1D(int dim1) {
        shape = Shape.make(dim1);
        if (shape.number() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total number of elements is too large.");
        }
        number = (int)shape.number();
        this.dim1 = dim1;
    }

    protected Array1D(int[] dims) {
        this(Shape.make(dims));
    }

    protected Array1D(Shape shape) {
        if (shape.rank() != 1) {
            throw new IllegalArgumentException("Bad number of dimensions for 1-D array.");
        }
        if (shape.number() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total number of elements is too large.");
        }
        this.number = (int)shape.number();
        this.shape = shape;
        this.dim1 = shape.dimension(0);
    }

    @Override
    public final int getRank() {
        return 1;
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    @Override
    public final int getNumber() {
        return number;
    }

    @Override
    public final int getDimension(int k) {
        return shape.dimension(k);
    }

    @Override
    public abstract Array1D copy();

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.  The same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     * @return A Scalar view on the given slice of the array.
     */
    public abstract Scalar slice(int idx);

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
     * @return A Scalar view on the given slice of the array.
     */
    public abstract Scalar slice(int idx, int dim);

    /**
     * Get a view of the array for given range of indices.
     *
     * @param rng1 - The range of indices to select along 1st dimension
     *               (or {@code null} to select all.
     *
     * @return A Array1D view for the given range of the array.
     */
    public abstract Array1D view(Range rng1);

    /**
     * Get a view of the array for given range of indices.
     *
     * @param idx1 - The list of indices to select along 1st dimension
     *               (or {@code null} to select all.
     *
     * @return A Array1D view for the given index selection of the
     *         array.
     */
    public abstract Array1D view(int[] idx1);

    /**
     * Get a view of the array as a 1D array.
     *
     * @return A 1D view of the array.
     */
    public abstract Array1D as1D();

    /**
     * Check the parameters of a 1D view with strides and get ordering.
     * @param number  - The number of elements in the wrapped array.
     * @param dim1    - The 1st dimension of the 1D view.
     * @param offset  - The offset of element (0) of the 1D view.
     * @param stride1 - The stride along the 1st dimension.
     * @return The ordering: {@link Shaped#COLUMN_MAJOR},
     *         {@link Shaped#ROW_MAJOR}, or {@link Shaped#NONSPECIFIC_ORDER}.
     * @throws IndexOutOfBoundsException
     */
    public static int checkViewStrides(int number, int offset, int stride1, int dim1) {
        int imin, imax, itmp;
        itmp = (dim1 - 1)*stride1;
        if (itmp >= 0) {
            imin = offset;
            imax = offset + itmp;
        } else {
            imin = offset + itmp;
            imax = offset;
        }
        if (imin < 0 || imax >= number) {
            throw new IndexOutOfBoundsException("1D view is not within available space");
        }
        return COLUMN_MAJOR;
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
