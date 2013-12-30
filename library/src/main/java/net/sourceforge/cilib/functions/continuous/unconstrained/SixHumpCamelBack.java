/**
 * __ __ _____ _/ /_/ /_ Computational Intelligence Library (CIlib) / ___/ / / /
 * __ \ (c) CIRG @ UP / /__/ / / / /_/ / http://cilib.net \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.NichingFunction;
import net.sourceforge.cilib.type.types.container.Vector;

import com.google.common.base.Preconditions;

import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.type.types.Numeric;

/**
 * InvertedSixHumpCamelBack function.
 *
 * <p>
 * Title: CILib</p>
 * <p>
 * Description: CILib (Computational Intelligence Library)</p>
 * <p>
 * Copyright: Copyright (c) 2004</p>
 * <p>
 * Company: </p>
 *
 * <p>
 * Characteristics:
 * <ul>
 * <li>Only defined for 2 dimensions</li>
 * <li>Multimodal</li>
 * <li>Continuous</li>
 * <li>Non Separable</li>
 * </ul>
 * </p>
 *
 * f(x) = -1.0316; x = (-0.0898, 0.1726); x = (0.0898, -0.1726) x_1 e [-3, 3];
 * x_2 e [-2, 2]
 *
 * R(-3,3),R(-2,2)
 *
 * @version 1.0
 */
public class SixHumpCamelBack extends ContinuousFunction implements Gradient, NichingFunction {

    private static final long serialVersionUID = -3834640752316926216L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() == 2, "SixHumpCamelBack function is only defined for 2 dimensions");

        double x = input.doubleValueOf(0);
        double y = input.doubleValueOf(1);

        return (4 - 2.1 * x * x + x * x * x * x / 3.0) * x * x + x * y + y * y * (4 * y * y - 4);
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
        double x = input.doubleValueOf(0);
        double y = input.doubleValueOf(1);

        
        return Vector.of(y + 2 * x * (4 - 2.1 * x * x + x * x * x * x / 3.0) + x * x * (4 * x * x * x / 3.0 - 4.2 * x),
                         8 * y * y * y + 2 * y * (4 * y * y - 4) + x);
    }

    @Override
    public double getNicheRadius() {
        return 0.5;
    }
}
