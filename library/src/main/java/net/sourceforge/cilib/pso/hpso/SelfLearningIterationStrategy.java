/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.hpso;

import fj.Ord;
import static fj.data.List.iterableList;
import static fj.function.Doubles.sum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import static net.sourceforge.cilib.entity.EntityType.Particle.*;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.math.random.generator.Rand;
import static net.sourceforge.cilib.niching.VectorBasedFunctions.*;
import net.sourceforge.cilib.problem.solution.Fitness;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.pso.particle.ParticleBehavior;
import net.sourceforge.cilib.pso.particle.StandardParticle;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;
import net.sourceforge.cilib.util.selection.Samples;
import net.sourceforge.cilib.util.selection.recipes.RandomSelector;
import net.sourceforge.cilib.util.selection.recipes.RouletteWheelSelector;
import net.sourceforge.cilib.util.selection.recipes.Selector;
import net.sourceforge.cilib.util.selection.weighting.ParticleBehaviorWeighting;
import net.sourceforge.cilib.util.selection.weighting.SpecialisedRatio;

/**
 * Li and Yang's Adaptive Learning PSO-II (ALPSO-II)
 * <p>
 * References:
 * </p>
 * <ul><li>
 * Changhe Li; Shengxiang Yang; , "Adaptive learning particle swarm optimizer-II for global optimization,"
 * Evolutionary Computation (CEC), 2010 IEEE Congress on , pp.1-8, 2010.
 * </li></ul>
 */
public class SelfLearningIterationStrategy extends AbstractIterationStrategy<PSO> implements HeterogeneousIterationStrategy {
    
    private Selector<ParticleBehavior> behaviorSelectionRecipe;
    private List<ParticleBehavior> behaviorPool;
    private SpecialisedRatio weighting;
    private ControlParameter minRatio;
    private ControlParameter q;
    private Particle aBest;

    private enum Props {
        PROPS;
    }

    private class ParticleProperties implements Type {

        public List<Double> selectionRatio;
        public List<Double> progress;
        public List<Double> reward;
        public List<Double> success;
        public List<Double> selected;

        public void incrementSuccess(int i) {
            success.set(i, success.get(i) + 1);
        }

        public void incrementSelected(int i) {
            selected.set(i, selected.get(i) + 1);
        }

        public double updateFrequency;
        public double learningProbability;
        public double improvRatio;
        public double stagnation;

        @Override
        public ParticleProperties getClone() {
            return new ParticleProperties();
        }
    }

    public SelfLearningIterationStrategy() {
        this.minRatio = ConstantControlParameter.of(0.01);
        this.behaviorPool = new ArrayList<>();
        this.weighting = new SpecialisedRatio();
        this.weighting.setBehaviors(behaviorPool);
        this.behaviorSelectionRecipe = new RouletteWheelSelector<>(new ParticleBehaviorWeighting(weighting));
        this.aBest = new StandardParticle();
        this.q = ConstantControlParameter.of(10);
    }

    public SelfLearningIterationStrategy(SelfLearningIterationStrategy copy) {
        super(copy);
        this.minRatio = copy.minRatio.getClone();
        this.behaviorPool = new ArrayList<>(copy.behaviorPool);
        this.weighting = copy.weighting;
        this.behaviorSelectionRecipe = copy.behaviorSelectionRecipe;
        this.aBest = copy.aBest.getClone();
        this.q = copy.q.getClone();
    }

    @Override
    public SelfLearningIterationStrategy getClone() {
        return new SelfLearningIterationStrategy(this);
    }

    private ParticleProperties get(Particle p) {
        return (ParticleProperties)p.getProperties().get(Props.PROPS);
    }

    @Override
    public void performIteration(PSO algorithm) {
        fj.data.List<Particle> topology = algorithm.getTopology();

        if (algorithm.getIterations() == 0) {
            initialise(topology, behaviorPool.size());
        }

        Ord<Particle> ord = Ord.ord(sortByDistance(aBest, new EuclideanDistanceMeasure()));
        fj.data.List<Particle> canLearnFromAbest = iterableList(topology)
                .sort(ord).take((int) q.getParameter());

        for(Particle particle : topology) {
            ParticleProperties props = get(particle);
            boolean canLearn = canLearnFromAbest.exists(equalParticle.f(particle));
            double oldWeight = props.selectionRatio.get(0);

            if (!canLearn) {
                props.selectionRatio.set(0, 0.0);
            }

            //get behavior
            weighting.setWeights(props.selectionRatio);

            ParticleBehavior behavior = behaviorSelectionRecipe.on(behaviorPool).select();
            particle.setParticleBehavior(behavior);

            int i = behaviorPool.indexOf(behavior);
            props.incrementSelected(i);

            if (!canLearn) {
                props.selectionRatio.set(0, oldWeight);
            }

            Fitness prevFitness = particle.getFitness();
            Fitness prevPBest = (Fitness) particle.getProperties().get(BEST_FITNESS);

            //update particle
            particle.updateVelocity();
            particle.updatePosition();

            boundaryConstraint.enforce(particle);
            particle.calculateFitness();

            //if particle improved
            if (particle.getFitness().compareTo(prevFitness) > 0) {
                props.stagnation = 0;
                props.incrementSuccess(i);
                props.progress.set(i, props.progress.get(i)
                    + Math.max(fitnessDifference(particle.getFitness(), prevFitness), 0));
                
                double progressSum = sum(iterableList(props.progress));
                double alpha = Rand.nextDouble();
                double penalty = props.success.get(i) == 0
                        && props.selectionRatio.get(i) == Collections.max(props.selectionRatio)
                        ? 0.9 : 1.0;

                props.reward.set(i, props.reward.get(i) +
                    progressSum != 0 && props.selected.get(i) != 0
                    ? props.progress.get(i) * alpha / progressSum
                        + (1 - alpha) * props.success.get(i) / props.selected.get(i)
                        + penalty * props.selectionRatio.get(i)
                    : penalty * props.selectionRatio.get(i)
                );

                //gbestupdate with abest
                for(int j = 0; j < particle.getDimension(); j++) {
                    if(Rand.nextDouble() < props.learningProbability) {
                        Particle aBestClone = aBest.getClone();
                        Vector aBestVector = (Vector) aBestClone.getBestPosition();

                        aBestVector.setReal(j, ((Vector)particle.getPosition()).doubleValueOf(j));
                        aBestClone.setCandidateSolution(aBestVector);
                        Fitness fitness = particle.getFitnessCalculator().getFitness(aBestClone);

                        if(fitness.compareTo(aBest.getBestFitness()) > 0) {
                            aBest.getProperties().put(BEST_POSITION, aBestVector);
                            aBest.getProperties().put(BEST_FITNESS, fitness);
                        }
                    }
                }
            } else {
                props.stagnation += 1.0;
            }

            //if pbest improved
            if(particle.getFitness().compareTo(particle.getBestFitness()) == 0) {
                //set abest
                if(aBest.getBestFitness().compareTo(particle.getFitness()) < 0) {
                    aBest.getProperties().put(BEST_POSITION, particle.getPosition().getClone());
                    aBest.getProperties().put(BEST_FITNESS, particle.getFitness().getClone());
                }
            }

            //if m_{k} >= U_{f}^{k}
            if (props.stagnation >= props.updateFrequency) {
                props.stagnation = 0;
                double rewardSum = sum(iterableList(props.reward));
                
                //update selection ratios
                for(int j = 0; j < behaviorPool.size(); j++) {
                    props.selectionRatio.set(j,
                            (rewardSum == 0 ? 0 : props.reward.get(j) / rewardSum)
                            * (1 - behaviorPool.size() * minRatio.getParameter())
                            + minRatio.getParameter());
                }
                //reset
                initAdaptiveProperties(props);
            }

        }

        List<Particle> perm = new RandomSelector<Particle>().on(topology).select(Samples.all());
        for(int k = 0; k < perm.size(); k++) {
            Particle p = perm.get(k);
            get(p).learningProbability = Math.max(1-Math.exp(-Math.pow(1.6*k/perm.size(), 4)), 0.05);
            get(p).updateFrequency = Math.max(10*Math.exp(-Math.pow(1.6*k/perm.size(),4)), 1);
        }

        //set one particle's best to abest so it can be measured and the rests neighbourhood best to abest
        topology.head().getProperties().put(BEST_FITNESS, aBest.getBestFitness());
        topology.head().getProperties().put(BEST_POSITION, aBest.getBestPosition());
    }

    private double fitnessDifference(Fitness newF, Fitness oldF) {
        return newF.compareTo(oldF) *
                Math.abs((newF.getValue().isNaN() ? 0 : newF.getValue()) - (oldF.getValue().isNaN() ? 0 : oldF.getValue()));
    }

    private List<Double> resetList(double n) {
        List<Double> l = Collections.nCopies(behaviorPool.size(), n);
        return new ArrayList<>(l);
    }

    private void initialise(fj.data.List<Particle> topology, int poolSize) {
        aBest = Topologies.getBestEntity(topology).getClone();

        for(int k = 0; k < topology.length(); k++) {
            Particle p = topology.index(k);
            ParticleProperties props = new ParticleProperties();

            props.updateFrequency = Math.max(10*Math.exp(-Math.pow(1.6*k/topology.length(),4)), 1);
            props.learningProbability = Math.max(1-Math.exp(-Math.pow(1.6*k/topology.length(), 4)), 0.05);
            props.stagnation = 0;
            props.improvRatio = 0.0;

            initAdaptiveProperties(props);

            props.selectionRatio = resetList(1.0 / poolSize);

            p.getProperties().put(Props.PROPS, props);
            p.setNeighbourhoodBest(aBest);
        }
    }

    private void initAdaptiveProperties(ParticleProperties props) {
        props.progress = resetList(0);
        props.reward = resetList(0);
        props.selected = resetList(0);
        props.success = resetList(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBehavior(ParticleBehavior behavior) {
        behaviorPool.add(behavior);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBehaviorPool(List<ParticleBehavior> pool) {
        behaviorPool = pool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParticleBehavior> getBehaviorPool() {
        return behaviorPool;
    }

    public void setMinRatio(ControlParameter minRatio) {
        this.minRatio = minRatio;
    }

    public ControlParameter getMinRatio() {
        return minRatio;
    }

    public void setQ(ControlParameter q) {
        this.q = q;
    }

    public ControlParameter getQ() {
        return q;
    }
}
