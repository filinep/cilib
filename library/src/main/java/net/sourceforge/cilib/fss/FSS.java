/**
 * Computational Intelligence Library (CIlib)
 * Copyright (C) 2003 - 2010
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.cilib.fss;

import com.google.common.collect.Lists;
import java.util.List;
import net.sourceforge.cilib.algorithm.initialisation.ClonedPopulationInitialisationStrategy;
import net.sourceforge.cilib.algorithm.population.IterationStrategy;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.coevolution.cooperative.ParticipatingAlgorithm;
import net.sourceforge.cilib.coevolution.cooperative.contributionselection.ContributionSelectionStrategy;
import net.sourceforge.cilib.coevolution.cooperative.contributionselection.ZeroContributionSelectionStrategy;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.entity.topologies.GBestTopology;
import net.sourceforge.cilib.fss.fish.Fish;
import net.sourceforge.cilib.fss.fish.StandardFish;
import net.sourceforge.cilib.fss.iterationstrategies.FSSIterationStrategy;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;

public class FSS extends SinglePopulationBasedAlgorithm implements ParticipatingAlgorithm {
    
    private Topology<Fish> topology;
    private IterationStrategy<FSS> iterationStrategy;
    private ContributionSelectionStrategy contributionSelection;

    public FSS() {
        topology = new GBestTopology<Fish>();
        iterationStrategy = new FSSIterationStrategy();
        initialisationStrategy = new ClonedPopulationInitialisationStrategy();
        initialisationStrategy.setEntityType(new StandardFish());
        contributionSelection = new ZeroContributionSelectionStrategy();
    }

    public FSS(FSS copy) {
        super(copy);
        this.topology = copy.topology.getClone();
        this.iterationStrategy = copy.iterationStrategy.getClone();
        this.initialisationStrategy = copy.initialisationStrategy.getClone();
        this.contributionSelection = copy.contributionSelection.getClone();
    }

    @Override
    public FSS getClone() {
        return new FSS(this);
    }

    @Override
    public void algorithmInitialisation() {
        Iterable<Fish> school = (Iterable<Fish>) this.initialisationStrategy.initialise(this.getOptimisationProblem());
        topology.clear();
        topology.addAll(Lists.<Fish>newLinkedList(school));

        for (Fish f : topology) {
            f.calculateFitness();
        }
    }

    @Override
    protected void algorithmIteration() {
        iterationStrategy.performIteration(this);
    }

    @Override
    public OptimisationSolution getBestSolution() {
        Fish bestEntity = Topologies.getBestEntity(topology);
        return new OptimisationSolution(bestEntity.getCandidateSolution(), bestEntity.getFitness());
    }

    @Override
    public List<OptimisationSolution> getSolutions() {
        List<OptimisationSolution> solutions = Lists.newLinkedList();
        for (Fish e : Topologies.getNeighbourhoodBestEntities(topology)) {
            solutions.add(new OptimisationSolution(e.getCandidateSolution(), e.getFitness()));
        }
        return solutions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    @Override
    public Topology<Fish> getTopology() {
        return topology;
    }

    public IterationStrategy<FSS> getIterationStrategy() {
        return iterationStrategy;
    }

    public void setIterationStrategy(IterationStrategy<FSS> iterationStrategy) {
        this.iterationStrategy = iterationStrategy;
    }

    @Override
    public ContributionSelectionStrategy getContributionSelectionStrategy() {
        return contributionSelection;
    }

    @Override
    public void setContributionSelectionStrategy(ContributionSelectionStrategy strategy) {
        contributionSelection = strategy;
    }
}
