/**
 * __ __ _____ _/ /_/ /_ Computational Intelligence Library (CIlib) / ___/ / / /
 * __ \ (c) CIRG @ UP / /__/ / / / /_/ / http://cilib.net \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * InvertedSchwefel function.
 *
 * <p>
 * Characteristics:
 * <ul>
 * <li>Multimodal</li>
 * <li>Separable</li>
 * <li>Discontinuous</li>
 * </ul>
 *
 * f(x) = 0; x = (-420.9687,...,-420.9687);
 *
 * x e [-512.03,511.97]
 *
 * R(-512.03, 511.97)^30
 *
 */
public class InvertedSchwefel extends ContinuousFunction implements Gradient {

    private static final long serialVersionUID = 3835871629510784855L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        double sum = 0;
        for (int i = 0; i < input.size(); ++i) {
            sum += input.doubleValueOf(i) * Math.sin(Math.sqrt(Math.abs(input.doubleValueOf(i))));
        }
        return sum;
    }

    public Double df(Vector input, int i) {
        double result = 0.0;
        double exp1 = (input.doubleValueOf(i - 1) * Math.cos(Math.sqrt(Math.abs(input.doubleValueOf(i - 1))))) / (2.0 * Math.sqrt(Math.abs(input.doubleValueOf(i - 1))));
        double exp2 = (input.doubleValueOf(i - 1)) / (Math.abs(input.doubleValueOf(i - 1)));
        double exp3 = Math.sin(Math.abs(input.doubleValueOf(i - 1)));

        result = exp1 * exp2 + exp3;
        return result;
    }

    public double getAverageGradientVector(Vector x) {

        double sum = 0;

        for (int i = 1; i <= x.size(); ++i) {
            sum += this.df(x, i);
        }

        return sum / x.size();
    }

    public double getGradientVectorLength(Vector x) {
        double sumsqrt = 0;

        for (int i = 1; i <= x.size(); ++i) {
            sumsqrt += this.df(x, i) * this.df(x, i);
        }

        return Math.sqrt(sumsqrt);
    }

    public Vector getGradientVector(Vector x) {
        Vector.Builder vectorBuilder = Vector.newBuilder();

        for (int i = 1; i <= x.size(); ++i) {
            vectorBuilder.add(this.df(x, i));
        }

        return vectorBuilder.build();
    }
}
