/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.controlparameter;

import net.sourceforge.cilib.algorithm.AbstractAlgorithm;

/**
 * A {@linkplain net.sourceforge.cilib.controlparameter.ControlParameter control parameter}
 * to represent a constant value. The specified value will be maintained until it is altered.
 */
public class WEPSOControlParameter implements ControlParameter {
    
    protected double w;
    
    /**
     * Create a new instance of {@code ConstantControlParameter}.
     */
    public WEPSOControlParameter() {

    }

    /**
     * Create a new instance of {@linkplain net.sourceforge.cilib.controlparameter.ConstantControlParameter}
     * with the provided value as the value for the {@linkplain net.sourceforge.cilib.controlparameter.ControlParameter}.
     * @param value The value to set.
     */
    protected WEPSOControlParameter(double value) {
        this.w = value;
    }

    /**
     * Create a copy of the provided instance.
     * @param copy The instance to copy.
     */
    public WEPSOControlParameter(WEPSOControlParameter copy) {
        this.w = copy.w;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WEPSOControlParameter getClone() {
        return new WEPSOControlParameter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getParameter() {
        return Math.pow(w,AbstractAlgorithm.get().getIterations());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double getParameter(double min, double max) {
        throw new UnsupportedOperationException("ConstantControlParameter has no bounds. Use a BoundedControlParameter instead.");
    }

    /**
     * Sets the constant parameter.
     * 
     * @param value The new constant parameter.
     */
    public void setW(double value) {
        this.w = value;
    }

}
