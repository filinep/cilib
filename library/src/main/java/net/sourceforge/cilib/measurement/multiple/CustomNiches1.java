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
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.measurement.multiple.filter.NicheProvider;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.DeratingOptimisationProblem;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.type.types.container.Vector;

public class CustomNiches1 implements Measurement<TypeList> {

    private NicheProvider filter;

    public CustomNiches1() {
        filter = null;
    }

    public CustomNiches1 getClone() {
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

                try {
                    final Problem d = algorithm.getOptimisationProblem();
                    final FunctionOptimisationProblem fop = (FunctionOptimisationProblem)d;
                    final Gradient df = (Gradient)fop.getFunction();
                    t3.add(Real.valueOf(df.getGradientVectorLength((Vector)p.getPosition())));
                } catch (Exception ex) {
                    t3.add(Real.valueOf(Double.NaN));
                }

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
