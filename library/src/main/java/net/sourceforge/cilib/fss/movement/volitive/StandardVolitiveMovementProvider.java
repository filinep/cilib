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
package net.sourceforge.cilib.fss.movement.volitive;

import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.controlparameter.LinearlyVaryingControlParameter;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.fss.fish.Fish;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.EuclideanDistanceMeasure;

public class StandardVolitiveMovementProvider implements VolitiveMovementProvider {
    
    private ControlParameter volitiveStepSize;
    
    public StandardVolitiveMovementProvider() {
        this.volitiveStepSize = new LinearlyVaryingControlParameter(2.0, 0.00002);
    }

    @Override
    public Vector get(Fish fish, Topology<Fish> school, Vector barycenter, double sumDeltaW) {
        UniformDistribution uniform = new UniformDistribution();
        EuclideanDistanceMeasure distance = new EuclideanDistanceMeasure();
        
        Vector x = (Vector) fish.getCandidateSolution();
        return x.plus(x.subtract(barycenter).divide(distance.distance(x, barycenter))
                .multiply(volitiveStepSize.getParameter())
                .multiply(uniform.getRandomNumber())
                .multiply(sumDeltaW > 0 ? -1 : 1));
    }

    public void setVolitiveStepSize(ControlParameter volitiveStepSize) {
        this.volitiveStepSize = volitiveStepSize;
    }

    public ControlParameter getVolitiveStepSize() {
        return volitiveStepSize;
    }

}
