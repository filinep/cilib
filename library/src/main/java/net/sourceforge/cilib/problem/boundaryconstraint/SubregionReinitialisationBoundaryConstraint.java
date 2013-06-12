/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.problem.boundaryconstraint;

import fj.F;
import java.util.Iterator;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.type.types.Bounds;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Reinitialises out of bounds dimensions to a random value between the previous value
 * and the closest bound.
 */
public class SubregionReinitialisationBoundaryConstraint implements BoundaryConstraint {

    @Override
    public SubregionReinitialisationBoundaryConstraint getClone() {
        return this;
    }

    @Override
    public void enforce(Entity entity) {
        final Vector s = (Vector) entity.getCandidateSolution();
        final Iterator<Numeric> iter = ((Vector) entity.getProperties().get(EntityType.PREVIOUS_SOLUTION)).iterator();
        final UniformDistribution uniform = new UniformDistribution();
        
        entity.setCandidateSolution(s.map(new F<Numeric, Numeric>() {
            @Override
            public Numeric f(Numeric a) {
                final Bounds b = a.getBounds();
                
                if (a.doubleValue() < a.getBounds().getLowerBound()) {
                    return Real.valueOf(uniform.getRandomNumber(b.getLowerBound(), iter.next().doubleValue()), b);
                } else if (a.doubleValue() > a.getBounds().getUpperBound()) {
                    return Real.valueOf(uniform.getRandomNumber(iter.next().doubleValue(), b.getUpperBound()), b);
                }
                
                return a;
            }
        }));
    }

}
