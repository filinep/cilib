/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

import com.google.common.base.Preconditions;
import net.sourceforge.cilib.functions.Gradient;

/**
 * <p><b>The Egg Holder.</b></p>
 *
 * <p><b>Reference:</b> S.K. Mishra, <i>Some New Test Functions
 * for Global Optimization and Performance of Repulsive Particle
 * Swarm Methods</i>, Technical Report, North-Eastern Hill University,
 * India, 2006</p>
 *
 * <p>Note: n >= 2</p>
 *
 * <p>Minimum:
 * <ul>
 * <li> f(<b>x</b>*) approx -959.64 </li>
 * <li> <b>x</b>* = (512,404.2319) for n=2</li>
 * <li> for x_i in [-512,512]</li>
 * </ul>
 * </p>
 *
 * <p>Characteristics:
 * <ul>
 * <li>Only defined for 2+ dimensions</li>
 * <li>Multimodal</li>
 * <li>Non-separable</li>
 * <li>Not regular</li>
 * </ul>
 * </p>
 *
 * R(-512.0,512.0)^30
 *
 */
public class EggHolder extends ContinuousFunction implements Gradient {

    private static final long serialVersionUID = 358993985066821115L;

    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() >= 2, "EggHolder function is only defined for 2 or more dimensions");

        double sum = 0.0;
        for (int j = 0; j < input.size() - 1; j++) {
            sum += (-1*(input.doubleValueOf(j+1) + 47.0)
                    *Math.sin(Math.sqrt(Math.abs(input.doubleValueOf(j+1) + input.doubleValueOf(j)/2.0 + 47.0)))
                    + Math.sin(Math.sqrt(Math.abs(input.doubleValueOf(j) - (input.doubleValueOf(j+1)+47.0))))
                    *(-1.0*input.doubleValueOf(j)));
        }
        return sum;
    }
    
    public Double df(Vector input, int i){
    double result=0.0;
    
    
    if (i==1) {
        double x1 = input.doubleValueOf(0);
        double x2 = input.doubleValueOf(1);
        
        double exp1=(x1*(-47.0+x1-x2)*Math.cos(Math.sqrt(Math.abs(-47.0+x1-x2))))/(2.0*Math.sqrt(Math.abs(-47.0+x1-x2))*(Math.abs(-47.0+x1-x2)));
        double exp2= ((47.0+x1/2.0+x2)*(-47.0-x2)*Math.cos(Math.sqrt(Math.abs(47.0+x1/2.0+x2))))/(4.0*(Math.sqrt(Math.abs(47.0+x1/2.0+x2)))*(Math.abs(47.0+x1/2.0+x2)));
        
        result= -Math.sin(Math.sqrt(Math.abs(-47.0+x1-x2)))-exp1+exp2;
    }
    
    else if(i==input.size()) {
        double xn1 = input.doubleValueOf(input.size()-2);
        double xn = input.doubleValueOf(input.size()-1);
        
        double e1=(xn1*(-47.0+xn1-xn)*Math.cos(Math.sqrt(Math.abs(-47.0+xn1-xn))))/(2.0*Math.sqrt(Math.abs(-47.0+xn1-xn))*(Math.abs(-47.0+xn1-xn)));
        double e2= ((47.0+xn1/2.0+xn)*(-47.0-xn)*Math.cos(Math.sqrt(Math.abs(47.0+xn1/2.0+xn))))/(2.0*(Math.sqrt(Math.abs(47.0+xn1/2.0+xn)))*(Math.abs(47.0+xn1/2.0+xn)));
        
        result= -Math.sin(Math.sqrt(Math.abs(47.0+xn1/2.0+xn)))+e1+e2;
        
    }
    
    else {
        
        double xim = input.doubleValueOf(i-2);
        double xi = input.doubleValueOf(i-1);
        double xip = input.doubleValueOf(i);
        
        double ex1=(xim*(-47.0+xim-xi)*Math.cos(Math.sqrt(Math.abs(-47.0+xim-xi))))/(2.0*Math.sqrt(Math.abs(-47.0+xim-xi))*(Math.abs(-47.0+xim-xi)));
        double ex2=((47.0+xim/2.0+xi)*(-47.0-xi)*Math.cos(Math.sqrt(Math.abs(47.0+xim/2.0+xi))))/(2.0*(Math.sqrt(Math.abs(47.0+xim/2.0+xi)))*(Math.abs(47.0+xim/2.0+xi)));
        double ex3=((-47.0+xi-xip)*(xi)*Math.cos(Math.sqrt(Math.abs(-47.0+xi-xip))))/(2.0*(Math.sqrt(Math.abs(-47.0+xi-xip)))*(Math.abs(-47.0+xi-xip)));
        double ex4=((47.0+xi/2.0+xip)*(-47.0-xip)*Math.cos(Math.sqrt(Math.abs(47.0+xi/2.0+xip))))/(4.0*(Math.sqrt(Math.abs(47.0+xi/2.0+xip)))*(Math.abs(47.0+xi/2.0+xip)));
        
        result= -Math.sin(Math.sqrt(Math.abs(47+xim/2+xi)))-Math.sin(Math.sqrt(Math.abs(-47+xi-xip)))+ex1+ex2+ex3+ex4;
        
    }
    
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
