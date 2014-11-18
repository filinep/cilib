/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */

package net.sourceforge.cilib.niching.iterationstrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import fj.F;
import fj.data.List;
import net.sourceforge.cilib.temp.SquareMatrix;
import net.sourceforge.cilib.temp.EntityUtil;
import net.sourceforge.cilib.entity.topologies.NichingLabeledNeighbourhood;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.Property;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.Int;
import net.sourceforge.cilib.type.types.container.Node;
import net.sourceforge.cilib.util.distancemeasure.DistanceMeasure;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;

public class AdaptiveNichingIterationStrategy extends
		AbstractIterationStrategy<PSO> {
	private static final long serialVersionUID = 1L;

	private ControlParameter pMax;
	private ControlParameter nicheIterMin;
	private ControlParameter nicheIterLength;

	private DistanceMeasure distance;

	java.util.List<Double> closest;
	int[][] proximityCount = null;

	public AdaptiveNichingIterationStrategy() {

		super();
		distance = new EuclideanDistanceMeasure();
		nicheIterMin = ConstantControlParameter.of(2);
		nicheIterLength = ConstantControlParameter.of(2);
		pMax = ConstantControlParameter.of(30);
	}

	@Override
	public AbstractIterationStrategy<PSO> getClone() {
		return this;
	}

	@Override
	public void performIteration(final PSO algorithm) {

		final fj.data.List<Particle> topology = algorithm.getTopology();
		int popSize = topology.length();

		if (proximityCount == null) {
			proximityCount = new int[popSize][popSize];
		}

		final F<Particle, Particle> evaluate = new F<Particle, Particle>() {
			@Override
			public Particle f(Particle current) {
				current.getBehaviour().performIteration(current);
				return current;
			}
		};

		final F<Particle, Particle> assign = new F<Particle, Particle>() {
			public Particle f(Particle current) {
				for (Particle other : algorithm.getNeighbourhood().f(topology,
						current)) {
					if (current.getSocialFitness().compareTo(
							other.getNeighbourhoodBest().getSocialFitness()) > 0) {
						other.setNeighbourhoodBest(current);
					}
				}

				return current;
			}
		};

		List<Particle> evaluated = topology.map(evaluate);

		// Adaptive niching
		// assumes constant ordering of particles in provided list
		SquareMatrix<Double> distances = new SquareMatrix<Double>(popSize,
				Double.MAX_VALUE);
		double nicheRadius = distanceCalc(evaluated, distances);
		Node<Particle> graph = makeGraph(evaluated, distances, nicheRadius);

		java.util.List<java.util.List<Particle>> niches = new ArrayList<java.util.List<Particle>>();
		makeNiches(graph, nicheRadius, niches);
		// last list is unNiched Particles
		java.util.List<Particle> unNiched = niches.remove(niches.size() - 1);

		NichingLabeledNeighbourhood<Particle> neighbouhood = (NichingLabeledNeighbourhood<Particle>) algorithm
				.getNeighbourhood();

		neighbouhood.setNeighbourhoods(niches);
		neighbouhood.setUnNiched(unNiched);

		algorithm.setTopology(evaluated.map(assign));

		// print niching stats
		// System.out.println();
		// System.out.println(nicheRadius);
		// System.out.println("n u  "+niches.size()+" "+unNiched.size());
		// int s,c = 0, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		// for (java.util.List<Particle> nh : niches) {
		// s = nh.size();
		// min = Math.min(s, min);
		// max = Math.max(s, max);
		// c += s;
		// }
		// System.out.println("("+min+","+max+") avg:"+(double)c/niches.size());
		// System.out.println();

	}

	// --------------------------------------------------+
	// utils
	@SuppressWarnings("unchecked")
	protected <E extends Entity> double distanceCalc(List<E> pop,
			SquareMatrix<Double> distances) {

		int size = pop.length();
		closest = new ArrayList<Double>(Collections.nCopies(size,
				Double.MAX_VALUE));

		E rowE;
		double dist;
		for (int row = 0; row < size; ++row) {
			rowE = pop.index(row);
			for (int col = row + 1; col < size; ++col) {
				dist = distance.distance(rowE.getPosition(), pop.index(col)
						.getPosition());
				distances.set(dist, row, col);
				// update closest to row and col
				if (dist < closest.get(row)) {
					closest.set(row, dist);
				}
				if (dist < closest.get(col)) {
					closest.set(col, dist);
				}
			}
		}

		double cumulative = 0;
		for (int p = 0; p < size; ++p) {
			cumulative += closest.get(p);
		}

		return cumulative / size;
	}

	protected <E extends Entity> Node<E> makeGraph(List<E> pop,
			SquareMatrix<Double> distances, double radius) {

		Node<E> graph = new Node<E>(pop);
		java.util.List<Node<E>> nodeList = graph.getGraph();

		int proxMin = (int) nicheIterMin.getParameter();
		int maxProxCount = (int) (proxMin + nicheIterLength.getParameter());
		int size = nodeList.size();
		for (int i = 0; i < size; ++i) {
			for (int j = i + 1; j < size; ++j) {
				// increment
				if (distances.get(i, j) < radius) {
					if (proximityCount[i][j] < maxProxCount) {
						++proximityCount[i][j];
					}
					if (proximityCount[i][j] >= proxMin) {
						nodeList.get(i).addBidirectionalEdge(nodeList.get(j));
					}
				}
				// decrement
				else {
					if (proximityCount[i][j] > 0) {
						--proximityCount[i][j];
					}
				}
			}
		}

		return graph;
	}

	protected <E extends Entity> void makeNiches(Node<E> graph, double radius,
			java.util.List<java.util.List<E>> niches) {

		java.util.Set<Node<E>> visited = new HashSet<Node<E>>();
		java.util.Set<Node<E>> reachable = new HashSet<Node<E>>();

		java.util.List<E> niche, unNiched = new ArrayList<E>();

		for (Node<E> node : graph.getGraph()) {
			// already niched
			if (visited.contains(node))
				continue;

			// create niche
			// if no edges then it is unNiched
			// edges are set bi-directionally
			// which means noEdge = closest > radius || iterMin not met
			if (node.getEdges().size() > 0) {
				niche = new ArrayList<E>();
				// reachable includes self and will be processed
				node.getReachable(reachable);
				for (Node<E> reached : reachable) {
					if (!visited.contains(reached)) {
						visited.add(reached);
						niche.add(reached.getElem());
						reached.getElem().put(
								Property.LABELED_NEIGHBOURHOOD_INDEX,
								Int.valueOf(niches.size()));
					}
				}
				niches.add(niche);
				reachable.clear();
			}
			// add to unNiched
			else {
				unNiched.add(node.getElem());
				node.getElem().put(Property.LABELED_NEIGHBOURHOOD_INDEX,
						Int.valueOf(-1)); // -1 : its unNiched
			}
		}

		// remove niche overflow and add to uNiched
		int overCrowded, max = (int) pMax.getParameter();
		E removed;
		for (int i = 0; i < niches.size(); ++i) {
			niche = niches.get(i);
			overCrowded = niche.size() - max;
			if (overCrowded > 0) {
				// System.out.println("removed: "+overCrowded);
				Collections.sort(niche, EntityUtil.getBestFitnessComparator());
				while (overCrowded > 0) {
					removed = niche.remove(niche.size() - 1);
					removed.reinitialise();
					removed.put(Property.LABELED_NEIGHBOURHOOD_INDEX,
							Int.valueOf(-1)); // -1 : its unNiched
					unNiched.add(removed);
					--overCrowded;
				}
			}
		}

		niches.add(unNiched);
	}

	// --------------------------------------------------+
	public void setPMax(ControlParameter pMax) {
		this.pMax = pMax;
	}

	public ControlParameter getPMax() {
		return pMax;
	}

	public void setNicheIterMin(ControlParameter nicheIterMin) {
		this.nicheIterMin = nicheIterMin;
	}

	public ControlParameter getNicheIterMin() {
		return nicheIterMin;
	}

	public void setNicheIterLength(ControlParameter nicheIterLength) {
		this.nicheIterLength = nicheIterLength;
	}

	public ControlParameter getNicheIterLength() {
		return nicheIterLength;
	}

	// --------------------------------------------------+

}
