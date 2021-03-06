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

package mitiv.linalg.shaped;

import mitiv.array.FloatArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.Vector;

/**
 * Class vector spaces which own instances of the FloatVector class.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 * 
 */
public class FloatShapedVectorSpace extends ShapedVectorSpace {

    public FloatShapedVectorSpace(Shape shape) {
        super(FLOAT, shape);
    }

    public FloatShapedVectorSpace(int[] dims) {
        super(FLOAT, dims);
    }

    public FloatShapedVectorSpace(int dim1) {
        super(FLOAT, dim1);
    }

    public FloatShapedVectorSpace(int dim1, int dim2) {
        super(FLOAT, dim1, dim2);
    }

    public FloatShapedVectorSpace(int dim1, int dim2, int dim3) {
        super(FLOAT, dim1, dim2, dim3);
    }

    public FloatShapedVectorSpace(int dim1, int dim2, int dim3, int dim4) {
        super(FLOAT, dim1, dim2, dim3, dim4);
    }

    @Override
    public FloatShapedVector create() {
        return new FloatShapedVector(this);
    }

    @Override
    public FloatShapedVector create(double value) {
        FloatShapedVector v = new FloatShapedVector(this);
        ArrayOps.fill(number, v.getData(), value);
        return v;
    }

    @Override
    public FloatShapedVector create(ShapedArray arr) {
        return create(arr, false);
    }

    @Override
    public FloatShapedVector create(ShapedArray arr, boolean forceCopy) {
        /* Verify shape, then convert to correct data type and avoid forcing a
         * copy if conversion yields a different array. */
        checkShape(arr);
        FloatArray tmp = arr.toFloat();
        return new FloatShapedVector(this, tmp.flatten(forceCopy && tmp == arr));
    }

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p>
     * This is a variant of {@link #create(ShapedArray)} for an array of
     * known data type.
     * </p>
     * @param arr - A shaped array with elements of type {@code float}.
     * @return A new FloatShapedVector.
     */
    public FloatShapedVector create(FloatArray arr) {
        return create(arr, false);
    }

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p>
     * This is a variant of {@link #create(ShapedArray, boolean)} for an array of
     * known data type.
     * </p>
     * @param arr       - A shaped array with elements of type {@code float}.
     * @param forceCopy - A flag to force a copy of the contents if true.
     * @return A new FloatShapedVector.
     */
    public FloatShapedVector create(FloatArray arr, boolean forceCopy) {
        checkShape(arr);
        return new FloatShapedVector(this, arr.flatten(forceCopy));
    }

    public FloatShapedVector clone(FloatShapedVector vec) {
        check(vec);
        return _clone(vec);
    }

    protected FloatShapedVector _clone(FloatShapedVector vec) {
        FloatShapedVector cpy = new FloatShapedVector(this);
        _copy(vec, cpy);
        return cpy;
    }

    @Override
    public FloatShapedVector clone(Vector vec) {
        check(vec);
        return _clone(vec);
    }

    @Override
    protected FloatShapedVector _clone(Vector vec) {
        return _clone((FloatShapedVector)vec);
    }

    public FloatShapedVector wrap(float[] x) {
        return new FloatShapedVector(this, x);
    }

    // FIXME:
    public void copy(float[] src, Vector dst) {
        check(dst);
        ((FloatShapedVector)dst).set(src);
    }

    @Override
    protected void _copy(Vector src, Vector dst) {
        ArrayOps.copy(number, ((FloatShapedVector) src).getData(),
                ((FloatShapedVector) dst).getData());
    }

    @Override
    protected void _swap(Vector x, Vector y) {
        _copy(x, y);
    }

    protected void _swap(FloatShapedVector vx, FloatShapedVector vy) {
        float[] x = vx.getData();
        float[] y = vy.getData();
        int n = vx.getNumber();
        for (int j = 0; j < n; ++j) {
            float a = x[j];
            x[j] = y[j];
            y[j] = a;
        }
    }

    @Override
    protected void _fill(Vector x, double alpha) {
        ArrayOps.fill(number, ((FloatShapedVector) x).getData(), alpha);
    }

    @Override
    protected double _dot(final Vector x, final Vector y) {
        return ArrayOps.dot(number, ((FloatShapedVector) x).getData(),
                ((FloatShapedVector) y).getData());
    }

    @Override
    protected double _norm2(Vector x) {
        return ArrayOps.norm2(((FloatShapedVector) x).getData());
    }

    @Override
    protected double _norm1(Vector x) {
        return ArrayOps.norm1(((FloatShapedVector) x).getData());
    }

    @Override
    protected double _normInf(Vector x) {
        return ArrayOps.normInf(((FloatShapedVector) x).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, Vector y) {
        ArrayOps.axpby(number,
                alpha, ((FloatShapedVector) x).getData(),
                beta,  ((FloatShapedVector) y).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, final Vector y, Vector dst) {
        ArrayOps.axpby(number,
                alpha, ((FloatShapedVector) x).getData(),
                beta,  ((FloatShapedVector) y).getData(), ((FloatShapedVector) dst).getData());
    }

    @Override
    protected void _axpbypcz(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z, Vector dst) {
        ArrayOps.axpbypcz(number,
                alpha, ((FloatShapedVector) x).getData(),
                beta,  ((FloatShapedVector) y).getData(),
                gamma, ((FloatShapedVector) z).getData(), ((FloatShapedVector) dst).getData());
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
