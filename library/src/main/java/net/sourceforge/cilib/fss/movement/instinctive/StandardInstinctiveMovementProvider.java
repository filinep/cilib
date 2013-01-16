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
package net.sourceforge.cilib.fss.movement.instinctive;

import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.fss.fish.Fish;
import net.sourceforge.cilib.type.types.container.Vector;

public class StandardInstinctiveMovementProvider implements InstinctiveMovementProvider {

    @Override
    public Vector get(Topology<Fish> school) {
        double sumDeltaF = 0;
        Vector sumProd = Vector.fill(0.0, school.get(0).getDimension());        
        
        for (Fish f : school) {
            // Only use fish that improved
            if (f.getDeltaF() > 0.0) {
                sumProd = sumProd.plus(f.getDeltaX().multiply(f.getDeltaF()));
                sumDeltaF += f.getDeltaF();
            }
        }
        
        if (sumDeltaF != 0.0) {
            sumProd = sumProd.divide(sumDeltaF);
        }
        
        return sumProd;
    }

}
