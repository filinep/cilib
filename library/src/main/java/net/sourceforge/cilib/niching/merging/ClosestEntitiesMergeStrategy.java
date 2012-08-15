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
package net.sourceforge.cilib.niching.merging;

import net.sourceforge.cilib.algorithm.population.PopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.Particle;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.entity.comparator.SocialBestFitnessComparator;
import net.sourceforge.cilib.pso.particle.ParticleBehavior;
import net.sourceforge.cilib.util.DistanceMeasure;
import net.sourceforge.cilib.util.EuclideanDistanceMeasure;

/**
 * Takes the entities from the worst sub-swarm which are closest to the gbest of 
 * the best sub-swarm and puts them in the best sub-swarm.
 */
public class ClosestEntitiesMergeStrategy extends MergeStrategy {
    
    private ControlParameter granularity;
    private DistanceMeasure distanceMeasure;
    
    /**
     * Default constructor.
     */
    public ClosestEntitiesMergeStrategy() {
        this.granularity = ConstantControlParameter.of(0.5);
        this.distanceMeasure = new EuclideanDistanceMeasure();
    }
    
    @Override
    public PopulationBasedAlgorithm f(PopulationBasedAlgorithm subSwarm1, PopulationBasedAlgorithm subSwarm2) {
        PopulationBasedAlgorithm bestSwarm;
        PopulationBasedAlgorithm worstSwarm;
        
        if (subSwarm1.getBestSolution().compareTo(subSwarm2.getBestSolution()) > 0) {
            bestSwarm = subSwarm1;
            worstSwarm = subSwarm2;
        } else {
            bestSwarm = subSwarm2;
            worstSwarm = subSwarm1;
        }
        
        PopulationBasedAlgorithm newSwarm = bestSwarm.getClone();
        Particle gBest = (Particle) Topologies.getBestEntity(newSwarm.getTopology(), new SocialBestFitnessComparator());
        
        for (Entity e : worstSwarm.getTopology()) {
            if (distanceMeasure.distance(e.getCandidateSolution(), gBest.getBestPosition()) < granularity.getParameter()) {
                ((Topology<Entity>) newSwarm.getTopology()).add(e);
                worstSwarm.getTopology().remove(e);
            }
        }

        Particle p;
        if (!newSwarm.getTopology().isEmpty() && (p = (Particle) newSwarm.getTopology().get(0)) instanceof Particle) {
            ParticleBehavior pb = p.getParticleBehavior();
            for (Entity e : newSwarm.getTopology()) {
                ((Particle) e).setParticleBehavior(pb);
            }
        }

        return newSwarm;
    }
    
    /**
     * Get the merge threshold value.
     * 
     * @return The value of the merge threshold.
     */
    public ControlParameter getGranularity() {
        return granularity;
    }

    /**
     * Set the merge threshold value.
     * 
     * @param threshold The value to set.
     */
    public void setGranularity(ControlParameter threshold) {
        this.granularity = threshold;
    }

    public DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }

    public void setDistanceMeasure(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
}
