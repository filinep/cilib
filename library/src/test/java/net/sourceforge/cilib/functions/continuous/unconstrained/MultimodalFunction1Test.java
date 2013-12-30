/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;

public class MultimodalFunction1Test {
    private ContinuousFunction function;

    @Before
    public void instantiate() {
        this.function = new EqualMaxima();
    }

    /**
     * Test of evaluate method, of class {@link MultimodalFunction1}.
     */
    @Test
    public void testApply() {
        Vector x = Vector.of(0.0, 0.0);

        //test minimum
        assertEquals(0.0, function.f(x), 0.0);

        //test another point
        x.setReal(0, 0.1);
        x.setReal(1, 0.5);
        assertEquals(2.0, function.f(x), 0.0);
        
        System.out.println("Five Uneven Peak Trap");
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(2.5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(7.5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(12.5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(17.5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(22.5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(27.5)));
        System.out.println(new FiveUnevenPeakTrap().f(Vector.of(30)));
        
        System.out.println("Equal Maxima");
        System.out.println(new EqualMaxima().f(Vector.of(0.1)) + " " + new EqualMaxima().getGradientVector(Vector.of(0.1)));
        System.out.println(new EqualMaxima().f(Vector.of(0.3)) + " " + new EqualMaxima().getGradientVector(Vector.of(0.3)));
        System.out.println(new EqualMaxima().f(Vector.of(0.5)) + " " + new EqualMaxima().getGradientVector(Vector.of(0.5)));
        System.out.println(new EqualMaxima().f(Vector.of(0.7)) + " " + new EqualMaxima().getGradientVector(Vector.of(0.7)));
        System.out.println(new EqualMaxima().f(Vector.of(0.9)) + " " + new EqualMaxima().getGradientVector(Vector.of(0.9)));
        
        System.out.println("Uneven Decreasing Maxima");
        System.out.println(new UnevenDecreasingMaxima().f(Vector.of(0.08)) + " " + new UnevenDecreasingMaxima().getGradientVector(Vector.of(0.08)));
        System.out.println(new UnevenDecreasingMaxima().f(Vector.of(0.246)) + " " + new UnevenDecreasingMaxima().getGradientVector(Vector.of(0.246)));
        System.out.println(new UnevenDecreasingMaxima().f(Vector.of(0.45)) + " " + new UnevenDecreasingMaxima().getGradientVector(Vector.of(0.45)));
        System.out.println(new UnevenDecreasingMaxima().f(Vector.of(0.68)) + " " + new UnevenDecreasingMaxima().getGradientVector(Vector.of(0.68)));
        System.out.println(new UnevenDecreasingMaxima().f(Vector.of(0.9325)) + " " + new UnevenDecreasingMaxima().getGradientVector(Vector.of(0.9325)));
        
        System.out.println("Himmelblau");
        System.out.println(new Himmelblau().f(Vector.of(3,2)) + " " + new Himmelblau().getGradientVector(Vector.of(3,2)));
        System.out.println(new Himmelblau().f(Vector.of(-2.805118,3.131312)) + " " + new Himmelblau().getGradientVector(Vector.of(-2.805118,3.131312)));
        System.out.println(new Himmelblau().f(Vector.of(-3.779310,-3.283186)) + " " + new Himmelblau().getGradientVector(Vector.of(-3.779310,-3.283186)));
        System.out.println(new Himmelblau().f(Vector.of(3.584428,-1.848126)) + " " + new Himmelblau().getGradientVector(Vector.of(3.584428,-1.848126)));
        
        System.out.println("Six Hump Camel Back");
        System.out.println(new SixHumpCamelBack().f(Vector.of(-0.089842, 0.712656)) + " " + new SixHumpCamelBack().getGradientVector(Vector.of(-0.089842, 0.712656)));
        System.out.println(new SixHumpCamelBack().f(Vector.of(0.089842, -0.712656)) + " " + new SixHumpCamelBack().getGradientVector(Vector.of(0.089842, -0.712656)));

        System.out.println("Shubert");
        System.out.println(new Shubert().f(Vector.of(1,1)) + " " + new Shubert().getGradientVector(Vector.of(1,1)));
        System.out.println(new Shubert().f(Vector.of(-7.5,-7.5)) + " " + new Shubert().getGradientVector(Vector.of(-7.5,-7.5)));
        System.out.println(new Shubert().f(Vector.of(-1.42513,-0.80032)) + " " + new Shubert().getGradientVector(Vector.of(-1.42513,-0.80032)));
        
        System.out.println("Vincent");
        System.out.println(new Vincent().f(Vector.of(9,9)) + " " + new Vincent().getGradientVector(Vector.of(9,9)));
        System.out.println(new Vincent().f(Vector.of(4,8)) + " " + new Vincent().getGradientVector(Vector.of(4,8)));
        
        System.out.println("Rastrigin");
        System.out.println(new RastriginNiching().f(Vector.of(0.15,0.1)) + " " + new RastriginNiching().getGradientVector(Vector.of(0.15,0.1)));
    }
}
