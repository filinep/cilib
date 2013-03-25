/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.iterationstrategies;

import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.crossover.operations.AsyncCrossoverOperation;
import net.sourceforge.cilib.pso.crossover.operations.BoltzmannCrossoverSelection;
import net.sourceforge.cilib.pso.crossover.operations.PSOCrossoverOperation;
import net.sourceforge.cilib.pso.particle.Particle;

/**
 * An iteration strategy that uses different PSOCrossoverOperations to affect the
 * swarm of particles.
 */
public class AsyncPSOCrossoverIterationStrategy extends AbstractIterationStrategy<PSO> {

    private AsyncCrossoverOperation crossoverOperation;

    /**
     * Default constructor
     */
    public AsyncPSOCrossoverIterationStrategy() {
        this.crossoverOperation = new BoltzmannCrossoverSelection();
    }

    /**
     * Copy constructor
     *
     * @param copy
     */
    public AsyncPSOCrossoverIterationStrategy(AsyncPSOCrossoverIterationStrategy copy) {
        this.crossoverOperation = copy.crossoverOperation.getClone();
    }

    /**
     * Clones this instance
     *
     * @return the clone
     */
    @Override
    public AsyncPSOCrossoverIterationStrategy getClone() {
        return new AsyncPSOCrossoverIterationStrategy(this);
    }

    @Override
    public void performIteration(PSO algorithm) {
        Topology<Particle> topology = algorithm.getTopology();

        for (int i = 0; i < topology.size(); i++) {
            Particle current = topology.get(i);
            current.updateVelocity();
            current.updatePosition();

            boundaryConstraint.enforce(current);
            current.calculateFitness();

            topology.set(i, crossoverOperation.async(algorithm, current));

            for (Particle other : topology.neighbourhood(topology.get(i))) {
                if (current.getSocialFitness().compareTo(other.getNeighbourhoodBest().getSocialFitness()) > 0) {
                    other.setNeighbourhoodBest(current);
                }
            }
        }
    }

    public void setCrossoverOperation(AsyncCrossoverOperation crossoverOperation) {
        this.crossoverOperation = crossoverOperation;
    }
}
