/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.niching.iterationstrategies;

import com.google.common.collect.Lists;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import static net.sourceforge.cilib.niching.NichingFunctions.createNiches;
import net.sourceforge.cilib.niching.NichingSwarms;

/**
 *
 */
public class VectorBasedPSO extends AbstractIterationStrategy<NichingAlgorithm> {

    @Override
    public AbstractIterationStrategy<NichingAlgorithm> getClone() {
        return this;
    }

    @Override
    public void performIteration(NichingAlgorithm alg) {
        System.out.print(alg.getMainSwarm().getTopology().length() + " ");
        NichingSwarms newSwarms = createNiches(alg.getNicheDetector(), 
                alg.getNicheCreator(),
                alg.getMainSwarmCreationMerger())
            .andThen(alg.getSubSwarmIterator())
            .f(NichingSwarms.of(alg.getMainSwarm(), alg.getPopulations()));
        System.out.print(new net.sourceforge.cilib.measurement.single.ParticlePositionUpdates().getValue(alg) + " ");
        System.out.println(alg.getMainSwarm().getTopology().length());

        //System.out.print('2');
        alg.setPopulations(Lists.newArrayList(newSwarms._2().toCollection()));
        alg.setMainSwarm(newSwarms._1());
        //System.out.println('3');
    }

}
