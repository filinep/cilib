/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple.filter;

import net.sourceforge.cilib.entity.Entity;
import fj.data.List;

/*
 * Interface for providing a definition of the underlying 
 * sub-population devide within an overall population.
 * 
 * Used for niching, clustering, etc...
 * 
 */
public interface MultiPopulationDefinition<E extends Entity> {

	List<List<E>> getPopulations();
}
