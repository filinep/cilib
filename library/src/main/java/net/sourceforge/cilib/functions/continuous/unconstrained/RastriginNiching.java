/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.functions.NichingFunction;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * X. Li, A. Engelbrecht, and M.G. Epitropakis, ``Benchmark Functions for CEC'2013 Special Session and Competition 
 * on Niching Methods for Multimodal Function Optimization'', Technical Report, Evolutionary Computation and Machine 
 * Learning Group, RMIT University, Australia, 2013
 */
public class RastriginNiching extends ContinuousFunction implements Gradient, NichingFunction {
    
    private List<Double> k;

    public RastriginNiching() {
        this.k = new ArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Preconditions.checkState(k.size() >= input.size(), "Not enough k values for the given input vector.");
        double sum = 0;
        for (int i = 0; i < input.size(); ++i) {
            sum += 10 + 9 * Math.cos(2 * Math.PI * input.doubleValueOf(i) * k.get(i));
        }
        return -sum;
    }
    
    public void setK(Double k) {
        this.k.add(k);
    }
    
    public double getAverageGradientVector(Vector x) {
        double sum = 0;
        for (Numeric n : getGradientVector(x)) {
            sum += n.doubleValue();
        }
        return sum / x.size();
    }
    
    public double getGradientVectorLength(Vector x) {
        return getGradientVector(x).length();
    }

    public Vector getGradientVector(Vector x) {
        Vector.Builder vectorBuilder = Vector.newBuilder();

        for (int i = 0; i < x.size(); ++i) {
            vectorBuilder.add(18*Math.PI*k.get(i)*Math.sin(2*Math.PI*k.get(i)*x.doubleValueOf(i)));
        }

        return vectorBuilder.build();
    }

    @Override
    public double getNicheRadius() {
        return 0.01;
    }
}
