/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.multiple.filter.MultiPopulationFilter;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.math.Stats;
import fj.F;
import fj.data.List;

public class AverageGradientLength implements Measurement<Real> {

    private MultiPopulationFilter filter;

    public AverageGradientLength() {
        filter = null;
    }

    public AverageGradientLength getClone() {
        return this;
    }

    public Real getValue(Algorithm algorithm) {
        final Problem d = algorithm.getOptimisationProblem();
        final FunctionOptimisationProblem fop = (FunctionOptimisationProblem)d;
        final Gradient df = (Gradient)fop.getFunction();

        List<Entity> solutions = filter.filter(algorithm);
        return Real.valueOf(Stats.mean(solutions.map(new F<Entity,Double>(){
                        @Override
                        public Double f(Entity e) {
                            Particle p = (Particle) e;
                            return df.getGradientVectorLength((Vector)p.getBestPosition());
                        }
                    })));
    }

    public MultiPopulationFilter getFilter() { 
       return this.filter;
    }

    public void setFilter(MultiPopulationFilter filter) {
        this.filter = filter;
    }
}
