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

package mitiv.linalg;

import mitiv.base.Traits;


/**
 * Class vector spaces which own instances of the FloatVector class.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 * 
 */
public class FloatVectorSpace extends VectorSpace {
    public FloatVectorSpace(int number) {
        super(Traits.FLOAT, number);
    }

    @Override
    public FloatVector create() {
        return new FloatVector(this);
    }

    @Override
    public FloatVector create(double value) {
        FloatVector v = new FloatVector(this);
        ArrayOps.fill(number, v.getData(), value);
        return v;
    }

    @Override
    public FloatVector clone(Vector vec) {
        check(vec);
        return _clone(vec);
    }

    @Override
    protected FloatVector _clone(Vector vec) {
        FloatVector cpy = new FloatVector(this);
        _copy(vec, cpy);
        return cpy;
    }

    public FloatVector wrap(float[] x) {
        return new FloatVector(this, x);
    }

    public void copy(final float[] src, Vector dst) {
        check(dst);
        ((FloatVector)dst).set(src);
    }

    @Override
    protected void _copy(Vector src, Vector dst) {
        ArrayOps.copy(number, ((FloatVector) src).getData(),
                ((FloatVector) dst).getData());
    }

    @Override
    protected void _fill(Vector x, double alpha) {
        ArrayOps.fill(number, ((FloatVector) x).getData(), alpha);
    }

    @Override
    protected double _dot(final Vector x, final Vector y) {
        return ArrayOps.dot(number, ((FloatVector) x).getData(),
                ((FloatVector) y).getData());
    }

    @Override
    protected double _norm2(Vector x) {
        return ArrayOps.norm2(((FloatVector) x).getData());
    }

    @Override
    protected double _norm1(Vector x) {
        return ArrayOps.norm1(((FloatVector) x).getData());
    }

    @Override
    protected double _normInf(Vector x) {
        return ArrayOps.normInf(((FloatVector) x).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, Vector y) {
        ArrayOps.axpby(number,
                alpha, ((FloatVector) x).getData(),
                beta,  ((FloatVector) y).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, final Vector y, Vector dst) {
        ArrayOps.axpby(number,
                alpha, ((FloatVector) x).getData(),
                beta,  ((FloatVector) y).getData(), ((FloatVector) dst).getData());
    }

    @Override
    protected void _axpbypcz(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z, Vector dst) {
        ArrayOps.axpbypcz(number,
                alpha, ((FloatVector) x).getData(),
                beta,  ((FloatVector) y).getData(),
                gamma, ((FloatVector) z).getData(), ((FloatVector) dst).getData());
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
