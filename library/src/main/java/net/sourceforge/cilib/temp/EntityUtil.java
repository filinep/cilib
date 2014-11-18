/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */

package net.sourceforge.cilib.temp;

import java.util.Comparator;

import net.sourceforge.cilib.entity.Entity;
import fj.F;
import fj.Ord;
import fj.Ordering;

public class EntityUtil {

	public static Ord<Entity> getBestFitnessOrdering() {

		return Ord.ord(new F<Entity, F<Entity, Ordering>>() {
			@Override
			public F<Entity, Ordering> f(final Entity a) {
				return new F<Entity, Ordering>() {
					@Override
					public Ordering f(final Entity b) {
						int x = a.getBestFitness()
								.compareTo(b.getBestFitness());
						return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ
								: Ordering.GT;
					}
				};
			}
		});
	}

	public static <E extends Entity> Comparator<E> getBestFitnessComparator() {

		return new Comparator<E>() {
			@Override
			public int compare(E a, E b) {
				return a.getBestFitness().compareTo(b.getBestFitness());
			}

		};
	}

}
