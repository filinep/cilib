/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple.filter;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.util.functions.Algorithms;
import fj.data.List;
import fj.F;

public class SubswarmNicheProvider implements NicheProvider<Entity> {

    public List<List<Entity>> getPopulations(Algorithm a) {
        MultiPopulationBasedAlgorithm mpba = (MultiPopulationBasedAlgorithm) a;
        return List.iterableList(mpba.getPopulations())
            .map(new F<SinglePopulationBasedAlgorithm, List<Entity>>() {
                    @Override
                    public List<Entity> f(SinglePopulationBasedAlgorithm s) {
                        return s.getTopology();
                    }
                });
    }
}
