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
package net.sourceforge.cilib.fss.movement.individual;

import fj.P1;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.controlparameter.LinearlyVaryingControlParameter;
import net.sourceforge.cilib.fss.fish.Fish;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.problem.boundaryconstraint.BoundaryConstraint;
import net.sourceforge.cilib.problem.boundaryconstraint.ClampingBoundaryConstraint;
import net.sourceforge.cilib.type.types.container.Vector;

public class StandardIndividualMovementProvider implements IndividualMovementProvider {

    private ControlParameter individualStepSize;
    private BoundaryConstraint boundaryConstraint;
    
    public StandardIndividualMovementProvider() {
        this.individualStepSize = new LinearlyVaryingControlParameter(1.0, 0.00001);
        this.boundaryConstraint = new ClampingBoundaryConstraint();
    }
    
    @Override
    public Fish move(Fish fish) {
        Fish temp = fish.getClone();
        
        final UniformDistribution uniform = new UniformDistribution();
        Vector deltaX = Vector.fill(individualStepSize.getParameter(), temp.getDimension())
            .multiply(new P1<Number>() {
                @Override
                public Number _1() {
                    return uniform.getRandomNumber(-1, 1);
                }                    
            });
        
        temp.setCandidateSolution(((Vector) temp.getCandidateSolution()).plus(deltaX));
        boundaryConstraint.enforce(temp);
        
        temp.calculateFitness();
        
        double deltaF = temp.getFitness().compareTo(fish.getFitness()) * Math.abs(temp.getFitness().getValue() - fish.getFitness().getValue());
        
        if (temp.compareTo(fish) > 0) {
            fish = temp;
        }
        
        fish.setDeltaX(deltaX);
        fish.setDeltaF(deltaF);
        
        return fish;
    }

    public void setIndividualStepSize(ControlParameter individualStepSize) {
        this.individualStepSize = individualStepSize;
    }

    public void setBoundaryConstraint(BoundaryConstraint boundaryConstraint) {
        this.boundaryConstraint = boundaryConstraint;
    }

    public ControlParameter getIndividualStepSize() {
        return individualStepSize;
    }

    public BoundaryConstraint getBoundaryConstraint() {
        return boundaryConstraint;
    }

}
