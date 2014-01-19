/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.hpso.HeterogeneousIterationStrategy;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.TypeList;

public class HPSOSelectionValues implements Measurement<TypeList> {

    @Override
    public Measurement<TypeList> getClone() {
        return this;
    }

    @Override
    public TypeList getValue(Algorithm algorithm) {
        PSO pso = (PSO) AbstractAlgorithm.get();
        HeterogeneousIterationStrategy strategy = (HeterogeneousIterationStrategy) pso.getIterationStrategy();
        TypeList list = new TypeList();
        
        for (Double d : strategy.getSelectionValues()) {
            list.add(Real.valueOf(d));
        }
        
        return list;
    }

}
