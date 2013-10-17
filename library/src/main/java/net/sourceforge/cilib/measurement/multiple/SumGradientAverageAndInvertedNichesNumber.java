/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.measurement.multiple;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.data.List;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.measurement.single.LocalNiches;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.solution.MinimisationFitness;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;
import net.sourceforge.cilib.util.functions.Algorithms;
import net.sourceforge.cilib.util.functions.Solutions;

/**
 *
 * @author florent
 */
public class SumGradientAverageAndInvertedNichesNumber implements Measurement<Real> {
	
	private ControlParameter nicheRadius = ConstantControlParameter.of(0.01);
	
	public void setNicheRadius(ControlParameter nicheRadius) {
		this.nicheRadius = nicheRadius;
	}

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
        
        List<Vector> niches = merge(List.iterableList(multi.getPopulations())
        		.map(Algorithms.<SinglePopulationBasedAlgorithm>getBestSolution()),
        		List.<Vector>nil());
        
        Problem d = algorithm.getOptimisationProblem();
        FunctionOptimisationProblem fop = (FunctionOptimisationProblem) d;
        Gradient df = (Gradient) fop.getFunction();
        
        for (Vector single : niches) {
            ++i;
            res += df.GetGradientVectorLength(single);
        }
        
        return Real.valueOf((res + 1) / ((double) i)); // Difference !!!
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
