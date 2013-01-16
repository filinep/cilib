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
package net.sourceforge.cilib.fss.iterationstrategies;

import java.util.Arrays;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.fss.FSS;
import net.sourceforge.cilib.fss.comparator.FitnessGainComparator;
import net.sourceforge.cilib.fss.fish.Fish;
import net.sourceforge.cilib.fss.movement.instinctive.InstinctiveMovementProvider;
import net.sourceforge.cilib.fss.movement.instinctive.StandardInstinctiveMovementProvider;
import net.sourceforge.cilib.fss.movement.volitive.StandardVolitiveMovementProvider;
import net.sourceforge.cilib.fss.movement.volitive.VolitiveMovementProvider;
import net.sourceforge.cilib.measurement.single.fss.Barycenter;
import net.sourceforge.cilib.problem.boundaryconstraint.ClampingBoundaryConstraint;
import net.sourceforge.cilib.type.types.container.Vector;

public class FSSIterationStrategy extends AbstractIterationStrategy<FSS> {
    
    private InstinctiveMovementProvider instinctiveMovement;
    private VolitiveMovementProvider volitiveMovement;
    
    public FSSIterationStrategy() {
        this.instinctiveMovement = new StandardInstinctiveMovementProvider();
        this.volitiveMovement = new StandardVolitiveMovementProvider();
        this.boundaryConstraint = new ClampingBoundaryConstraint();
    }
    
    public FSSIterationStrategy(FSSIterationStrategy copy) {
        super(copy);
        this.instinctiveMovement = copy.instinctiveMovement;
        this.volitiveMovement = copy.volitiveMovement;
    }

    @Override
    public FSSIterationStrategy getClone() {
        return new FSSIterationStrategy(this);
    }

    @Override
    public void performIteration(FSS algorithm) {
        Topology<Fish> school = algorithm.getTopology();
        
        for (int i = 0; i < school.size(); i++) {
            Fish f = school.get(i);
            school.set(i, f.getIndividualMovement().move(f));
        }

        // max delta f normalization: TODO: Move to a strategy
        Fish[] s = school.getClone().toArray(new Fish[]{});
        Arrays.sort(s, new FitnessGainComparator());        
        double maxDeltaF = Math.max(Math.abs(s[0].getDeltaF()), Math.abs(s[s.length - 1].getDeltaF()));

        for (Fish f : school) {
            f.getFeedingStrategy().feed(f, school, maxDeltaF);
        }
        
        Vector collectiveInstinctiveMovement = instinctiveMovement.get(school);
        for (Fish f : school) {
            Vector x = (Vector) f.getCandidateSolution();
            x = x.plus(collectiveInstinctiveMovement);
            f.setCandidateSolution(x);
        }
        
        // barycenter calculation
        Vector barycenter = Barycenter.calculate(school);
        
        double sumDeltaW = 0.0;
        for (Fish f : school) {
            sumDeltaW += f.getDeltaW();
        }

        for (Fish f : school) {
            Vector x = volitiveMovement.get(f, school, barycenter, sumDeltaW);
            f.setCandidateSolution(x);
            boundaryConstraint.enforce(f);
            f.calculateFitness();
        }
    }

    public void setInstinctiveMovement(InstinctiveMovementProvider instinctiveMovement) {
        this.instinctiveMovement = instinctiveMovement;
    }

    public InstinctiveMovementProvider getInstinctiveMovement() {
        return instinctiveMovement;
    }
    
    public void setVolitiveMovement(VolitiveMovementProvider volitiveMovement) {
        this.volitiveMovement = volitiveMovement;
    }

    public VolitiveMovementProvider getVolitiveMovement() {
        return volitiveMovement;
    }
    
}
