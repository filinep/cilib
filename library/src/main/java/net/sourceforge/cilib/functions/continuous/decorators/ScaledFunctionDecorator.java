/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.decorators;

import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Characteristics:
 *
 * Let c be a fixed positive number.
 *
 * Horizontal Scaling: If g(x) = f(cx), then (c > 1) means that g(x) is f(x)
 * compressed in the horizontal direction by a factor of c (0 < c < 1) means
 * that g(x) is f(x) stretched in the horizontal direction by a factor of 1/c
 *
 * Vertical Scaling: If g(x) = cf(x), then (c > 1) means that g(x) is f(x)
 * stretched in the vertical direction by a factor of c (0 < c < 1) means that
 * g(x) is f(x) compressed in the vertical direction by a factor of 1/c
 *
 */
public class ScaledFunctionDecorator extends ContinuousFunction implements Gradient {

    private static final long serialVersionUID = -5316734133098401441L;
    private ContinuousFunction function;
    private ControlParameter verticalScale;
    private ControlParameter horizontalScale;

    /**
     * Create an instance of the decorator.
     */
    public ScaledFunctionDecorator() {
        verticalScale = ConstantControlParameter.of(1.0);
        horizontalScale = ConstantControlParameter.of(1.0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Vector tmp = Vector.copyOf(input);

        for (int i = 0; i < input.size(); i++) {
            tmp.setReal(i, (horizontalScale.getParameter() * input.doubleValueOf(i)));
        }

        return (verticalScale.getParameter() * function.f(tmp));
    }

    /**
     * Get the decorated function.
     * @return The decorated function.
     */
    public ContinuousFunction getFunction() {
        return function;
    }

    /**
     * Set the function that is to be decorated.
     * @param function The function to decorated.
     */
    public void setFunction(ContinuousFunction function) {
        this.function = function;
    }

    public void setVerticalScale(ControlParameter verticalScale) {
        this.verticalScale = verticalScale;
    }

    public ControlParameter getVerticalScale() {
        return verticalScale;
    }

    public void setHorizontalScale(ControlParameter horizontalScale) {
        this.horizontalScale = horizontalScale;
    }

    public ControlParameter getHorizontalScale() {
        return horizontalScale;
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

    @Override
    public Vector getGradientVector(Vector x) {
        Vector tmp = Vector.copyOf(x);

        for (int i = 0; i < x.size(); i++) {
            tmp.setReal(i, (horizontalScale.getParameter() * x.doubleValueOf(i)));
        }

        return ((Gradient)function).getGradientVector(tmp).multiply(verticalScale.getParameter());
    }
}
