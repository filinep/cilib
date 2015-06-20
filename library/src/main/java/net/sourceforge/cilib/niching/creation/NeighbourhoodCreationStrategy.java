/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.niching.creation;

import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.controlparameter.LinearlyVaryingControlParameter;
import net.sourceforge.cilib.controlparameter.UpdateOnIterationControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.Property;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.comparator.SocialBestFitnessComparator;
import net.sourceforge.cilib.entity.visitor.RadiusVisitor;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.measurement.generic.Iterations;
import net.sourceforge.cilib.measurement.single.diversity.centerinitialisationstrategies.GBestCenterInitialisationStrategy;
import net.sourceforge.cilib.niching.NichingSwarms;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.problem.boundaryconstraint.ClampingBoundaryConstraint;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.behaviour.StandardParticleBehaviour;
import net.sourceforge.cilib.pso.iterationstrategies.SynchronousIterationStrategy;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.pso.velocityprovider.ClampingVelocityProvider;
import net.sourceforge.cilib.pso.velocityprovider.GCVelocityProvider;
import net.sourceforge.cilib.pso.velocityprovider.StandardVelocityProvider;
import net.sourceforge.cilib.stoppingcondition.Maximum;
import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;
import net.sourceforge.cilib.util.functions.Entities;
import net.sourceforge.cilib.util.functions.Particles;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.P1;
import fj.data.List;
import fj.function.Integers;

public class NeighbourhoodCreationStrategy extends NicheCreationStrategy {

    private ControlParameter m;
    private ControlParameter r;
    private ControlParameter delta;

    public NeighbourhoodCreationStrategy() {
        this.swarmType = new PSO();
        ((SynchronousIterationStrategy) ((PSO) this.swarmType).getIterationStrategy()).setBoundaryConstraint(new ClampingBoundaryConstraint());
        this.swarmType.addStoppingCondition(new MeasuredStoppingCondition(new Iterations(), new Maximum(), 500));

        ClampingVelocityProvider delegate = new ClampingVelocityProvider(ConstantControlParameter.of(1.0),
                new StandardVelocityProvider(new UpdateOnIterationControlParameter(new LinearlyVaryingControlParameter(0.7, 0.2)),
                    ConstantControlParameter.of(1.2), ConstantControlParameter.of(1.2)));

        GCVelocityProvider gcVelocityProvider = new GCVelocityProvider();
        gcVelocityProvider.setDelegate(delegate);
        gcVelocityProvider.setRho(ConstantControlParameter.of(0.01));

        this.swarmBehavior = new StandardParticleBehaviour();
        ((StandardParticleBehaviour) this.swarmBehavior).setVelocityProvider(gcVelocityProvider);

        this.m = ConstantControlParameter.of(5);
        this.r = ConstantControlParameter.of(1);
        this.delta = ConstantControlParameter.of(0.1);
    }

    private int countParticles(NichingSwarms a) {
        return Integers.sum(a.getSubswarms().cons(a.getMainSwarm()).map(new F<SinglePopulationBasedAlgorithm, Integer>() {
                @Override
                public Integer f(SinglePopulationBasedAlgorithm s) {
                    return s.getTopology().length();
                }
            }));
    }

    @Override
    public NichingSwarms f(NichingSwarms a, final Entity b) {
        final Particle p = (Particle) b;

        final F<SinglePopulationBasedAlgorithm, Boolean> worseSwarm = new F<SinglePopulationBasedAlgorithm, Boolean>() {
                @Override
                public Boolean f(SinglePopulationBasedAlgorithm a) {
                    Particle best = (Particle) Topologies.getBestEntity(a.getTopology(), new SocialBestFitnessComparator());
                    double d = new EuclideanDistanceMeasure().distance(p.getBestPosition(), best.getBestPosition());
                    boolean dod = p.getBestFitness().compareTo(best.getBestFitness()) > 0 && d < delta.getParameter();
                    return dod;
                }
            };

        List<SinglePopulationBasedAlgorithm> subSwarms = a.getSubswarms().removeAll(worseSwarm);
        SinglePopulationBasedAlgorithm newMainSwarm = a.getMainSwarm();

        final List<Entity> wholeSpecies = a.getMainSwarm().getTopology().filter(new F<Particle, Boolean>() {
                public Boolean f(Particle a) {
                    return p.getNeighbourhoodBest() == a.getNeighbourhoodBest();
                }
            }).sort(Ord.ord(new F2<Particle, Particle, Ordering>() {
                    @Override
                    public Ordering f(Particle a, Particle b) {
                        return Ordering.values()[-a.getBestFitness().compareTo(b.getBestFitness()) + 1];
                    }
                }.curry()));

        if (a.getSubswarms().exists(worseSwarm)) {
            wholeSpecies.map(Entities.reinitialise());
        } else {

            List<Entity> bestSpecies = wholeSpecies.take((int) m.getParameter());
            final List<Entity> redundant = wholeSpecies.drop(bestSpecies.length());

            final UniformDistribution uniform = new UniformDistribution();
            final RadiusVisitor radiusV = new RadiusVisitor();
            radiusV.setPopulationCenter(new GBestCenterInitialisationStrategy());
            final double radius = radiusV.f((List<Entity>) bestSpecies);
            final Vector base = (Vector) p.getBestPosition();

            final P1<Number> randRange = new P1<Number>() {
                    @Override
                    public Number _1() {
                        if (wholeSpecies.tail().isEmpty())
                            return uniform.getRandomNumber(-r.getParameter(), r.getParameter());
                        return uniform.getRandomNumber(-radius, radius);
                    }
                };

            int needed = (int) m.getParameter() - bestSpecies.length();
            if (needed > 0) {
                List<Entity> extras = List.range(0, needed).map(new F<Integer,Entity>() {
                        public Entity f(Integer a) {
                            Vector newPos = base.plus(Vector.newBuilder().repeat(base.size(), Real.valueOf(1.0)).build().multiply(randRange));
                            Vector newVel = Vector.newBuilder().repeat(base.size(), Real.valueOf(1.0)).build().multiply(randRange);
                            Particle newP = p.getClone();

                            newP.setPosition(newPos);
                            newP.put(Property.VELOCITY, newVel);
                            newP.updateFitness(newP.getBehaviour().getFitnessCalculator().getFitness(newP));
                            newP.setNeighbourhoodBest(newP);
                            newP.put(Property.BEST_FITNESS, newP.getFitness());
                            newP.put(Property.BEST_POSITION, newP.getPosition());

                            return newP;
                        }
                    });
                bestSpecies = bestSpecies.append(extras);
            }

            // Create new subswarm
            final SinglePopulationBasedAlgorithm newSubSwarm = swarmType.getClone();
            newSubSwarm.setOptimisationProblem(a.getMainSwarm().getOptimisationProblem());
            newSubSwarm.setTopology(bestSpecies.map(new F<Entity, Entity>() {
                    @Override
                    public Entity f(Entity e) {
                        Particle pp = (Particle) e.getClone();
                        pp.setBehaviour(swarmBehavior.getClone());
                        pp.setNeighbourhoodBest(pp);
                        return pp;
                    }
                }
                    ));
            subSwarms = subSwarms.cons(newSubSwarm);

            // Create new mainswarm
            final int requiredParticles = a.getMainSwarm().getInitialisationStrategy().getEntityNumber();
            final int currentParticles = countParticles(a);
            final int neededParticles = requiredParticles - currentParticles;
            final Entity proto = a.getMainSwarm().getInitialisationStrategy().getEntityType();
            final Problem problem = a.getMainSwarm().getOptimisationProblem();

            newMainSwarm = a.getMainSwarm().getClone();
            newMainSwarm.setTopology(a.getMainSwarm().getTopology().filter(new F<Entity, Boolean>() {
                    @Override
                    public Boolean f(final Entity e) {
                        return !wholeSpecies.exists(new F<Entity,Boolean>() {
                                @Override
                                public Boolean f(final Entity a) {
                                    return e == a;
                                }
                            });
                    }
                })
                .append(redundant.map(Entities.<Entity>reinitialise().andThen(new F<Entity, Entity>() {
                        @Override
                        public Entity f(Entity a) {
                            Particle p = (Particle) a;
                            p.setNeighbourhoodBest(p);
                            return p;
                        }
                    })))
                .append(List.range(0, neededParticles).map(new F<Integer, Entity>() {
                        @Override
                        public Entity f(Integer i) {
                            Entity pe = proto.getClone();
                            pe.initialise(problem);
                            return pe;
                        }
                        }.andThen(Entities.<Entity>evaluate())))
                );
        }

        return NichingSwarms.of(newMainSwarm, subSwarms);
    }

    /**
     * @return the m
     */
    public ControlParameter getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setM(ControlParameter m) {
        this.m = m;
    }

    /**
     * @return the r
     */
    public ControlParameter getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(ControlParameter r) {
        this.r = r;
    }

    /**
     * @return the delta
     */
    public ControlParameter getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(ControlParameter delta) {
        this.delta = delta;
    }

}
