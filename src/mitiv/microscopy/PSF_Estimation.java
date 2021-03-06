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

package mitiv.microscopy;

import mitiv.base.Shape;
import mitiv.array.ArrayFactory;
import mitiv.array.DoubleArray;
import mitiv.cost.QuadraticCost;
import mitiv.deconv.ConvolutionOperator;
import mitiv.exception.IncorrectSpaceException;
import mitiv.invpb.ReconstructionJob;
import mitiv.invpb.ReconstructionSynchronizer;
import mitiv.invpb.ReconstructionViewer;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
import mitiv.optim.ArmijoLineSearch;
import mitiv.optim.BoundProjector;
import mitiv.optim.LBFGS;
import mitiv.optim.LBFGSB;
import mitiv.optim.LineSearch;
import mitiv.optim.MoreThuenteLineSearch;
import mitiv.optim.NonLinearConjugateGradient;
import mitiv.optim.OptimTask;
import mitiv.optim.ReverseCommunicationOptimizer;
import mitiv.optim.SimpleBounds;
import mitiv.optim.SimpleLowerBound;
import mitiv.optim.SimpleUpperBound;
import mitiv.utils.MathUtils;

public class PSF_Estimation implements ReconstructionJob {

    private double mu = 10.0;
    private double epsilon = 1.0;
    private double gatol = 0.0;
    private double grtol = 1e-3;
    private int limitedMemorySize = 0;
    private double lowerBound = Double.NEGATIVE_INFINITY;
    private double upperBound = Double.POSITIVE_INFINITY;
    private boolean verbose = false;
    private boolean debug = false;
    private int maxiter = 200;
    private DoubleArray data = null;
    private DoubleArray psf = null;
    private DoubleArray result = null;
    private double fcost = 0.0;
    private DoubleShapedVector gcost = null;
    private MicroscopyModelPSF1D pupil = null;
    private ReverseCommunicationOptimizer minimizer = null;
    private ReconstructionViewer viewer = null;
    private ReconstructionSynchronizer synchronizer = null;
    private double[] synchronizedParameters = {0.0, 0.0};
    private boolean[] change = {false, false};
    private String[] synchronizedParameterNames = {"Regularization Level", "Relaxation Threshold"};
    private double[] weights = null;

    public void createSynchronizer() {
        if (synchronizer == null) {
            synchronizedParameters[0] = mu;
            synchronizedParameters[1] = epsilon;
            synchronizer = new ReconstructionSynchronizer(synchronizedParameters);
        }
    }
    public void deleteSynchronizer() {
        synchronizer = null;
    }
    // FIXME: names should be part of the synchronizer...
    public String getSynchronizedParameterName(int i) {
        return synchronizedParameterNames[i];
    }

    private boolean run = true;

    public PSF_Estimation() {
    }

    private static void fatal(String reason) {
        throw new IllegalArgumentException(reason);
    }


    public void fitPSF(DoubleShapedVector x, int flag) {

        // Check input data and get dimensions.
        if (data == null) {
            fatal("Input data not specified.");
        }
        Shape shape = data.getShape();
        int rank = data.getRank();

        // Check the PSF.
        if (psf == null) {
            fatal("PSF not specified.");
        }
        if (psf.getRank() != rank) {
            fatal("PSF must have same rank as data.");
        }
        for (int k = 0; k < rank; ++k) {
            if (psf.getDimension(k) != shape.dimension(k)) {
                fatal("The dimensions of the PSF must match those of the input image.");
            }
        }
        if (result != null) {
            /* We try to keep the previous result, at least its dimensions
             * must match. */
            for (int k = 0; k < rank; ++k) {
                if (result.getDimension(k) != data.getDimension(k)) {
                    result = null;
                    break;
                }
            }
        }

        //DoubleShapedVectorSpace xSpace = new DoubleShapedVectorSpace(x.getDimension(0));
        DoubleShapedVectorSpace xSpace = x.getSpace();
        // Initialize a vector space and populate it with workspace vectors.
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        RealComplexFFT FFT = new RealComplexFFT(space);
        LinearOperator W = null;
        if (weights != null) {
            // FIXME: for now the weights are stored as a simple Java vector.
            if (weights.length != data.getNumber()) {
                throw new IllegalArgumentException("Error weights and input data size don't match");
            }
            W = new LinearOperator(space) {
                @Override
                protected void privApply(Vector src, Vector dst, int job)
                        throws IncorrectSpaceException {
                    double[] inp = ((DoubleShapedVector)src).getData();
                    double[] out = ((DoubleShapedVector)dst).getData();
                    int number = src.getNumber();
                    for (int i = 0; i < number; ++i) {
                        out[i] = inp[i]*weights[i];
                    }
                }
            };
        }
        double[] tmp = psf.flatten();
        DoubleShapedVector y = space.wrap(data.flatten());
        DoubleShapedVector h = space.wrap(tmp);

        result = ArrayFactory.wrap(x.getData(), x.getShape());
        ConvolutionOperator H = new ConvolutionOperator(FFT, h);
        if (debug) {
            System.out.println("Vector space initialization complete.");
        }

        // Build the cost functions
        QuadraticCost fdata = new QuadraticCost(H, y, W);
        //CompositeDifferentiableCostFunction cost = new CompositeDifferentiableCostFunction(1.0, fdata);
        fcost = 0.0;
        gcost = space.create();
        if (debug) {
            System.out.println("Cost function initialization complete.");
        }

        // Initialize the non linear conjugate gradient
        LineSearch lineSearch = null;
        LBFGS lbfgs = null;
        LBFGSB lbfgsb = null;
        NonLinearConjugateGradient nlcg = null;
        BoundProjector projector = null;
        int bounded = 0;
        if (lowerBound != Double.NEGATIVE_INFINITY) {
            bounded |= 1;
        }
        if (upperBound != Double.POSITIVE_INFINITY) {
            bounded |= 2;
        }
        if (bounded == 0) {
            /* No bounds have been specified. */
            lineSearch = new MoreThuenteLineSearch(0.05, 0.1, 1E-17);
            if (limitedMemorySize > 0) {
                lbfgs = new LBFGS(xSpace, limitedMemorySize, lineSearch);
                lbfgs.setAbsoluteTolerance(gatol);
                lbfgs.setRelativeTolerance(grtol);
                minimizer = lbfgs;
            } else {
                int method = NonLinearConjugateGradient.DEFAULT_METHOD;
                nlcg = new NonLinearConjugateGradient(xSpace, method, lineSearch);
                nlcg.setAbsoluteTolerance(gatol);
                nlcg.setRelativeTolerance(grtol);
                minimizer = nlcg;
            }
        } else {
            /* Some bounds have been specified. */
            lineSearch = new ArmijoLineSearch(0.5, 0.1);
            if (bounded == 1) {
                /* Only a lower bound has been specified. */
                projector = new SimpleLowerBound(space, lowerBound);
            } else if (bounded == 2) {
                /* Only an upper bound has been specified. */
                projector = new SimpleUpperBound(space, upperBound);
            } else {
                /* Both a lower and an upper bounds have been specified. */
                projector = new SimpleBounds(space, lowerBound, upperBound);
            }
            int m = (limitedMemorySize > 1 ? limitedMemorySize : 5);
            lbfgsb = new LBFGSB(space, projector, m, lineSearch);
            lbfgsb.setAbsoluteTolerance(gatol);
            lbfgsb.setRelativeTolerance(grtol);
            minimizer = lbfgsb;
            projector.apply(x, x);

        }

        if (debug) {
            System.out.println("Optimization method initialization complete.");
        }

        DoubleShapedVector gX = xSpace.create();
        // Launch the non linear conjugate gradient
        OptimTask task = minimizer.start();
        int iter = 0;
        while (run) {
            if (task == OptimTask.COMPUTE_FG) {
                iter++;
                if(flag == 1)
                {
                    /*
                    System.out.println("--------------");
                    System.out.println("defocus");
                    MathUtils.printArray(x.getData());
                    */
                    pupil.setDefocus(x.getData());
                }
                else if (flag == 2)
                {
                    /*
                    System.out.println("--------------");
                    System.out.println("alpha");
                    MathUtils.printArray(x.getData());
                    */
                    pupil.setPhi(x.getData());
                }
                else if(flag == 3)
                {
                    /*
                    System.out.println("--------------");
                    System.out.println("beta");
                    MathUtils.printArray(x.getData());
                    */
                    pupil.setRho(x.getData());
                }
                pupil.computePSF();             
                fcost = fdata.computeCostAndGradient(1.0, space.wrap(pupil.getPSF()), gcost, true);

                //System.out.format("fdata = %22.15E\n", fcost);
                if(flag == 1)
                {
                    gX = xSpace.wrap(pupil.apply_J_defocus(gcost.getData()));
                    //System.out.println("grd");
                    //MathUtils.printArray(gX.getData());
                }
                else if (flag == 2) 
                {
                    gX = xSpace.wrap(pupil.apply_J_phi(gcost.getData()));
                    //System.out.println("grd");
                    //MathUtils.printArray(gX.getData());
                }
                else if(flag == 3)
                {
                    gX = xSpace.wrap(pupil.apply_J_rho(gcost.getData()));
                    //System.out.println("grd");
                    //MathUtils.printArray(gX.getData());
                }
            } else if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                if (viewer != null) {
                    viewer.display(this);
                }
                boolean stop = (task == OptimTask.FINAL_X);
                if (! stop && maxiter >= 0 && minimizer.getIterations() >= maxiter) {
                    System.err.format("Warning: too many iterations (%d).\n", maxiter);
                    stop = true;
                }
                if (stop) {
                    break;
                }
            } else {
                System.err.println("error/warning: " + task);
                break;
            }
            if (synchronizer != null) {
                /* FIXME: check the values, suspend/resume, restart the algorithm, etc. */
                if (synchronizer.getTask() == ReconstructionSynchronizer.STOP) {
                    break;
                }
                synchronizedParameters[0] = mu;
                synchronizedParameters[1] = epsilon;
                if (synchronizer.updateParameters(synchronizedParameters, change)) {
                    if (change[0]) {
                        mu = synchronizedParameters[0];
                    }
                    if (change[1]) {
                        epsilon = synchronizedParameters[1];
                    }
                    // FIXME: restart!!!
                }
            }
            /*
            System.out.println("Evaluations");
            System.out.println(minimizer.getEvaluations());
            System.out.println("Iterations");
            System.out.println(minimizer.getIterations());
            */
           // System.out.println("i");
            task = minimizer.iterate(x, fcost, gX);
            if(minimizer.getEvaluations() > 20)
                break;
        }
        if (verbose) {
            System.out.format("min(x) = %g\n", ArrayOps.getMin(x.getData()));
            System.out.format("max(x) = %g\n", ArrayOps.getMax(x.getData()));
        }
        
        if(flag == 1)
        {
            System.out.println("--------------");
            System.out.println("defocus");
            MathUtils.printArray(x.getData());
            pupil.setDefocus(x.getData());
        }
        else if (flag == 2)
        {
            System.out.println("--------------");
            System.out.println("alpha");
            MathUtils.printArray(x.getData());
            pupil.setPhi(x.getData());
        }
        else if(flag == 3)
        {
            System.out.println("--------------");
            System.out.println("beta");
            MathUtils.printArray(x.getData());
            pupil.setRho(x.getData());
        }
    }
 
    /* Below are all methods required for a RecosntructionJob. */


    public void setVerboseMode(boolean value) {
        verbose = value;
    }
    public void setDebugMode(boolean value) {
        debug = value;
    }

    public void setMaximumIterations(int value) {
        maxiter = value;
    }
    public void setLimitedMemorySize(int value) {
        limitedMemorySize = value;
    }

    public void setRegularizationWeight(double value) {
        mu = value;
    }
    public void setRegularizationThreshold(double value) {
        epsilon = value;
    }
    public void setAbsoluteTolerance(double value) {
        gatol = value;
    }
    public void setRelativeTolerance(double value) {
        grtol = value;
    }
    public double getRelativeTolerance() {
        return grtol;
    }
    public void setLowerBound(double value) {
        lowerBound = value;
    }
    public void setUpperBound(double value) {
        upperBound = value;
    }
    public void stop(){
        run = false;
    }
    public void setWeight(double[] W){
        this.weights = W;
    }

    
    public ReconstructionViewer getViewer() {
        return viewer;
    }
    public void setViewer(ReconstructionViewer rv) {
        viewer = rv;
    }
    public ReconstructionSynchronizer getSynchronizer() {
        return synchronizer;
    }
    
    public void setPupil(MicroscopyModelPSF1D pupil)
    {
        this.pupil = pupil;
    }
    
    public DoubleArray getData() {
        return data;
    }
    
    public void setData(DoubleArray data) {
        this.data = data;
    }
    
    public DoubleArray getPsf() {
        return psf;
    }
    public void setPsf(DoubleArray psf) {
        this.psf = psf;
    }
    
    @Override
    public DoubleArray getResult() {
        /* Nothing else to do because the actual result is in a vector
         * which shares the contents of the ShapedArray.  Otherwise,
         * some kind of synchronization is needed. */
        return result;
    }

    public void setResult(DoubleArray result) {
        this.result = result;
    }
    
    @Override
    public int getIterations() {
        return (minimizer == null ? 0 : minimizer.getIterations());
    }

    @Override
    public int getEvaluations() {
        return (minimizer == null ? 0 : minimizer.getEvaluations());
    }

    @Override
    public double getCost() {
        return fcost;
    }

    @Override
    public double getGradientNorm2() {
        return (gcost == null ? 0.0 : gcost.norm2());
    }

    @Override
    public double getGradientNorm1() {
        return (gcost == null ? 0.0 : gcost.norm1());
    }

    @Override
    public double getGradientNormInf() {
        return (gcost == null ? 0.0 : gcost.normInf());
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