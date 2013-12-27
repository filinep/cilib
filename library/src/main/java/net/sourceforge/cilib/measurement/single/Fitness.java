/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.entity.Topologies;
import java.util.Comparator;

/**
 *
 */
public class Fitness implements Measurement<Real> {
    private static final long serialVersionUID = 4152219744331703008L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Fitness getClone() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Real getValue(Algorithm algorithm) {
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            double fitness = algorithm.getBestSolution().getFitness().getValue();
            return Real.valueOf(fitness);
        }

        fj.data.List<? extends Entity> topology;
        NichingAlgorithm na = (NichingAlgorithm) algorithm;
        topology = na.getMainSwarm().getTopology();
        for (SinglePopulationBasedAlgorithm s : na.getPopulations()) {
            topology = topology.append(s.getTopology());
        }

        Entity e = Topologies.getBestEntity(topology, new Comparator<Entity>() {
            public int compare(Entity e1, Entity e2) {
                return e1.getBestFitness().compareTo(e2.getBestFitness());
            }
        });

        return Real.valueOf(e.getBestFitness().getValue());
    }
}
