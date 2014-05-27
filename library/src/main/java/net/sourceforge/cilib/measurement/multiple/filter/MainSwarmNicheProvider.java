/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple.filter;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.pso.particle.Particle;
import fj.data.List;
import fj.F;

public class MainSwarmNicheProvider implements NicheProvider<Entity> {

    public List<List<Entity>> getPopulations(Algorithm a) {
        NichingAlgorithm mpba = (NichingAlgorithm) a;
        List<? extends Entity> main = mpba.getMainSwarm().getTopology();
        return List.list((List<Entity>) main)
            .append(new SubswarmNicheProvider().getPopulations(a));
    }
}
