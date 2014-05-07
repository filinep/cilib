/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.measurement.multiple.filter.MultiPopulationFilter;
import net.sourceforge.cilib.type.types.Int;

public class NicheCount implements Measurement<Int> {

    private MultiPopulationFilter filter;

    public NicheCount() {
        filter = null;
    }

    public NicheCount getClone() {
        return this;
    }

    public Int getValue(Algorithm algorithm) {
        return Int.valueOf(filter.filter(algorithm).length());
    }

    public MultiPopulationFilter getFilter() {
        return this.filter;
    }

    public void setFilter(MultiPopulationFilter filter) {
        this.filter = filter;
    }
}
