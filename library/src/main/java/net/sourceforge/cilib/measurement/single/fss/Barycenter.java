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
import net.sourceforge.cilib.type.types.container.Vector;

public class Barycenter implements Measurement<Vector> {

    @Override
    public Barycenter getClone() {
        return this;
    }

    @Override
    public Vector getValue(Algorithm algorithm) {
        Topology<Fish> school = ((FSS) algorithm).getTopology();
        return calculate(school);
    }
    
    public static Vector calculate(Topology<Fish> school) {
        double sumWeight = 0.0;
        Vector barycenter = Vector.fill(0.0, school.get(0).getDimension());
        
        for (Fish f : school) {
            Vector x = (Vector) f.getCandidateSolution();
            barycenter = barycenter.plus(x.multiply(f.getWeight()));
            sumWeight += f.getWeight();
        }
        
        barycenter = barycenter.divide(sumWeight);
        
        return barycenter;
    }

}
