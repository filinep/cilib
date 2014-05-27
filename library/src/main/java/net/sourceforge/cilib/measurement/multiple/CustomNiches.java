/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import fj.data.List;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.measurement.multiple.filter.NicheProvider;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.TypeList;

public class CustomNiches implements Measurement<TypeList> {

    private NicheProvider filter;

    public CustomNiches() {
        filter = null;
    }

    public CustomNiches getClone() {
        return this;
    }

    public TypeList getValue(Algorithm algorithm) {
        List<List<Entity>> niches = filter.getPopulations(algorithm);
        TypeList t1 = new TypeList();
        for (List<Entity> l : niches) {
            TypeList t2 = new TypeList();
            for (Entity e : l) {
                TypeList t3 = new TypeList();
                Particle p = (Particle) e;

                t3.add(p.getPosition());
                t3.add(Real.valueOf(p.getFitness().getValue()));
                t3.add(p.getNeighbourhoodBest().getBestPosition());
                t3.add(p.getBestPosition());

                t2.add(t3);
            }
            t1.add(t2);
        }
        return t1;
    }

    public void setNicheProvider(NicheProvider filter) {
        this.filter = filter;
    }
}
