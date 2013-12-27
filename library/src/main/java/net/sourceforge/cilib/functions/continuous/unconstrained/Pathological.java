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
 * The Pathological function.
 *
 * <p>Title: CILib</p>
 * <p>Description: CILib (Computational Intelligence Library)</p>
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * Characteristics:
 * <ul>
 * <li>Only defined for more than or equal to 2 dimensions</li>
 * <li>Multimodal</li>
 * <li>Continuous</li>
 * </ul>
 *
 * R(-11,11) ^ n
 *
 * @version 1.0
 */
public class Pathological extends ContinuousFunction implements Gradient{

    private static final long serialVersionUID = 7323733640884766707L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() >= 2, "Pathological function is only defined for more than 2 dimensions");
        double sum = 0.0;
        double num = 0.0;
        double denom = 0.0;
        double n = input.size();
        for (int i = 1 ; i <= n-1 ; ++i)
        {
            num = Math.pow(Math.sin(Math.sqrt(100.0*(Math.pow(input.doubleValueOf(i),2)))+Math.pow(input.doubleValueOf(i-1), 2)),2)-0.5;
            denom = 0.5+(0.001*Math.pow(input.doubleValueOf(i-1)-input.doubleValueOf(i), 4));
            sum += num/denom;
        }
        return sum;
    }
    
    
    public Double df(Vector input, int i){
    
        double result=0.0;
    
    
    if (i==1) {
        double x1 = input.doubleValueOf(0);
        double x2 = input.doubleValueOf(1);
        
        double exp1= ((-0.5+Math.pow(Math.sin(Math.sqrt(100.0*x2*x2+x1*x1)),2))*Math.pow(x1-x2,3))/(250.0*Math.pow(0.5+Math.pow(x1-x2,4)/1000.0,2));
        double exp2= (2.0*x1*Math.cos(Math.sqrt(x1*x1+100.0*x2*x2))*Math.sin(Math.sqrt(x1*x1+100.0*x2*x2)))/(Math.sqrt(100.0*x2*x2+x1*x1)*(0.5+Math.pow(x1-x2,4)/1000.0));
        
        result= -exp1 + exp2;
    }
    
    else if(i==input.size()) {
        double xn1 = input.doubleValueOf(input.size()-2);
        double xn = input.doubleValueOf(input.size()-1);
        
        double e1= ((-0.5+Math.pow(Math.sin(Math.sqrt(100.0*xn*xn+xn1*xn1)),2))*Math.pow(xn1-xn,3))/(250.0*Math.pow(0.5+Math.pow(xn1-xn,4)/1000.0,2));
        double e2= (200.0*xn*Math.cos(Math.sqrt(xn1*xn1+100.0*xn*xn))*Math.sin(Math.sqrt(xn1*xn1+100.0*xn*xn)))/(Math.sqrt(100.0*xn*xn+xn1*xn1)*(0.5+Math.pow(xn1-xn,4)/1000.0));
        
        result= e1+e2;
        
    }
    
    else {
        
        double xim = input.doubleValueOf(i-2);
        double xi = input.doubleValueOf(i-1);
        double xip = input.doubleValueOf(i);
        
        double ex1=((-0.5+Math.pow(Math.sin(Math.sqrt(100.0*xi*xi+xim*xim)),2))*Math.pow(xim-xi,3))/(250.0*Math.pow(0.5+Math.pow(xim-xi,4)/1000.0,2));
        double ex2=(200.0*xi*Math.cos(Math.sqrt(xim*xim+100.0*xi*xi))*Math.sin(Math.sqrt(xim*xim+100.0*xi*xi)))/(Math.sqrt(100.0*xi*xi+xim*xim)*(0.5+Math.pow(xim-xi,4)/1000.0));
        double ex3=((-0.5+Math.pow(Math.sin(Math.sqrt(100.0*xip*xip+xi*xi)),2))*Math.pow(xi-xip,3))/(250.0*Math.pow(0.5+Math.pow(xi-xip,4)/1000.0,2));
        double ex4=(2.0*xi*Math.cos(Math.sqrt(xi*xi+100.0*xip*xip))*Math.sin(Math.sqrt(xi*xi+100.0*xip*xip)))/(Math.sqrt(100.0*xip*xip+xi*xi)*(0.5+Math.pow(xi-xip,4)/1000.0));
        
        result= ex1+ex2-ex3+ex4;
        
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
