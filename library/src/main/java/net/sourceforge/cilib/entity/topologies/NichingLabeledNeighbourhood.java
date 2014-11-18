/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */

package net.sourceforge.cilib.entity.topologies;

import fj.F;
import fj.data.List;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.Property;
import net.sourceforge.cilib.entity.topologies.GBestNeighbourhood;
import net.sourceforge.cilib.entity.topologies.Neighbourhood;
import net.sourceforge.cilib.entity.topologies.VonNeumannNeighbourhood;
import net.sourceforge.cilib.type.types.Int;

public class NichingLabeledNeighbourhood<E extends Entity> extends
		Neighbourhood<E> implements NichingNeighbourhood {

	List<E> unNiched;
	List<List<E>> neighbourhoods;

	private ControlParameter unNichedTopology;

	Neighbourhood<E> delegate;

	public NichingLabeledNeighbourhood() {
		delegate = null;// pbest topology
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> f(List<E> pop, E ind) {

		if (neighbourhoods == null) {
			return List.<E> list(ind);
		}

		Int i = ind.get(Property.LABELED_NEIGHBOURHOOD_INDEX);
		if (delegate == null) { // self topology
			return (i.intValue() < 0) ? List.<E> list(ind) : neighbourhoods
					.index(i.intValue());
		} else {
			return (i.intValue() < 0) ? delegate.f(unNiched, ind)
					: neighbourhoods.index(i.intValue());
		}
	}

	public List<List<E>> getNeighbourhoods() {
		return neighbourhoods;
	}

	public void setUnNiched(List<E> unNiched) {
		this.unNiched = unNiched;
	}

	public void setUnNiched(java.util.List<E> unNiched) {
		this.unNiched = List.iterableList(unNiched);
	}

	public void setNeighbourhoods(List<List<E>> neighbourhoods) {

		this.neighbourhoods = neighbourhoods;
	}

	public void setNeighbourhoods(
			java.util.List<java.util.List<E>> neighbourhoods) {

		final F<java.util.List<E>, List<E>> convert = new F<java.util.List<E>, List<E>>() {
			@Override
			public List<E> f(java.util.List<E> utilList) {
				return List.iterableList(utilList);
			}
		};

		this.neighbourhoods = List.iterableList(neighbourhoods).map(convert);
		// System.out.println(this.neighbourhoods.length());
	}

	public void setUnNichedTopology(ControlParameter unNichedTopology) {
		this.unNichedTopology = unNichedTopology;
		switch ((int) unNichedTopology.getParameter()) {
		case 0:
			delegate = new VonNeumannNeighbourhood<E>();
			break;

		case 1:
			delegate = new GBestNeighbourhood<E>();
			break;
		default:
			delegate = null;
		}
	}

	// --------------------------------------------+
	@Override
	public int getPopulationSize(int index) {

		return (neighbourhoods == null) ? 0 : neighbourhoods.index(index)
				.length();
	}

	@Override
	public int getPopulationCount() {

		return (neighbourhoods == null) ? 0 : neighbourhoods.length();
	}

	// --------------------------------------------+
}
