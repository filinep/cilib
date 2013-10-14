/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.entity.initialisation;

import fj.P1;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.math.random.ProbabilityDistributionFunction;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.StructuredType;
import net.sourceforge.cilib.type.types.container.Vector;

public class MutatedPBestInitialisationStrategy implements InitialisationStrategy<Particle> {
    
    private ProbabilityDistributionFunction distribution;

    public MutatedPBestInitialisationStrategy() {
        this.distribution = new UniformDistribution(ConstantControlParameter.of(0.0), ConstantControlParameter.of(0.1));
    }

    private MutatedPBestInitialisationStrategy(MutatedPBestInitialisationStrategy copy) {
        this.distribution = copy.distribution;
    }

    @Override
    public MutatedPBestInitialisationStrategy getClone() {
        return new MutatedPBestInitialisationStrategy(this);
    }

    @Override
    public void initialise(Enum<?> key, Particle entity) {
        Vector pbest = (Vector) entity.getProperties().get(EntityType.CANDIDATE_SOLUTION);
        pbest = pbest.plus(Vector.newBuilder().repeat(pbest.size(), Real.valueOf(1.0)).build().multiply(new P1<Number>() {
            @Override
            public Number _1() {
                return distribution.getRandomNumber();
            }
        }));

        entity.calculateFitness();
        
        Entity a = entity.getClone();
        a.getProperties().put(EntityType.Particle.BEST_POSITION, entity.getCandidateSolution().getClone());
        a.getProperties().put(EntityType.Particle.BEST_FITNESS, entity.getFitness().getClone());
        a.setCandidateSolution(pbest);
        a.calculateFitness();
        
        if (a.compareTo(entity) > 0) {
            entity.getProperties().put(EntityType.Particle.BEST_POSITION, pbest);
            entity.getProperties().put(EntityType.Particle.BEST_FITNESS, a.getFitness());
        } else {
            entity.getProperties().put(EntityType.Particle.BEST_POSITION, a.getCandidateSolution());
            entity.getProperties().put(EntityType.Particle.BEST_FITNESS, a.getFitness());
            entity.getProperties().put(EntityType.CANDIDATE_SOLUTION, a.getProperties().get(EntityType.Particle.BEST_POSITION));
            entity.getProperties().put(EntityType.FITNESS, a.getBestFitness());
        }        
        
    }
}
