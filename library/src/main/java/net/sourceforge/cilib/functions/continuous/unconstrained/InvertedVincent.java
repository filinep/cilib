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
 * <p><b>The InvertedVincent Function.</b></p>
 *
 * <p>Characteristics:
 * <ul>
 * <li>Multi-dimensional only</li>
 * <li>Multimodal</li>
 * <li>Non-Separable</li>
 * <li>for n=1, 6 global minima and no local minima</li>
 * <li>for n=2, 36 global minima and no local minima</li>
 * </ul>
 * </p>
 *
 * R(0.25,10)^n
 *
 */
public class InvertedVincent extends ContinuousFunction implements Gradient, NichingFunction {
    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        double result = 1.0;
        for (int i = 0; i < input.size(); ++i)
            result += (1.0/(double)(input.size()))*Math.sin(10.0*Math.log10(input.doubleValueOf(i)));
        return result;
    }
    
    public Double df(Vector input, int i){
    double result=0.0;
    result=(10.0*Math.cos(10.0*Math.log10(input.doubleValueOf(i-1))))/(input.size()*Math.log(10.0)*input.doubleValueOf(i-1));
    
        return result;
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
		return 0.2;
	}
}
