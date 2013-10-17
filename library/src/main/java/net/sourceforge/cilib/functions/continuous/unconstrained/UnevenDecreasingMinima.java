/**           __  __
 *    _____  / /_/ /_    Computational Intelligence Library (CIlib)
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
 * Multimodal4 function.
 *
 * Minimum for domain: 0.0
 * R(0, 1)^1
 *
 */
public class UnevenDecreasingMinima extends ContinuousFunction implements Gradient, NichingFunction {

    private static final long serialVersionUID = -957215773660609565L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        double sum = 0.0;
        for (int i = 0; i < input.size(); i++) {
            double x = Math.pow(Math.sin(5.0 * Math.PI * (Math.pow(input.doubleValueOf(i), 0.75) - 0.05)), 6.0);
            double exp1 = -2.0 * Math.log(Math.pow((input.doubleValueOf(i) - 0.08) / 0.854, 2.0));
            double y = Math.exp(exp1);
            sum += -x * y;
        }
        return sum;
    }
    
    public Double df(Vector input, int i){
    double result=0.0;
    double exp1=-Math.pow(Math.sin(5.0 * Math.PI * (Math.pow(input.doubleValueOf(i-1), 0.75) - 0.05)), 5.0);
    double exp2=Math.exp(-2.0 * Math.log(Math.pow((input.doubleValueOf(i-1) - 0.08) / 0.854, 2.0)));
    double exp3=(4.0*Math.sin(5.0*Math.PI)*(Math.pow(input.doubleValueOf(i-1), 0.75) - 0.05))/(Math.log(2.0)*(input.doubleValueOf(i-1)-0.854));
    double exp4=22.5*Math.PI*Math.cos(5.0 * Math.PI *(Math.pow(input.doubleValueOf(i-1), 0.75) - 0.05))*(Math.pow(input.doubleValueOf(i-1), -1.0/4.0));
    result=exp1*exp2*(exp4-exp3);
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
		return 0.01;
	}
}
