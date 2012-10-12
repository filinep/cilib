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
package net.sourceforge.cilib.measurement.single.fss;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.fss.FSS;
import net.sourceforge.cilib.fss.fish.Fish;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.solution.Fitness;
import net.sourceforge.cilib.type.types.container.Vector;

public class Barycenter implements Measurement<Vector> {

    @Override
    public Barycenter getClone() {
        return this;
    }

    @Override
    public Vector getValue(Algorithm algorithm) {
        Topology<Fish> school = ((FSS) algorithm).getTopology();
        double sumWeight = school.get(0).getWeight();
        Vector barycenter = school.get(0).getDeltaX().multiply(sumWeight);
        
        for (int i = 1; i < school.size(); i++) {
            Fish f = school.get(i);
            barycenter = barycenter.plus(f.getDeltaX().multiply(f.getWeight()));
            sumWeight += f.getWeight();
        }
        
        barycenter = barycenter.divide(sumWeight);
        
        return barycenter;
    }

}
