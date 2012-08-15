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

import com.google.common.collect.Lists;
import fj.F;
import fj.data.List;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.SocialEntity;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.entity.comparator.SocialBestFitnessComparator;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import static net.sourceforge.cilib.niching.NichingFunctions.*;
import net.sourceforge.cilib.niching.NichingSwarms;
import net.sourceforge.cilib.pso.dynamic.detectionstrategies.EnvironmentChangeDetectionStrategy;
import net.sourceforge.cilib.pso.dynamic.detectionstrategies.PeriodicDetectionStrategy;
import net.sourceforge.cilib.util.functions.Algorithms;

public class DynamicVectorBasedPSO extends AbstractIterationStrategy<NichingAlgorithm> {
    
    private EnvironmentChangeDetectionStrategy environmentChangeDetection;
    
    public DynamicVectorBasedPSO() {
        this.environmentChangeDetection = new PeriodicDetectionStrategy();
    }

    @Override
    public DynamicVectorBasedPSO getClone() {
        return this;
    }

    @Override
    public void performIteration(NichingAlgorithm alg) {
        if (this.environmentChangeDetection.detect(alg)) {
            List<Entity> subSwarms = List.iterableList(alg.getPopulations()).map(Algorithms.getTopology().andThen(new F<Topology<? extends Entity>, Entity>() {
                @Override
                public Entity f(Topology<? extends Entity> a) {
                    return Topologies.getBestEntity(a, new SocialBestFitnessComparator());
                }                
            }));
            
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
