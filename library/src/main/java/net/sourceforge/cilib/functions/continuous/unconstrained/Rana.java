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
 *
 * 
 */
public class Rana extends ContinuousFunction implements Gradient{
    
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() >= 2, "Rana function is only defined for 2 or more dimensions");

        double sum = 0.0;
        double a=0.0;
        double b=0.0;
        
        
        for (int i = 0; i < input.size() - 1; i++) {
            a = Math.sqrt(Math.abs(input.doubleValueOf(i+1)+1-input.doubleValueOf(i)));
            b = Math.sqrt(Math.abs(input.doubleValueOf(i+1)+1+input.doubleValueOf(i)));
            
            sum += (input.doubleValueOf(i)*Math.sin(a)*Math.cos(b)
                    + (input.doubleValueOf(i+1)+1)*Math.cos(a)*Math.sin(b));
        }
        return sum;
    }
    
    
    public Double df(Vector input, int i)
    {
        Preconditions.checkArgument(input.size() > 0, "Rana function is defined for 1 dimension at least");
        
        double res = 0.0;
        int n = input.size();
        
        if (i == 1)
        {
            double a = 1 - input.doubleValueOf(0)+input.doubleValueOf(1);
            double b = 1 + input.doubleValueOf(0)+input.doubleValueOf(1);
            double sina = Math.sin(Math.sqrt(Math.abs(a)));
            double sinb = Math.sin(Math.sqrt(Math.abs(b)));
            double cosa = Math.cos(Math.sqrt(Math.abs(a)));
            double cosb = Math.cos(Math.sqrt(Math.abs(b)));
            
            res = cosb*sina+(a/Math.abs(a))*((1+input.doubleValueOf(1))*sina*sinb-input.doubleValueOf(0)*cosa*cosb)/(2.0*Math.sqrt(Math.abs(a)))+(b/Math.abs(b))*((1+input.doubleValueOf(1))*cosa*cosb-input.doubleValueOf(0)*sina*sinb)/(2.0*Math.sqrt(Math.abs(b)));
        }
        else if (i == n)
        {
            double a = 1 - input.doubleValueOf(n-2)+input.doubleValueOf(n-1);
            double b = 1 + input.doubleValueOf(n-2)+input.doubleValueOf(n-1);
            double sina = Math.sin(Math.sqrt(Math.abs(a)));
            double sinb = Math.sin(Math.sqrt(Math.abs(b)));
            double cosa = Math.cos(Math.sqrt(Math.abs(a)));
            double cosb = Math.cos(Math.sqrt(Math.abs(b)));
            
            res = cosa*sinb+(a/Math.abs(a))*(input.doubleValueOf(n-2)*cosa*cosb-(1+input.doubleValueOf(n-1))*sina*sinb)/(2.0*Math.sqrt(Math.abs(a)))+(b/Math.abs(b))*((1+input.doubleValueOf(n-1))*cosa*cosb-input.doubleValueOf(n-2)*sina*sinb)/(2.0*Math.sqrt(Math.abs(b)));
        }
        else
        {
            double a = 1 - input.doubleValueOf(i-2)+input.doubleValueOf(i-1);
            double b = 1 + input.doubleValueOf(i-2)+input.doubleValueOf(i-1);
            double sina = Math.sin(Math.sqrt(Math.abs(a)));
            double sinb = Math.sin(Math.sqrt(Math.abs(b)));
            double cosa = Math.cos(Math.sqrt(Math.abs(a)));
            double cosb = Math.cos(Math.sqrt(Math.abs(b)));
            
            double ap1 = 1 - input.doubleValueOf(i-1)+input.doubleValueOf(i);
            double bp1 = 1 + input.doubleValueOf(i-1)+input.doubleValueOf(i);
            double sinap1 = Math.sin(Math.sqrt(Math.abs(ap1)));
            double sinbp1 = Math.sin(Math.sqrt(Math.abs(bp1)));
            double cosap1 = Math.cos(Math.sqrt(Math.abs(ap1)));
            double cosbp1 = Math.cos(Math.sqrt(Math.abs(bp1)));
            
            
            res = cosa*sinb+cosbp1*sinap1+(a/Math.abs(a))*(input.doubleValueOf(i-2)*cosa*cosb-(1+input.doubleValueOf(i-1))*sina*sinb)/(2.0*Math.sqrt(Math.abs(a)))+(b/Math.abs(b))*((1+input.doubleValueOf(i-1))*cosa*cosb-input.doubleValueOf(i-2)*sina*sinb)/(2.0*Math.sqrt(Math.abs(b)))+(ap1/Math.abs(ap1))*((1+input.doubleValueOf(i))*sinap1*sinbp1-input.doubleValueOf(i-1)*cosap1*cosbp1)/(2.0*Math.sqrt(Math.abs(ap1)))+(bp1/Math.abs(bp1))*((1+input.doubleValueOf(i))*cosap1*cosbp1-input.doubleValueOf(i-1)*sinap1*sinbp1)/(2.0*Math.sqrt(Math.abs(bp1)));
        }
        
        return res;
        
        
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
