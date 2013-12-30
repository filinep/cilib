/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.NichingFunction;
import net.sourceforge.cilib.type.types.container.Vector;

import com.google.common.base.Preconditions;

import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.type.types.Numeric;

/**
 * The InvertedHimmelblau function.
 *
 * <p>Title: CILib</p>
 * <p>Description: CILib (Computational Intelligence Library)</p>
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * Characteristics:
 * <ul>
 * <li>Only defined for 2 dimensions</li>
 * <li>Multimodal</li>
 * <li>Continuous</li>
 * </ul>
 *
 * R(-6, 6)^2
 *
 * @version 1.0
 */
public class Himmelblau extends ContinuousFunction implements Gradient, NichingFunction {

    private static final long serialVersionUID = 7323733640884766707L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() == 2, "Himmelblau function is only defined for 2 dimensions");

        double x = input.doubleValueOf(0);
        double y = input.doubleValueOf(1);
        return Math.pow((x * x + y - 11.0), 2.0) + Math.pow((x + y * y - 7.0), 2.0);
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
    
    public Vector getGradientVector(Vector input) {
        Preconditions.checkArgument(input.size() == 2, "Himmelblau function is only defined for 2 dimensions");

        double x = input.doubleValueOf(0);
        double y = input.doubleValueOf(1);

        return Vector.of(4.0 * x * (x * x + y - 11.0) - 2.0 * (x + y * y - 7.0),
                         2.0 * (x * x + y - 11.0) - 4.0 * y * (x + y * y - 7.0));
    }

    @Override
    public double getNicheRadius() {
        return 0.01;
    }
}
