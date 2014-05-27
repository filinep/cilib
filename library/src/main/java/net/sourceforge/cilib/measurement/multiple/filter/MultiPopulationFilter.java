/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.multiple.filter;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.Vectors;
import fj.F;
import fj.F2;
import fj.data.List;

/*
 * Class used to filter a list of sub-populations 
 * defined by a {@link MultiPopulationDefinition} using
 * the mid-point between pairs of sub-population bests in order
 * to determine if they are on the same optima.
 * 
 */
public class MultiPopulationFilter<E extends Entity> {

	private NicheProvider multiPopulationDefinition;

	public MultiPopulationFilter() {
		multiPopulationDefinition = null;
	}

	public MultiPopulationFilter(MultiPopulationFilter<E> copy) {
		this.multiPopulationDefinition = copy.multiPopulationDefinition;
	}

	public List<E> filter(Algorithm a) {

		List<List<E>> pops = multiPopulationDefinition.getPopulations(a);
		final List<E> bests = getBestEntities(pops);

		// predicate which returns true if current sub-population best
		// is fitness-dominated by any mid-point generated by it
		// and another sub-population best.
		F<E, Boolean> filterPredicate = new F<E, Boolean>() {
			@Override
			public Boolean f(E current) {
				E other, temp = (E) current.getClone();
				Vector pos = (Vector) current.getPosition();
				for (int i = 0; i < bests.length(); ++i) {
					other = bests.index(i);
					// only checks midpoints when other > current
					// which means current can potentially be eliminated
					// other will be considered on its turn
					if (other.compareTo(current) > 0) {
						temp.setPosition(Vectors.mean(pos,
								(Vector) other.getPosition()).some());
						temp.updateFitness(temp.getBehaviour()
								.getFitnessCalculator().getFitness(temp));
						if (temp.compareTo(current) > 0) {
							// return true to remove current E
							return true;
						}
					}
				}

				return false;
			}
		};

		return bests.removeAll(filterPredicate);
	}

	private List<E> getBestEntities(List<List<E>> pops) {

		// function to cycle a given sub-population and return best entity
		final F2<E, E, E> cycleEntities = new F2<E, E, E>() {
			@Override
			public E f(E best, E cur) {
				return (best != null) ? (best.compareTo(cur) > 0) ? best : cur : cur;
			}
		};

		// function to filter list of sub-pops to list of best entity per
		// sub-pop
		final F<List<E>, E> cyclePops = new F<List<E>, E>() {
			@Override
			public E f(List<E> population) {
				return population.foldLeft(cycleEntities, null);
			}
		};

		return pops.map(cyclePops);
	}

	public NicheProvider getNicheProvider() {
		return multiPopulationDefinition;
	}

	public void setNicheProvider(
			NicheProvider multiPopulationDefinition) {
		this.multiPopulationDefinition = multiPopulationDefinition;
	}

}
