/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.initialisation.HeterogeneousPopulationInitialisationStrategy;
import net.sourceforge.cilib.math.Stats;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.hpso.HeterogeneousIterationStrategy;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.pso.particle.ParticleBehavior;
import net.sourceforge.cilib.type.types.Int;
import net.sourceforge.cilib.type.types.container.Vector;


public class HPSOVelocity implements Measurement<Vector> {

    @Override
    public HPSOVelocity getClone() {
        return this;
    }

    @Override
    public Vector getValue(Algorithm algorithm) {
        PSO pso = (PSO) algorithm;
        fj.data.List<Particle> topology = pso.getTopology();
        HeterogeneousIterationStrategy strategy = (HeterogeneousIterationStrategy) pso.getIterationStrategy();
        HeterogeneousPopulationInitialisationStrategy initStrategy = (HeterogeneousPopulationInitialisationStrategy) pso.getInitialisationStrategy();
        List<ParticleBehavior> initialBehaviorPool = initStrategy.getBehaviorPool();
        List<ParticleBehavior> behaviorPool = strategy.getBehaviorPool();

        List<List<Double>> profile = new ArrayList();
        for (int i = 0; i < behaviorPool.size(); i++) {
            profile.add(new ArrayList());
        }

        for (Particle p : topology) {
            for (int i = 0; i < profile.size(); i++) {
                if (p.getParticleBehavior() == behaviorPool.get(i) || p.getParticleBehavior() == initialBehaviorPool.get(i)) {
                    profile.get(i).add(((Vector) p.getCandidateSolution()).subtract((Vector) 
                    p.getProperties().get(net.sourceforge.cilib.entity.EntityType.PREVIOUS_SOLUTION)).norm());
                    break;
                }
            }
        }

        Vector.Builder builder = Vector.newBuilder();
        for (List<Double> l : profile) {
            if (l.isEmpty()) {
                builder.add(0.0);
            } else {
                builder.add(Stats.mean(l));
            }
        }

        return builder.build();
    }
}
