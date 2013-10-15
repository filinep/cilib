/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.problem.solution;

import net.sourceforge.cilib.type.types.Type;

/**
 *
 * @author florent
 */
public interface GradientAverage extends Type, Comparable<GradientAverage> {

    /**
     * {@inheritDoc}
     */
    @Override
    GradientAverage getClone();

    /**
     * 
     * @
     */
    Double getValue();

    /**
     * 
     * 
     * @param value The desired value of the {@code GetGradientAverage} object.
     */
    GradientAverage newInstance(Double value);

}
