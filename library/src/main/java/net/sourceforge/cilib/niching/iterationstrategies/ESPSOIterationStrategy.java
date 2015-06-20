/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.niching.iterationstrategies;

import static net.sourceforge.cilib.niching.NichingFunctions.createNiches;
import static net.sourceforge.cilib.niching.NichingSwarms.onMainSwarm;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.niching.NichingSwarms;

import com.google.common.collect.Lists;

import fj.P2;
import fj.data.List;

public class ESPSOIterationStrategy extends AbstractIterationStrategy<NichingAlgorithm> {

    public ESPSOIterationStrategy() {
    }

    public ESPSOIterationStrategy(ESPSOIterationStrategy copy) {
    }

    @Override
    public ESPSOIterationStrategy getClone() {
        return new ESPSOIterationStrategy(this);
    }

    @Override
    public void performIteration(NichingAlgorithm alg) {
        P2<SinglePopulationBasedAlgorithm, List<SinglePopulationBasedAlgorithm>> newSwarms =
            onMainSwarm(alg.getMainSwarmIterator())
            .andThen(alg.getSubSwarmIterator())
            .andThen(createNiches(alg.getNicheDetector(),
                                  alg.getNicheCreator(),
                         alg.getMainSwarmCreationMerger()))
            .f(NichingSwarms.of(alg.getMainSwarm(), alg.getPopulations()));

        alg.setPopulations(Lists.newArrayList(newSwarms._2().toCollection()));
        alg.setMainSwarm(newSwarms._1());
    }
}
