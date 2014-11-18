/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */

package net.sourceforge.cilib.pso;

import net.sourceforge.cilib.algorithm.MultiPopAlgorithm;
import net.sourceforge.cilib.entity.topologies.NichingNeighbourhood;
import net.sourceforge.cilib.pso.PSO;

public class MultiPopPSO extends PSO implements MultiPopAlgorithm {
	private static final long serialVersionUID = 1L;

	@Override
	public int getAllPopulationSize() {
		return this.topology.length();
	}

	@Override
	public int getPopulationSize(int index) {
		return ((NichingNeighbourhood) this.getNeighbourhood())
				.getPopulationSize(index);
	}

	@Override
	public int getPopulationCount() {
		return ((NichingNeighbourhood) this.getNeighbourhood())
				.getPopulationCount();
	}

}
