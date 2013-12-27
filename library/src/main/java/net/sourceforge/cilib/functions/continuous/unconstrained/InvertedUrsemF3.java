/**
 * __ __ _____ _/ /_/ /_ Computational Intelligence Library (CIlib) / ___/ / / /
 * __ \ (c) CIRG
 *
 * @ UP / /__/ / / / /_/ / http://cilib.net \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

import com.google.common.base.Preconditions;
import net.sourceforge.cilib.functions.Gradient;

/**
 * InvertedUrsemF3 function.
 *
 * Minimum: 2.5 R(-2, 2)^2
 *
 */
public class InvertedUrsemF3 extends ContinuousFunction implements Gradient {

    private static final long serialVersionUID = -4477290008482842765L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() == 2, "UrsemF3 function is only defined for 2 dimensions");

        double x = input.doubleValueOf(0);
        double y = input.doubleValueOf(1);

        double result = Math.sin(2.2 * Math.PI * x - 0.5 * x) * ((2.0 - Math.abs(y)) / 2.0) * ((3.0 - Math.abs(x)) / 2.0);
        result += Math.sin(0.5 * Math.PI * y * y + 0.5 * Math.PI) * ((2.0 - Math.abs(y)) / 2.0) * ((2.0 - Math.abs(x)) / 2.0);

        return result;
    }

    public Double df(Vector input, int i) {
        double result = 0;//-2;
        double x = input.doubleValueOf(0);
        double y = input.doubleValueOf(1);

        if (i == 1) {

            result = -(1.0 / 4.0) * (0.5 - (11.0 * Math.PI / 5.0)) * (3.0 - Math.abs(x)) * (2.0 - Math.abs(y)) * Math.cos(x / 2.0 - (11.0 * Math.PI * x) / 5.0);
            result += -(1.0 / 4.0) * (2.0 - Math.abs(y)) * (Math.cos(Math.PI * y * y / 2.0) - Math.sin(x / 2.0 - 11.0 * Math.PI * x / 5.0)) * (y / Math.abs(y));
      
        } else if (i == 2) {
            result = -(1.0 / 4.0) * Math.PI * y * (2.0 - Math.abs(x)) * (2.0 - Math.abs(y)) * Math.sin(Math.PI * y * y / 2.0);
            result += -(1.0 / 4.0) * (2.0 - Math.abs(x)) * Math.cos(Math.PI * y * y / 2.0) * (y / Math.abs(y));
            result += (1.0 / 4.0) * (3.0 - Math.abs(x)) * Math.sin(x / 2.0 - 11.0 * Math.PI * x / 5.0) * (y / Math.abs(y));

        }
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
        double sumsqrt = 0.0;

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
