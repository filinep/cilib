/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.measurement.multiple;

import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.measurement.single.LocalNiches;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 *
 * @author florent
 */
public class SumGradientAverageAndInvertedNichesNumber implements Measurement<Real> {

    @Override
    public SumGradientAverageAndInvertedNichesNumber getClone() {
        return this;
    }

    @Override
    public Real getValue(Algorithm algorithm) {

        double res = 0.0;
        int i = 0;

        MultiPopulationBasedAlgorithm multi;
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            multi = neighbourhood2populations((SinglePopulationBasedAlgorithm<Entity>) algorithm);
        } else {
            multi = (MultiPopulationBasedAlgorithm) algorithm;
        }

        TypeList tl = new LocalNiches().getValue(multi);
        Problem d = algorithm.getOptimisationProblem();
        FunctionOptimisationProblem fop = (FunctionOptimisationProblem) d;
        Gradient df = (Gradient) fop.getFunction();
        
        for (Type t : tl) {
            Vector v = (Vector) t;
            res += df.GetGradientVectorLength(v);
        }
        
        return Real.valueOf(1.0 / tl.size() + res / tl.size());
        /*for (SinglePopulationBasedAlgorithm single : multi.getPopulations()) {
            ++i;
            OptimisationSolution best = single.getBestSolution();
            Problem d = single.getOptimisationProblem();
            FunctionOptimisationProblem fop = (FunctionOptimisationProblem) d;
            Gradient df = (Gradient) fop.getFunction();

            res += df.GetGradientVectorLength((Vector) best.getPosition());
        }
        return Real.valueOf((res + 1) / ((double) i)); // Difference !!!*/
    }

    private <E extends Entity> MultiPopulationBasedAlgorithm neighbourhood2populations(SinglePopulationBasedAlgorithm<E> s) {
        MultiPopulationBasedAlgorithm m = new MultiPopulationBasedAlgorithm() {
            @Override
            protected void algorithmIteration() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public AbstractAlgorithm getClone() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public OptimisationSolution getBestSolution() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Iterable<OptimisationSolution> getSolutions() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        for (E e : s.getTopology()) {
            SinglePopulationBasedAlgorithm dummyPopulation = s.getClone();
            dummyPopulation.setTopology(s.getNeighbourhood().f(s.getTopology(), e));
            m.addPopulationBasedAlgorithm(dummyPopulation);
        }
        return m;
    }
}
