/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple.filter;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.util.functions.Algorithms;
import fj.data.List;
import fj.Equal;
import fj.F2;

public class NBestNicheProvider implements NicheProvider<Particle> {

    public List<List<Particle>> getPopulations(Algorithm a) {
        SinglePopulationBasedAlgorithm<Particle> pba = (SinglePopulationBasedAlgorithm<Particle>) a;
        return pba.getTopology().group(
            Equal.<Particle>equal(new F2<Particle, Particle, Boolean>() {
                    @Override
                    public Boolean f(Particle p1, Particle p2) {
                        return p1.getNeighbourhoodBest().equals(p2.getNeighbourhoodBest());
                    }
                }.curry())
            );
    }
}
