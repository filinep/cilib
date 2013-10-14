/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import fj.Equal;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.TypeList;

/**
 * Measurement to perform measurements on a set of contained {@code Algorithm}
 * instances. This type of measurement is generally only defined for
 * {@link net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm}.
 */
public class CompositeMeasurement implements Measurement<TypeList> {

    private static final long serialVersionUID = -7109719897119621328L;
    private List<Measurement<? extends Type>> measurements;

    /**
     * Create a new instance with zero measurements.
     */
    public CompositeMeasurement() {
        this.measurements = new ArrayList<Measurement<? extends Type>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeMeasurement getClone() {
        CompositeMeasurement newCM = new CompositeMeasurement();

        for(Measurement<? extends Type> m : this.measurements) {
            newCM.addMeasurement(m.getClone());
        }

        return newCM;
    }

    /**
     * Get the measurement values for all sub-algorithms.
     * @param algorithm The top level algorithm
     * @return The values of measurements applied to all contained algorithms.
     */
    @Override
    public TypeList getValue(Algorithm algorithm) {
        TypeList vector = new TypeList();

        MultiPopulationBasedAlgorithm multi;
        
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            multi = neighbourhood2populations((SinglePopulationBasedAlgorithm<Entity>) algorithm);
           
        } else {
            multi = (MultiPopulationBasedAlgorithm) algorithm;
        }

        for (SinglePopulationBasedAlgorithm single : multi.getPopulations()) {
            TypeList vector1 = new TypeList();
            for (Measurement<? extends Type> measurement : measurements) {
                vector1.add(measurement.getValue(single));
            }
            vector.add(vector1);
        }

        return vector;
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
            boolean found = false;
            for (SinglePopulationBasedAlgorithm s1 : m.getPopulations()) {
                if (s1.getTopology().exists(Equal.<E>anyEqual().eq(e))) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                SinglePopulationBasedAlgorithm dummyPopulation = s.getClone();            
                dummyPopulation.setTopology(s.getNeighbourhood().f(s.getTopology(), e));

                m.addPopulationBasedAlgorithm(dummyPopulation);
            }
        }
        
        return m;
    }

    /**
     * Add a measurement to the composite for evaluation on the sub-algorithms.
     * @param measurement The measurement to add.
     */
    public void addMeasurement(Measurement<? extends Type> measurement) {
        this.measurements.add(measurement);
    }
}
