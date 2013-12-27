/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import com.google.common.base.Preconditions;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * 4 globals peaks
 * 
 */
public class PenHolder extends ContinuousFunction implements Gradient{
    
    
    /**
     * {@inheritDoc}
     */
    
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() >= 2, "PenHolder function is only defined for more than 2 dimensions");
    
        double sum = 0.0;
        double prod=1;
        for (int j = 0; j < input.size() - 1; j++) {
            sum += input.doubleValueOf(j)*input.doubleValueOf(j);
            prod*=Math.cos(input.doubleValueOf(j));
        }
        return -Math.exp(Math.pow(Math.abs(prod*Math.exp(Math.abs(1.0-Math.sqrt(sum)/Math.PI))),-1));
    }
    
    public Double df(Vector input, int i){
    double result=0.0;
    double som=0.0;
    double prod1=1.0;
    double exp1=0.0;
    
    for (int j = 0; j < input.size() - 1; j++) {
            som += input.doubleValueOf(j)*input.doubleValueOf(j);
            prod1 *= Math.cos(input.doubleValueOf(j));
            
        }
    
    exp1 = Math.exp(Math.abs(1-Math.sqrt(som)/Math.PI));
    
    double exp2=-exp1*((Math.sin(input.doubleValueOf(i-1))*prod1)/(Math.cos(input.doubleValueOf(i-1))))-((input.doubleValueOf(i-1)*exp1*prod1*(1-Math.sqrt(som))/Math.PI)/(Math.PI*Math.sqrt(som)*Math.abs(1-Math.sqrt(som)/Math.PI)));
    double num=Math.exp(Math.pow(Math.abs(prod1*exp1),-1))*((prod1)/(Math.abs(prod1)))*exp2;
    double den=Math.pow(Math.abs(exp1*prod1),2);
    
    result=(num)/(den);
    
    return result;
        }
    
    public double getAverageGradientVector ( Vector x)
    {
        
        double sum = 0;
        
        for (int i = 1; i <= x.size(); ++i)
        {
            sum += this.df(x,i);
        }
           
        return sum/x.size();
    }
    
    public double getGradientVectorLength (Vector x)
    {
        double sumsqrt = 0;
        
        for (int i = 1; i <= x.size(); ++i)
        {
            sumsqrt += this.df(x,i)*this.df(x,i);
        }
        
        return Math.sqrt(sumsqrt);
    }
    
    public Vector getGradientVector (Vector x)
    {
        Vector.Builder vectorBuilder = Vector.newBuilder();
        
        for (int i = 1; i <= x.size(); ++i)
        {
             vectorBuilder.add(this.df(x,i));
        }
        
        return vectorBuilder.build();
    }
}
    
