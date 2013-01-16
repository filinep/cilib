/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.controlparameter;

import net.sourceforge.cilib.algorithm.AbstractAlgorithm;

/**
 * A Control parameter that varies linearly according to the completed percentage of the algorithm.
 */
public class LinearlyVaryingControlParameter implements ControlParameter {
    
    private ControlParameter initialValue;
    private ControlParameter finalValue;
    
    public LinearlyVaryingControlParameter() {
        this(0.0, Double.MAX_VALUE);
    }

    public LinearlyVaryingControlParameter(double initialValue, double finalValue) {
        this.initialValue = ConstantControlParameter.of(initialValue);
        this.finalValue = ConstantControlParameter.of(finalValue);
    }
    
    public LinearlyVaryingControlParameter(LinearlyVaryingControlParameter copy) {
        this.initialValue = copy.initialValue;
        this.finalValue = copy.finalValue;
    }
    
    @Override
    public LinearlyVaryingControlParameter getClone() {
        return new LinearlyVaryingControlParameter(this);
    }

    @Override
    public double getParameter() {
        return getParameter(initialValue.getParameter(), finalValue.getParameter());
    }
    
    @Override
    public double getParameter(double initialVal, double finalVal) {
        return initialVal + (finalVal - initialVal) * AbstractAlgorithm.get().getPercentageComplete();
    } 

    public void setInitialValue(double initialValue) {
        this.initialValue = ConstantControlParameter.of(initialValue);
    }

    public void setFinalValue(double finalValue) {
        this.finalValue = ConstantControlParameter.of(finalValue);
    }

    public void setParameter(double newParameter) {
        finalValue = ConstantControlParameter.of(newParameter);
    }

    public void setInitialParameter(ControlParameter initialValue) {
        this.initialValue = initialValue;
    }

    public ControlParameter getInitialValue() {
        return initialValue;
    }

    public void setFinalParameter(ControlParameter finalValue) {
        this.finalValue = finalValue;
    }

    public ControlParameter getFinalValue() {
        return finalValue;
    }
}
