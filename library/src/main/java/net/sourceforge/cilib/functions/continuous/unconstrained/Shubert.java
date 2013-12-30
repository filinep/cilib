/**
 * __ __ _____ _/ /_/ /_ Computational Intelligence Library (CIlib) / ___/ / / /
 * __ \ (c) CIRG @ UP / /__/ / / / /_/ / http://cilib.net \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.functions.NichingFunction;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * <p>
 * <b>The InvertedShubert Function.</b></p>
 *
 * <p>
 * Global Minimum:
 * <ul>
 * <li>&fnof;(<b>x</b>*) = -186.7309088</li>
 * <li> Many global minima: n=1 has 3, n=2 has 9, n=3 has 81, n=4 has 324, n has
 * pow(3,n)</li>
 * <li> All unevenly spaced</li>
 * <li> for x<sub>i</sub> in [-10,10]</li>
 * </ul>
 * </p>
 *
 * <p>
 * Local Minimum:
 * <ul>
 * <li> Many local minima</li>
 * </ul>
 * </p>
 *
 * <p>
 * Characteristics:
 * <ul>
 * <li>Multi-dimensional</li>
 * <li>Multimodal</li>
 * <li>Non-Separable</li>
 * </ul>
 * </p>
 *
 */
public class Shubert extends ContinuousFunction implements Gradient, NichingFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        double result = 1.0;
        for (int i = 0; i < input.size(); ++i) {
            double result2 = 0.0;
            for (int j = 1; j <= 5; j++) {
                result2 += j * Math.cos((j + 1) * input.doubleValueOf(i) + j);
            }
            result *= result2;
        }
        return result;
    }
    
    public Double df(Vector input, int k) {
        double prod = 1.0;
        for (int i = 0; i < input.size(); ++i) {
            if (k != i) {
                double sum = 0.0;
                for (int j = 1; j <= 5; ++j) {
                    sum += j * Math.cos((j + 1) * input.doubleValueOf(i) + j);
                }
                prod *= sum;
            }
        }
        
        double sum2 = 0.0;
        for (int j = 1; j <= 5; ++j) {
            sum2 += -j * (j + 1) * Math.sin(input.doubleValueOf(k)*(j+1) + j);
        }

        return prod * sum2;
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
            vectorBuilder.add(this.df(x, i));
        }

        return vectorBuilder.build();
    }

    @Override
    public double getNicheRadius() {
        return 0.5;
    }
}
