/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.functions.NichingFunction;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * <p><b>The InvertedShubert Function.</b></p>
 *
 * <p>Global Minimum:
 * <ul>
 * <li>&fnof;(<b>x</b>*) = -186.7309088</li>
 * <li> Many global minima: n=1 has 3, n=2 has 9, n=3 has 81, n=4 has 324, n has pow(3,n)</li>
 * <li> All unevenly spaced</li>
 * <li> for x<sub>i</sub> in [-10,10]</li>
 * </ul>
 * </p>
 *
 * <p>Local Minimum:
 * <ul>
 * <li> Many local minima</li>
 * </ul>
 * </p>
 *
 * <p>Characteristics:
 * <ul>
 * <li>Multi-dimensional</li>
 * <li>Multimodal</li>
 * <li>Non-Separable</li>
 * </ul>
 * </p>
 *
 */
public class InvertedShubert extends ContinuousFunction implements Gradient, NichingFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        double result = 1.0;
        for (int i = 0; i < input.size(); ++i) {
            double result2 = 0.0;
            for (int j = 1; j <= 5; j++) {
                result2 += j*Math.cos((j+1)*input.doubleValueOf(i) + j);
            }
            result *= result2;
        }
        return result;
    }
    
    public Double df(Vector input, int i) {
        double res = 0.0;
        double sum1 = 0.0;
        for (int k = 1; k <= 5; ++k)
        {
            sum1 += k*Math.cos(k+1);
        }
        double prod = 1.0;
        for (int j = 1; j <= input.size(); ++j)
        {
            double sum2 = 0.0;
            for (int l = 1; l <= 5; ++l)
            {
                sum2 += l*input.doubleValueOf(j-1)*Math.cos(l+1);
            }
            prod = prod*sum2 + 15.0;
            
        }
        double sum3 = 0.0;
        for (int m = 1; m <= 5; ++m)
            {
                sum3 += m*input.doubleValueOf(i-1)*Math.cos(m+1);
            }
        res = sum1 + prod/sum3;
           
        return res;
        
    }
    
    public double GetGradientVectorAverage ( Vector x)
    {
        
        double sum = 0;
        
        for (int i = 1; i <= x.size(); ++i)
        {
            sum += this.df(x,i);
        }
           
        return sum/x.size();
    }
    
    public double GetGradientVectorLength (Vector x)
    {
        double sumsqrt = 0;
        
        for (int i = 1; i <= x.size(); ++i)
        {
            sumsqrt += this.df(x,i)*this.df(x,i);
        }
        
        return Math.sqrt(sumsqrt);
    }
    
    public Vector GetGradientVector (Vector x)
    {
        Vector.Builder vectorBuilder = Vector.newBuilder();
        
        for (int i = 1; i <= x.size(); ++i)
        {
             vectorBuilder.add(this.df(x,i));
        }
        
        return vectorBuilder.build();
    }

	@Override
	public double getNicheRadius() {
		return 0.5;
	}
}
