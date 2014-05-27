/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple.filter;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.niching.VectorBasedFunctions;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;
import net.sourceforge.cilib.util.functions.Algorithms;
import fj.data.List;
import fj.F;
import fj.Ord;

public class NearestNicheProvider implements NicheProvider<Particle> {

    private ControlParameter nearestCount;

    public List<List<Particle>> getPopulations(Algorithm a) {
        final SinglePopulationBasedAlgorithm<Particle> pba = (SinglePopulationBasedAlgorithm<Particle>) a;
        return pba.getTopology().map(new F<Particle, List<Particle>>() {
                @Override
                public List<Particle>f(Particle a) {
                    return pba.getTopology()
                        .sort(Ord.ord(VectorBasedFunctions.sortByDistance(a, new EuclideanDistanceMeasure())))
                        .take((int) nearestCount.getParameter());
                }
            });
    }

    public void setNearestCount(ControlParameter cp) {
        this.nearestCount = cp;
    }
}
