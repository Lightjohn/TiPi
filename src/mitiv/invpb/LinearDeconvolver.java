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

package mitiv.invpb;

import mitiv.deconv.ConvolutionOperator;
import mitiv.exception.NotImplementedException;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpace;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.FloatVector;
import mitiv.linalg.FloatVectorSpace;
import mitiv.linalg.FloatVectorSpaceWithRank;
import mitiv.linalg.IdentityOperator;
import mitiv.linalg.LinearConjugateGradient;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.RealComplexFFT;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
/**
 * 
 * @author Leger Jonathan
 */
public class LinearDeconvolver {
    private int rank;
    private Vector h;
    private Vector q;
    private Vector w;
    private Vector y;
    private Vector z; // scratch vector in complex space
    private Vector b;
    private LinearOperator H;
    private LinearOperator W;
    private LinearOperator Q;
    private LeftHandSideMatrix A;
    private RealComplexFFT FFT;
    private LinearConjugateGradient cg;
    private double muFactor = 1.0;
    private boolean single;

    /**
     * Float Version.
     * 
     * @param shape
     * @param data
     * @param psf
     * @param wgt
     * @param mu
     */
    public LinearDeconvolver(int shape[], float[] data, float[] psf, float[] wgt, double mu) {
        /* See single precision version for comments. */
        FloatVectorSpaceWithRank space = new FloatVectorSpaceWithRank(shape);
        y = space.wrap(data);
        h = space.wrap(psf);
        w = (wgt == null ? null : space.wrap(wgt));
        FFT = new RealComplexFFT(space);
        single = true;
        setup(shape, mu);
    }

    /**
     * Double version.
     * 
     * @param shape
     * @param data
     * @param psf
     * @param wgt
     * @param mu
     */
    public LinearDeconvolver(int shape[], double[] data, double[] psf, double[] wgt, double mu) {
        /* Check dimensions and create FFT operator. 
         * We assume that all cases (i.e. 1D, 2D, 3D) can be wrapped
         * into a simple vector space.  Note that calling space.wrap() checks
         * whether array size is compatible with vector space.
         */
        DoubleVectorSpaceWithRank space = new DoubleVectorSpaceWithRank(shape);
        y = space.wrap(data);
        h = space.wrap(psf);
        w = (wgt == null ? null : space.wrap(wgt));
        FFT = new RealComplexFFT(space);
        single = false;
        setup(shape, mu);
    }

    private void setup(int shape[], double mu) {
        rank = shape.length;
        if (rank > 3) {
            throw new IllegalArgumentException("Too many dimensions.");
        }

        /* Allocate workspaces. */
        VectorSpace space = FFT.getInputSpace();
        z = FFT.getOutputSpace().create();
        q = space.create(); // not outputSpace!

        if (single) {
            generateIsotropicQ(shape, ((FloatVector)q).getData());
            Q = new LinearOperator(space) {           
                @Override
                protected void privApply(Vector src, Vector dst, int job) {
                    if (job == DIRECT || job == ADJOINT) {
                        FFT.apply(src, z, DIRECT);
                        multiplyByQ(((FloatVector)q).getData(), ((FloatVector)z).getData());
                        FFT.apply(z, dst, INVERSE);
                    } else {
                        throw new NotImplementedException();
                    }
                }
            };            
        } else {
            generateIsotropicQ(shape, ((DoubleVector)q).getData());
            Q = new LinearOperator(space) {           
                @Override
                protected void privApply(Vector src, Vector dst, int job) {
                    if (job == DIRECT || job == ADJOINT) {
                        FFT.apply(src, z, DIRECT);
                        multiplyByQ(((DoubleVector)q).getData(), ((DoubleVector)z).getData());
                        FFT.apply(z, dst, INVERSE);
                    } else {
                        throw new NotImplementedException();
                    }
                }
            };
        }

        /* Create convolution operator H. */
        H = new ConvolutionOperator(FFT, h);

        /* Check weights. */
        if (w == null) {
            muFactor = 1.0;
            W = new IdentityOperator(H.getOutputSpace());
        } else {
            double wMin, wMax;
            if (single) {
                float[] wMinMax = ArrayOps.getMinMax(((FloatVector)w).getData());
                wMin = (double)wMinMax[0];
                wMax = (double)wMinMax[1];
            } else {
                double[] wMinMax = ArrayOps.getMinMax(((DoubleVector)w).getData());
                wMin = wMinMax[0];
                wMax = wMinMax[1];
            }
            if (wMin < 0.0) {
                throw new IllegalArgumentException("Weights must be non-negative.");
            }
            if (wMax <= 0.0) {
                // FIXME: this is not an exception, just a very special case (a general solution
                // is to return  a vector of zeros).
                throw new IllegalArgumentException("All weights are zero.");
            }
            if (wMin == wMax) {
                // FIXME: more optimization possible?
                muFactor = 1.0/wMax;
                W = new IdentityOperator(H.getOutputSpace());
            } else {
                muFactor = 1.0;
                if (single) {
                    W = new LinearOperator(space) {
                        protected void privApply(Vector src, Vector dst, int job) {
                            if (job == DIRECT || job == ADJOINT) {
                                multiplyByW(((FloatVector)w).getData(),
                                        ((FloatVector)src).getData(),
                                        ((FloatVector)dst).getData());
                            } else {
                                throw new NotImplementedException();
                            }
                        }
                    };
                } else {
                    W = new LinearOperator(space) {
                        protected void privApply(Vector src, Vector dst, int job) {
                            if (job == DIRECT || job == ADJOINT) {
                                multiplyByW(((DoubleVector)w).getData(),
                                        ((DoubleVector)src).getData(),
                                        ((DoubleVector)dst).getData());
                            } else {
                                throw new NotImplementedException();
                            }
                        }
                    };
                }
            }
        }

        /* Regularization weight. */
        if (mu < 0.0) {
            throw new IllegalArgumentException("Regularization weight must be non-negative.");
        }
        mu *= muFactor;

        /* Creation of the LHS matrix A and RHS vector b for the linear problem. */
        A = new LeftHandSideMatrix(H, W, Q , mu);
        b = A.getOutputSpace().create();
        A.computeRightHandSideVector(y, b);
        cg = new LinearConjugateGradient(A, b);
    }

    private static void multiplyByQ(final double[] q, double[] z) {
        int size = q.length;
        for (int k = 0; k < size; ++k) {
            z[2*k] *= q[k];
            z[2*k + 1] *= q[k];
        }
    }

    private static void multiplyByQ(final float[] q, float[] z) {
        int size = q.length;
        for (int k = 0; k < size; ++k) {
            z[2*k] *= q[k];
            z[2*k + 1] *= q[k];
        }
    }

    private static void multiplyByW(final double[] w, final double[] x, double[] y) {
        int n = w.length;
        for (int j = 0; j < n; ++j) {
            y[j] = w[j]*x[j];
        }
    }

    private static void multiplyByW(final float[] w, final float[] x, float[] y) {
        int n = w.length;
        for (int j = 0; j < n; ++j) {
            y[j] = w[j]*x[j];
        }
    }

    private static double[] generateFrequency(double s, int length) {
        double[] u = new double[length];
        int k0 = length/2;
        for (int k = 0; k <= k0; ++k) {
            u[k] = s*(double)k;
        }
        for (int k = k0 + 1; k < length; ++k) {
            u[k] = s*(k - length);
        }
        return u;
    }
    private static void generateIsotropicQ(int[] shape, float[] q) {
        int n = q.length;
        double[] qTemp = new double[n];
        generateIsotropicQ(shape, qTemp);
        for (int j = 0; j < n; ++j) {
            q[j] = (float)qTemp[j];
        }
    }
    private static void generateIsotropicQ(int[] shape, double[] q) {
        /* Compute weights q = 4*PI^2*sum(kj/Nj) for operator Q. */
        int rank = shape.length;
        double[][] u = new double[rank][];
        for (int r = 0; r < rank; ++r) {
            double[] t = generateFrequency(2.0*Math.PI/shape[r], shape[r]);
            for (int k = 0; k < t.length; ++k) {
                t[k] = t[k]*t[k];
            }
            u[r] = t;
        }
        if (rank == 1) {
            int n1 = shape[0];
            double[] u1 = u[0];
            for (int k1 = 0; k1 < n1; ++k1) {
                q[k1] = u1[k1];
            }
        } else if (rank == 2) {
            int n1 = shape[0];
            int n2 = shape[1];
            double[] u1 = u[0];
            double[] u2 = u[1];
            for (int k2 = 0; k2 < n2; ++k2) {
                for (int k1 = 0; k1 < n1; ++k1) {
                    q[k1 + n1*k2] = u1[k1] + u2[k2];
                }
            }
        } else {
            int n1 = shape[0];
            int n2 = shape[1];
            int n3 = shape[2];
            double[] u1 = u[0];
            double[] u2 = u[1];
            double[] u3 = u[2];
            for (int k3 = 0; k3 < n3; ++k3) {
                for (int k2 = 0; k2 < n2; ++k2) {
                    for (int k1 = 0; k1 < n1; ++k1) {
                        q[k1 + n1*k2 + n1*n2*k3] = u1[k1] + u2[k2] + u3[k3];
                    }
                }
            }
        }
    }

    /**
     * 
     */
    public double getMu() {
        return A.getMu()/muFactor;
    }

    /**
     * 
     */
    public void setMu(double mu) {
        A.setMu(mu*muFactor);
    }

    /**
     * Compute the solution and store the result in x
     * 
     * @param x
     * @param maxiter
     * @param reset
     * @return
     */
    public int solve(float[] x, int maxiter, boolean reset) {
        if (! single) {
            throw new IllegalArgumentException("Expecting a single precision floating point array.");
        }
        return solve(((FloatVectorSpace)A.getInputSpace()).wrap(x), maxiter, reset);
    }

    /**
     * Compute the solution and store the result in x
     * 
     * @param x
     * @param maxiter
     * @param reset
     * @return
     */
    public int solve(double[] x, int maxiter, boolean reset) {
        if (single) {
            throw new IllegalArgumentException("Expecting a double precision floating point array.");
        }
        return solve(((DoubleVectorSpace)A.getInputSpace()).wrap(x), maxiter, reset);
    }

    /**
     * 
     * Not sure if can really be used
     */
    //FIXME should not be public, cause space of x will not be equal further
    public int solve(Vector x, int maxiter, boolean reset) {
        return cg.solve(x, maxiter, reset);
    }
    
    public int solve2(double[] x, int maxiter, boolean reset) {
        if (single) {
            throw new IllegalArgumentException("Expecting a double precision floating point array.");
        }
        return solve2(((DoubleVectorSpace)A.getInputSpace()).wrap(x), maxiter, reset);
    }

    /**
     * 
     * Not sure if can really be used
     */
    //FIXME should not be public, cause space of x will not be equal further
    public int solve2(Vector x, int maxiter, boolean reset) {
        return cg.solve2(x, maxiter, reset);
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