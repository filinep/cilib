/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.niching.creation;

import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.pso.hpso.detectionstrategies.PersonalBestStagnationDetectionStrategy;
import net.sourceforge.cilib.pso.particle.Particle;

public class PBestStagnatedNicheDetection extends NicheDetection {

    private PersonalBestStagnationDetectionStrategy detector;

    public PBestStagnatedNicheDetection() {
         this.detector = new PersonalBestStagnationDetectionStrategy();
    }

    public PBestStagnatedNicheDetection(PBestStagnatedNicheDetection copy) {
        this.detector = copy.detector.getClone();
    }

    @Override
    public Boolean f(SinglePopulationBasedAlgorithm a, Entity b) {
        Particle p = (Particle) b;
        boolean d = detector.detect(p);
        return d && p.getNeighbourhoodBest() == p;
    }

    public void setS(ControlParameter s) {
        this.detector.setWindowSize(s);
    }
}
