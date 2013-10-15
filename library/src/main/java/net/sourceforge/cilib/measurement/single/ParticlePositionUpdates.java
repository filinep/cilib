/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import fj.F;
import fj.function.Integers;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.Int;

public class ParticlePositionUpdates implements Measurement<Int> {

    @Override
    public Measurement<Int> getClone() {
        return this;
    }

    @Override
    public Int getValue(Algorithm algorithm) {
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            return Int.valueOf(posUpdateCount((SinglePopulationBasedAlgorithm<Particle>) algorithm));
        } else {
            MultiPopulationBasedAlgorithm psos = (MultiPopulationBasedAlgorithm) algorithm;
            int count = 0;
            for (SinglePopulationBasedAlgorithm sPop : psos.getPopulations()) {
                count += posUpdateCount(sPop);
            }
            
            return Int.valueOf(count);
        }
    }

    private int posUpdateCount(SinglePopulationBasedAlgorithm<Particle> algorithm) {
        return Integers.sum(algorithm.getTopology().map(new F<Particle, Integer>() {
            @Override
            public Integer f(Particle a) {
                return ((Int) a.getProperties().get(EntityType.Particle.Count.POSITION_UPDATE_COUNTER)).intValue();
            }            
        }));
    }
}