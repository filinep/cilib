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
package net.sourceforge.cilib.fss.fish;

import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.AbstractEntity;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.entity.initialization.InitializationStrategy;
import net.sourceforge.cilib.entity.initialization.RandomInitializationStrategy;
import net.sourceforge.cilib.fss.feeding.FeedingStrategy;
import net.sourceforge.cilib.fss.feeding.StandardFeedingStrategy;
import net.sourceforge.cilib.fss.movement.individual.IndividualMovementProvider;
import net.sourceforge.cilib.fss.movement.individual.StandardIndividualMovementProvider;
import net.sourceforge.cilib.fss.weightinitialisation.StandardWeightInitialisationStrategy;
import net.sourceforge.cilib.fss.weightinitialisation.WeightInitialisationStrategy;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.solution.InferiorFitness;
import net.sourceforge.cilib.type.types.container.Vector;

public class StandardFish extends AbstractEntity implements Fish {
    
    private double weight = 0.0;
    private double deltaW = 0.0;
    private double deltaF = 0.0;
    private Vector deltaX;
    
    private ControlParameter minWeight;
    private ControlParameter maxWeight;
    
    private WeightInitialisationStrategy weightInitialisationStrategy;
    private IndividualMovementProvider individualMovement;
    private FeedingStrategy feedingStrategy;
    
    private InitializationStrategy<Fish> positionInitializationStrategy;
    
    public StandardFish() {
        this.deltaX = Vector.of();
        this.weightInitialisationStrategy = new StandardWeightInitialisationStrategy();
        this.positionInitializationStrategy = new RandomInitializationStrategy<Fish>();
        
        this.individualMovement = new StandardIndividualMovementProvider();        
        this.feedingStrategy = new StandardFeedingStrategy();
        this.minWeight = ConstantControlParameter.of(1);
        this.maxWeight = ConstantControlParameter.of(1000);
    }
    
    public StandardFish(StandardFish copy) {
        super(copy);
        
        this.weight = copy.weight;
        this.deltaF = copy.deltaF;
        this.deltaW = copy.deltaW;
        this.deltaX = Vector.copyOf(copy.deltaX);
        
        this.weightInitialisationStrategy = copy.weightInitialisationStrategy;
        this.individualMovement = copy.individualMovement;
        this.feedingStrategy = copy.feedingStrategy;
        this.positionInitializationStrategy = copy.positionInitializationStrategy.getClone();
        
        this.minWeight = copy.minWeight.getClone();
        this.maxWeight = copy.maxWeight.getClone();
    }

    @Override
    public Fish getClone() {
        return new StandardFish(this);
    }

    @Override
    public void calculateFitness() {
        getProperties().put(EntityType.PREVIOUS_FITNESS, getFitness());        
        getProperties().put(EntityType.FITNESS, this.getFitnessCalculator().getFitness(this));
    }

    @Override
    public void initialise(Problem problem) {
        this.weight = weightInitialisationStrategy.initialiseWeight(this);
        
        this.getProperties().put(EntityType.CANDIDATE_SOLUTION, problem.getDomain().getBuiltRepresentation().getClone());
        this.positionInitializationStrategy.initialize(EntityType.CANDIDATE_SOLUTION, this);
        
        this.getProperties().put(EntityType.FITNESS, InferiorFitness.instance());
        
        this.deltaX = Vector.fill(0.0, getCandidateSolution().size());
    }

    @Override
    public int getDimension() {
        return getCandidateSolution().size();
    }

    @Override
    public void reinitialise() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Entity t) {
        return getFitness().compareTo(t.getFitness());
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public Vector getDeltaX() {
        return deltaX;
    }

    @Override
    public void setDeltaX(Vector dx) {
        this.deltaX = dx;
    }

    @Override
    public double getDeltaF() {
        return deltaF;
    }

    @Override
    public void setDeltaF(double deltaF) {
        this.deltaF = deltaF;
    }

    @Override
    public ControlParameter getMaxWeight() {
        return maxWeight;
    }

    @Override
    public ControlParameter getMinWeight() {
        return minWeight;
    }

    @Override
    public void setMinWeight(ControlParameter minWeight) {
        this.minWeight = minWeight;
    }

    @Override
    public void setMaxWeight(ControlParameter maxWeight) {
        this.maxWeight = maxWeight;
    }

    @Override
    public double getDeltaW() {
        return deltaW;
    }

    @Override
    public void setDeltaW(double dw) {
        this.deltaW = dw;
    }

    @Override
    public IndividualMovementProvider getIndividualMovement() {
        return individualMovement;
    }

    @Override
    public void setIndividualMovement(IndividualMovementProvider individualMovement) {
        this.individualMovement = individualMovement;
    }

    @Override
    public void setFeedingStrategy(FeedingStrategy feedingStrategy) {
        this.feedingStrategy = feedingStrategy;
    }

    @Override
    public FeedingStrategy getFeedingStrategy() {
        return feedingStrategy;
    }

    public void setPositionInitializationStrategy(InitializationStrategy<Fish> positionInitializationStrategy) {
        this.positionInitializationStrategy = positionInitializationStrategy;
    }

    public InitializationStrategy<Fish> getPositionInitializationStrategy() {
        return positionInitializationStrategy;
    }

    public void setWeightInitialisationStrategy(WeightInitialisationStrategy weightInitialisationStrategy) {
        this.weightInitialisationStrategy = weightInitialisationStrategy;
    }

    public WeightInitialisationStrategy getWeightInitialisationStrategy() {
        return weightInitialisationStrategy;
    }
}
