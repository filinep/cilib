/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.measurement.multiple;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.controlparameter.NichingRadiusControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;
import net.sourceforge.cilib.util.functions.Algorithms;

public class SumGradientAverageAndInvertedNichesNumber implements Measurement<Real> {

    private ControlParameter nicheRadius = new NichingRadiusControlParameter();

    public void setNicheRadius(ControlParameter nicheRadius) {
        this.nicheRadius = nicheRadius;
    }

    @Override
    public SumGradientAverageAndInvertedNichesNumber getClone() {
        return this;
    }

    @Override
    public Real getValue(Algorithm algorithm) {
        MultiPopulationBasedAlgorithm multi = getDummyMPBA();
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            SinglePopulationBasedAlgorithm s = (SinglePopulationBasedAlgorithm) algorithm;
            for (Object e : s.getTopology()) {
                SinglePopulationBasedAlgorithm dummyPopulation = s.getClone();
                dummyPopulation.setTopology(List.list(e));
                multi.addPopulationBasedAlgorithm(dummyPopulation);
            }
        } else {
            MultiPopulationBasedAlgorithm m = (MultiPopulationBasedAlgorithm) algorithm;
            for (SinglePopulationBasedAlgorithm s : m.getPopulations()) {
                for (Object e : s.getTopology()) {
                    SinglePopulationBasedAlgorithm s1 = s.getClone();
                    s1.setTopology(fj.data.List.list(e));
                    multi.addPopulationBasedAlgorithm(s1);
                }
            }
            
            if (algorithm instanceof NichingAlgorithm) {
                for (Object e : ((NichingAlgorithm)algorithm).getMainSwarm().getTopology()) {
                    SinglePopulationBasedAlgorithm s1 = ((NichingAlgorithm)algorithm).getMainSwarm().getClone();
                    s1.setTopology(fj.data.List.list(e));
                    multi.addPopulationBasedAlgorithm(s1);
                }
            }
        }

        List<Vector> niches = merge(List.iterableList(multi.getPopulations())
                .map(Algorithms.<SinglePopulationBasedAlgorithm>getBestSolution()),
                List.<Vector>nil());

        Problem d = algorithm.getOptimisationProblem();
        FunctionOptimisationProblem fop = (FunctionOptimisationProblem) d;
        Gradient df = (Gradient) fop.getFunction();

        double sum = 0.0;
        for (Vector single : niches) {
            sum += df.getGradientVectorLength(single);
        }

        //System.out.println(-Math.exp(sum) / Math.exp(niches.length()));
        return Real.valueOf(-Math.exp(sum) / Math.exp(niches.length()));
    }

    private List<Vector> merge(final List<OptimisationSolution> swarm, final List<Vector> acc) {
        if (swarm.isEmpty()) {
            return acc;
        }

        if (swarm.tail().isEmpty()) {
            return merge(List.<OptimisationSolution>nil(), acc.cons((Vector) swarm.head().getPosition()));
        }

        List<OptimisationSolution> toMerge = swarm.tail().filter(new F<OptimisationSolution, Boolean>() {
            @Override
            public Boolean f(OptimisationSolution e) {
                return new EuclideanDistanceMeasure()
                        .distance((Vector) swarm.head().getPosition(), (Vector) e.getPosition()) < nicheRadius.getParameter();
            }
        }).cons(swarm.head());

        return merge(swarm.minus(Equal.<OptimisationSolution>anyEqual(), toMerge),
                acc.cons((Vector) toMerge.maximum(Ord.<OptimisationSolution>comparableOrd()).getPosition()));
    }

    private <E extends Entity> MultiPopulationBasedAlgorithm neighbourhood2populations(SinglePopulationBasedAlgorithm<E> s) {
        MultiPopulationBasedAlgorithm m = getDummyMPBA();
        for (E e : s.getTopology()) {
            SinglePopulationBasedAlgorithm dummyPopulation = s.getClone();
            dummyPopulation.setTopology(List.list(e));
            m.addPopulationBasedAlgorithm(dummyPopulation);
        }
        return m;
    }
    
    private <E extends Entity> MultiPopulationBasedAlgorithm getDummyMPBA() {
        return new MultiPopulationBasedAlgorithm() {
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
    }
}
