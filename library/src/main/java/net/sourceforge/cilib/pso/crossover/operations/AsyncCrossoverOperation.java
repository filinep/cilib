/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.crossover.operations;

import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.util.Cloneable;

public interface AsyncCrossoverOperation extends Cloneable {
    
    @Override
    AsyncCrossoverOperation getClone();
    
    Particle async(PSO pso, Particle p);

}
