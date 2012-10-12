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
package net.sourceforge.cilib.fss.fish;

import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.fss.feeding.FeedingStrategy;
import net.sourceforge.cilib.fss.movement.individual.IndividualMovementProvider;
import net.sourceforge.cilib.type.types.container.Vector;

public interface Fish extends Entity {
    
    @Override
    Fish getClone();
   
    double getWeight();
    
    void setWeight(double weight);
    
    Vector getDeltaX();
    
    void setDeltaX(Vector dx);
    
    double getDeltaF();
    
    void setDeltaF(double df);
    
    double getDeltaW();
    
    void setDeltaW(double dw);
    
    ControlParameter getMaxWeight();
    
    void setMaxWeight(ControlParameter maxWeight);
    
    ControlParameter getMinWeight();
    
    void setMinWeight(ControlParameter minWeight);
    
    IndividualMovementProvider getIndividualMovement();
    
    void setIndividualMovement(IndividualMovementProvider imp);
    
    FeedingStrategy getFeedingStrategy();
    
    void setFeedingStrategy(FeedingStrategy fs);
}
