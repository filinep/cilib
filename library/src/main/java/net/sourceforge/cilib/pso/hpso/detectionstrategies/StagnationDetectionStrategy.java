/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.hpso.detectionstrategies;

import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.problem.solution.Fitness;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.Int;

public class StagnationDetectionStrategy implements BehaviorChangeTriggerDetectionStrategy {
    
    private ControlParameter windowSize;

    private enum Stagnation {
        POS_STAGNATION
    }

    public StagnationDetectionStrategy() {
        this.windowSize = ConstantControlParameter.of(10);
    }
    
    public StagnationDetectionStrategy(StagnationDetectionStrategy copy) {
        this.windowSize = copy.windowSize.getClone();
    }

    public StagnationDetectionStrategy getClone() {
        return new StagnationDetectionStrategy(this);
    }
    
    public boolean detect(Particle p) {
        Int i = (Int) p.getProperties().get(Stagnation.POS_STAGNATION);
        
        if (i == null) {
            p.getProperties().put(Stagnation.POS_STAGNATION, Int.valueOf(0));
            return false;
        }
        
        if (p.getFitness().compareTo((Fitness) p.getProperties().get(EntityType.PREVIOUS_FITNESS)) <= 0) {
            p.getProperties().put(Stagnation.POS_STAGNATION, Int.valueOf(i.intValue() + 1));
        }
        
        if (i.intValue() > windowSize.getParameter()) {
            p.getProperties().put(Stagnation.POS_STAGNATION, Int.valueOf(0));
            return true;
        }
        
        return false;
    }

    public void setWindowSize(ControlParameter windowSize) {
        this.windowSize = windowSize;
    }

    public ControlParameter getWindowSize() {
        return windowSize;
    }

}
