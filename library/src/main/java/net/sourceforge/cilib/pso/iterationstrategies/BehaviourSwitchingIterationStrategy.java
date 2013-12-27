/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.cilib.pso.iterationstrategies;

import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.algorithm.population.IterationStrategy;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.topologies.GBestNeighbourhood;
import net.sourceforge.cilib.entity.topologies.Neighbourhood;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.pso.particle.ParticleBehavior;

/**
 *
 * @author filipe
 */
public class BehaviourSwitchingIterationStrategy extends AbstractIterationStrategy<PSO> {
    
    private IterationStrategy<PSO> delegate = new SynchronousIterationStrategy();
    private IterationStrategy<PSO> newStrategy = new SynchronousIterationStrategy();
    private ControlParameter switchingIteration = ConstantControlParameter.of(250);
    private ParticleBehavior behaviour;
    private Neighbourhood<Particle> neighbourhood = new GBestNeighbourhood<Particle>();

    @Override
    public AbstractIterationStrategy getClone() {
        return this;
    }

    @Override
    public void performIteration(PSO algorithm) {
        if (algorithm.getIterations() == ((int) switchingIteration.getParameter())) {
            for (Particle p : algorithm.getTopology()) {
                p.setParticleBehavior(behaviour);
            }
            algorithm.setNeighbourhood(neighbourhood);
            delegate = newStrategy;
        }
        
        delegate.performIteration(algorithm);
    }

    public void setSwitchingIteration(ControlParameter switchingIteration) {
        this.switchingIteration = switchingIteration;
    }

    public void setDelegate(IterationStrategy<PSO> delegate) {
        this.delegate = delegate;
    }

    public void setBehaviour(ParticleBehavior behaviour) {
        this.behaviour = behaviour;
    }
    
}
