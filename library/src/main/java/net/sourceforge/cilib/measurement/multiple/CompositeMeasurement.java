/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import com.google.common.collect.Lists;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;

/**
 * Measurement to perform measurements on a set of contained {@code Algorithm}
 * instances. This type of measurement is generally only defined for
 * {@link net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm}.
 */
public class CompositeMeasurement implements Measurement<TypeList> {

    private static final long serialVersionUID = -7109719897119621328L;
    private List<Measurement<? extends Type>> measurements;
    private boolean useSpecies = false;
    private ControlParameter radius;

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
        for (Measurement<? extends Type> m : this.measurements) {
            newCM.addMeasurement(m.getClone());
        }
        return newCM;
    }

    /**
     * Get the measurement values for all sub-algorithms.
     *
     * @param algorithm The top level algorithm
     * @return The values of measurements applied to all contained algorithms.
     */
    @Override
    public TypeList getValue(Algorithm algorithm) {
        TypeList vector = new TypeList();

        MultiPopulationBasedAlgorithm multi;
        
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            if (!useSpecies) {
                multi = neighbourhood2populations((SinglePopulationBasedAlgorithm<Entity>) algorithm);
            } else {
                multi = species2populations((SinglePopulationBasedAlgorithm<Entity>) algorithm);
            }
        } else {
            //multi = (MultiPopulationBasedAlgorithm) algorithm;
            MultiPopulationBasedAlgorithm m = (MultiPopulationBasedAlgorithm) algorithm;
            multi = getDummyMPBA();
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

        for (SinglePopulationBasedAlgorithm single : multi.getPopulations()) {
            TypeList vector1 = new TypeList();
            for (Measurement<? extends Type> measurement : measurements) {
                vector1.add(measurement.getValue(single));
            }
            vector.add(vector1);
        }
        return vector;
    }
    
    private <E extends Entity> F2<fj.data.List<E>, fj.data.List<SinglePopulationBasedAlgorithm>, fj.data.List<SinglePopulationBasedAlgorithm>> getNhoods(final SinglePopulationBasedAlgorithm algorithm) {
        final EuclideanDistanceMeasure distance = new EuclideanDistanceMeasure();
        return new F2<fj.data.List<E>, fj.data.List<SinglePopulationBasedAlgorithm>, fj.data.List<SinglePopulationBasedAlgorithm>>() {
            @Override
            public fj.data.List<SinglePopulationBasedAlgorithm> f(final fj.data.List<E> list, fj.data.List<SinglePopulationBasedAlgorithm> acc) {
                if (list.isEmpty()) {
                    return acc;
                }

                final fj.data.List<E> sorted = list.sort(Ord.<E>ord(new F2<E, E, Ordering>() {
                    @Override
                    public Ordering f(E a, E b) {
                        return Ordering.values()[-a.getFitness().compareTo(b.getFitness()) + 1];
                    }
                }.curry()));

                fj.data.List<E> neighbours = sorted.filter(new F<E, Boolean>() {
                    @Override
                    public Boolean f(E a) {
                        return distance.distance(a.getCandidateSolution(), sorted.head().getCandidateSolution()) < radius.getParameter();
                    }
                });
                
                SinglePopulationBasedAlgorithm alg = algorithm.getClone();
                alg.setTopology(neighbours);

                return this.f(sorted.minus(Equal.<E>anyEqual(), neighbours), acc.cons(alg));
            }
        };
    }
    
    private <E extends Entity> MultiPopulationBasedAlgorithm species2populations(SinglePopulationBasedAlgorithm s) {
        MultiPopulationBasedAlgorithm m = getDummyMPBA();
        m.setPopulations(Lists.newArrayList(getNhoods(s).f(s.getTopology(), fj.data.List.<SinglePopulationBasedAlgorithm>nil()).toCollection()));
        return m;
    }
    
    private <E extends Entity> MultiPopulationBasedAlgorithm neighbourhood2populations(SinglePopulationBasedAlgorithm<E> s) {
        MultiPopulationBasedAlgorithm m = getDummyMPBA();
        
        for (E e : s.getTopology()) {
            /*boolean found = false;
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
            }*/
            SinglePopulationBasedAlgorithm dummyPopulation = s.getClone();            
            dummyPopulation.setTopology(fj.data.List.<E>list(e));

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
    
    /**
     * Add a measurement to the composite for evaluation on the sub-algorithms.
     *
     * @param measurement The measurement to add.
     */
    public void addMeasurement(Measurement<? extends Type> measurement) {
        this.measurements.add(measurement);
    }

    public void setUseSpecies(Boolean useSpecies) {
        this.useSpecies = useSpecies;
    }

    public void setRadius(ControlParameter radius) {
        this.radius = radius;
    }
}
