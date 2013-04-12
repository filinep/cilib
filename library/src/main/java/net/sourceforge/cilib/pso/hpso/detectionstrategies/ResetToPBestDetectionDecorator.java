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

public class ResetToPBestDetectionDecorator implements BehaviorChangeTriggerDetectionStrategy {

    private BehaviorChangeTriggerDetectionStrategy detectionStrategy;

    public ResetToPBestDetectionDecorator() {
        this.detectionStrategy = new PersonalBestStagnationDetectionStrategy();
    }

    public ResetToPBestDetectionDecorator(ResetToPBestDetectionDecorator copy) {
        this.detectionStrategy = copy.detectionStrategy.getClone();
    }
    
    public ResetToPBestDetectionDecorator getClone() {
        return new ResetToPBestDetectionDecorator(this);
    }

    public boolean detect(Particle entity) {
        if (detectionStrategy.detect(entity)) {
            entity.setCandidateSolution(entity.getBestPosition());
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
