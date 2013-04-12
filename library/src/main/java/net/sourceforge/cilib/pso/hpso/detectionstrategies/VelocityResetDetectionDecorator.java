/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.hpso.detectionstrategies;

import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.container.Vector;

public class VelocityResetDetectionDecorator implements BehaviorChangeTriggerDetectionStrategy {
    
    private BehaviorChangeTriggerDetectionStrategy detectionStrategy;

    public VelocityResetDetectionDecorator() {
        this.detectionStrategy = new PersonalBestStagnationDetectionStrategy();
    }

    public VelocityResetDetectionDecorator(VelocityResetDetectionDecorator copy) {
        this.detectionStrategy = copy.detectionStrategy.getClone();
    }
    
    public VelocityResetDetectionDecorator getClone() {
        return new VelocityResetDetectionDecorator(this);
    }

    public boolean detect(Particle entity) {
        if (detectionStrategy.detect(entity)) {
            entity.getVelocityInitialisationStrategy().initialise(EntityType.Particle.VELOCITY, entity);
            return true;
        }
        
        return false;
    }

    public void setDetectionStrategy(BehaviorChangeTriggerDetectionStrategy detectionStrategy) {
        this.detectionStrategy = detectionStrategy;
    }

    public BehaviorChangeTriggerDetectionStrategy getDetectionStrategy() {
        return detectionStrategy;
    }

}
