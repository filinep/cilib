/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.hpso;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.algorithm.initialisation.HeterogeneousPopulationInitialisationStrategy;
import net.sourceforge.cilib.algorithm.population.IterationStrategy;
import net.sourceforge.cilib.problem.boundaryconstraint.BoundaryConstraint;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.hpso.detectionstrategies.BehaviorChangeTriggerDetectionStrategy;
import net.sourceforge.cilib.pso.hpso.detectionstrategies.PersonalBestStagnationDetectionStrategy;
import net.sourceforge.cilib.pso.iterationstrategies.SynchronousIterationStrategy;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.pso.particle.ParticleBehavior;

public class CyclicBehaviorIterationStrategy implements IterationStrategy<PSO>, HeterogeneousIterationStrategy {
    
    private IterationStrategy<PSO> iterationStrategy;
    private BehaviorChangeTriggerDetectionStrategy detectionStrategy;
    private List<ParticleBehavior> behaviorPool;

    public CyclicBehaviorIterationStrategy() {
        this.iterationStrategy = new SynchronousIterationStrategy();
        this.detectionStrategy = new PersonalBestStagnationDetectionStrategy();
        this.behaviorPool = new ArrayList<>();
    }

    public CyclicBehaviorIterationStrategy(CyclicBehaviorIterationStrategy copy) {
        this.iterationStrategy = copy.iterationStrategy.getClone();
        this.detectionStrategy = copy.detectionStrategy.getClone();
        this.behaviorPool = new ArrayList<>(copy.behaviorPool);
    }

    @Override
    public CyclicBehaviorIterationStrategy getClone() {
        return new CyclicBehaviorIterationStrategy(this);
    }

    @Override
    public void performIteration(PSO algorithm) {
        ParticleBehavior behavior;
        for(Particle p : algorithm.getTopology()) {

            if (detectionStrategy.detect(p)) {
                int behaviorIndex = behaviorPool.indexOf(p.getParticleBehavior());
                if (behaviorIndex == -1) {
                    HeterogeneousPopulationInitialisationStrategy initStrat = (HeterogeneousPopulationInitialisationStrategy) algorithm.getInitialisationStrategy();
                    behaviorIndex  = initStrat.getBehaviorPool().indexOf(p.getParticleBehavior());
                }
                
                behavior = behaviorPool.get((behaviorIndex + 1) % behaviorPool.size());
                behavior.incrementSelectedCounter();
                p.setParticleBehavior(behavior);
            }
        }
        
        iterationStrategy.performIteration(algorithm);
    }

    @Override
    public void addBehavior(ParticleBehavior behavior) {
        behaviorPool.add(behavior);
    }

    @Override
    public void setBehaviorPool(List<ParticleBehavior> pool) {
        behaviorPool = pool;
    }

    @Override
    public List<ParticleBehavior> getBehaviorPool() {
        return behaviorPool;
    }

    @Override
    public List<Double> getSelectionValues() {
        return new ArrayList<>();
    }

    @Override
    public BoundaryConstraint getBoundaryConstraint() {
        return iterationStrategy.getBoundaryConstraint();
    }

    @Override
    public void setBoundaryConstraint(BoundaryConstraint boundaryConstraint) {
        iterationStrategy.setBoundaryConstraint(boundaryConstraint);
    }

}
