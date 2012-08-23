/**
 * Computational Intelligence Library (CIlib)
 * Copyright (C) 2003 - 2010
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.cilib.niching.iterationstrategies;

import net.sourceforge.cilib.algorithm.population.PopulationBasedAlgorithm;
import net.sourceforge.cilib.type.types.Int;
import net.sourceforge.cilib.entity.EntityType;
import com.google.common.base.Supplier;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.DistanceMeasure;
import net.sourceforge.cilib.entity.visitor.RadiusVisitor;
import fj.Equal;
import com.google.common.collect.Lists;
import fj.F;
import fj.Ord;
import fj.data.List;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.Particle;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.entity.comparator.SocialBestFitnessComparator;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import static net.sourceforge.cilib.niching.NichingFunctions.*;
import net.sourceforge.cilib.niching.NichingSwarms;
import net.sourceforge.cilib.pso.dynamic.detectionstrategies.EnvironmentChangeDetectionStrategy;
import net.sourceforge.cilib.pso.dynamic.detectionstrategies.PeriodicDetectionStrategy;
import net.sourceforge.cilib.util.EuclideanDistanceMeasure;
import net.sourceforge.cilib.util.functions.Algorithms;
import static net.sourceforge.cilib.niching.creation.VectorBasedNicheCreationStrategy.*;

public class DynamicVectorBasedPSO extends AbstractIterationStrategy<NichingAlgorithm> {
    
    private EnvironmentChangeDetectionStrategy environmentChangeDetection;
    private DistanceMeasure distanceMeasure;
    private ControlParameter minSwarmSize;
    
    public DynamicVectorBasedPSO() {
        this.environmentChangeDetection = new PeriodicDetectionStrategy();
        this.distanceMeasure = new EuclideanDistanceMeasure();
        this.minSwarmSize = ConstantControlParameter.of(3.0);
    }

    @Override
    public DynamicVectorBasedPSO getClone() {
        return this;
    }
    
    private static F<Topology<? extends Entity>, Entity> getNicheBests = 
        new F<Topology<? extends Entity>, Entity>() {
            @Override
            public Entity f(Topology<? extends Entity> a) {
                return Topologies.getBestEntity(a, new SocialBestFitnessComparator());
            }                
        };

    @Override
    public void performIteration(NichingAlgorithm alg) {
        if (this.environmentChangeDetection.detect(alg)) {
            final int swarmSize = alg.getMainSwarm().getInitialisationStrategy().getEntityNumber();
            
            // reinitialize swarm
            alg.performInitialisation();
            
            // get list of nbests
            List<Entity> subSwarms = List.iterableList(alg.getPopulations())
                .map(Algorithms.getTopology().andThen(getNicheBests));
            
            // make sure there are still same number of entities as when started
            Topology<Entity> mainSwarm = (Topology<Entity>) alg.getMainSwarm().getTopology();
            mainSwarm.removeAll(mainSwarm.subList(0, swarmSize - subSwarms.length()));
            
            for (int i = 0; i < subSwarms.length(); i++) {
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                Particle gBest = (Particle) subSwarms.index(i);
                List<Particle> newTopology = List.list(gBest);
                List<Particle> swarm = ((List<Particle>) topologyProvider.f(swarms)).delete(gBest, Equal.equal(equalParticle.curry()));

                RadiusVisitor visitor = new RadiusVisitor();
                visitor.visit(swarms.getMainSwarm().getTopology());
                double nRadius = visitor.getResult();

                // get closest particle with dot < 0
                List<Particle> filteredSwarm = swarm.filter(dot(gBest).andThen(ltZero));
                if(!filteredSwarm.isEmpty()) {
                    Particle closest = filteredSwarm.minimum(Ord.ord(sortByDistance(gBest, distanceMeasure).curry()));
                    nRadius = distanceMeasure.distance(closest.getCandidateSolution(), gBest.getCandidateSolution());
                    newTopology = newTopology.append(swarm.filter(filter(distanceMeasure, gBest, nRadius)));
                }

                // to prevent new particles from having the same position as the gBest
                if (nRadius == 0) {
                    nRadius = ((Vector) gBest.getCandidateSolution()).get(0).getBounds().getUpperBound();
                }

                // Add particles until the swarm has at least 3 particles
                final double nicheRadius = nRadius;
                final UniformDistribution uniform = new UniformDistribution();
                int extras = (int) minSwarmSize.getParameter() - newTopology.length();

                for (int i = 0; i < extras; i++) {
                    Particle newP = gBest.getClone();

                    // new position within the niche
                    Vector solution = (Vector) newP.getCandidateSolution();
                    solution = solution.multiply(new Supplier<Number>() {
                        @Override
                        public Number get() {
                            return uniform.getRandomNumber(-nicheRadius, nicheRadius);
                        }
                    }).plus((Vector) gBest.getCandidateSolution());

                    newP.setCandidateSolution(solution);
                    newP.getProperties().put(EntityType.Coevolution.POPULATION_ID, Int.valueOf(swarms.getSubswarms().length() + 1));
                    newTopology = newTopology.cons(newP);
                }

                // Create the new subswarm, set its optimisation problem, add the particles to it
                PopulationBasedAlgorithm newSubswarm = swarmType.getClone();
                newSubswarm.setOptimisationProblem(alg.getMainSwarm().getOptimisationProblem());
                newSubswarm.getTopology().clear();
                ((Topology<Particle>) newSubswarm.getTopology()).addAll(newTopology.toCollection());

                // Remove the subswarms particles from the main swarm
                PopulationBasedAlgorithm newMainSwarm = alg.getMainSwarm().getMainSwarm().getClone();
                newMainSwarm.getTopology().clear();
                for(Entity e : alg.getMainSwarm().getTopology()) {
                    Particle p = (Particle) e;

                    if (!newTopology.exists(equalParticle.f(p))) {
                        ((Topology<Entity>) newMainSwarm.getTopology()).add(e.getClone());
                    }
                }

                // Set the subswarm's behavior and return the new swarms
                return NichingSwarms.of(newMainSwarm, swarms.getSubswarms().cons(Populations.enforceTopology(swarmBehavior).f(newSubswarm.getClone())));
                
                
                
                
                
                
                
                
                
                
                
                
                
                
            }
        }
        
        NichingSwarms newSwarms = createNiches(alg.getNicheDetector(), 
                alg.getNicheCreator(),
                alg.getMainSwarmCreationMerger())
            .andThen(alg.getSubSwarmIterator())
            .andThen(merge(alg.getMergeDetector(),
                alg.getMainSwarmMerger(), 
                alg.getSubSwarmMerger()))
            .f(NichingSwarms.of(alg.getMainSwarm(), alg.getPopulations()));

        alg.setPopulations(Lists.newArrayList(newSwarms._2().toCollection()));
        alg.setMainSwarm(newSwarms._1());
    }

}
