/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fj.F;
import fj.data.Java;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.topologies.SpeciationNeighbourhood;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.type.types.container.Vector;

public class LocalNiches implements Measurement<TypeList> {

    private List<Particle> niches;
    private SpeciationNeighbourhood neighbourhood;
    private Double error;
    private Boolean useMemoryInformation;

    public LocalNiches() {
        this.neighbourhood = new SpeciationNeighbourhood();
        this.error = 1e-4;
        this.useMemoryInformation = true;
    }

    private LocalNiches(LocalNiches copy) {
        this.niches = copy.niches;
        this.neighbourhood = copy.neighbourhood;
        this.error = copy.error;
        this.useMemoryInformation = copy.useMemoryInformation;
    }

    @Override
    public LocalNiches getClone() {
        return new LocalNiches(this);
    }

    @Override
    public TypeList getValue(Algorithm algorithm) {
        niches = Lists.newArrayList();

        if (algorithm instanceof NichingAlgorithm) {
            NichingAlgorithm pba = (NichingAlgorithm) algorithm;
            Iterables.addAll(niches, pba.getTopology());

            for (SinglePopulationBasedAlgorithm p : pba.getPopulations()) {
                Iterables.addAll(niches, p.getTopology());
            }
        } else {
            SinglePopulationBasedAlgorithm pba = (SinglePopulationBasedAlgorithm) algorithm;
            Iterables.addAll(niches, pba.getTopology());
        }

        if (useMemoryInformation) {
            for (int i = 0; i < niches.size(); i++) {
                if (niches.get(i) instanceof Particle) {
                    Particle p = (Particle) niches.get(i);
                    Particle clone = p.getClone();
                    clone.setCandidateSolution(clone.getBestPosition());
                    clone.getProperties().put(EntityType.Particle.BEST_FITNESS, clone.getBestFitness());
                    niches.set(i, clone);
                }
            }
        }

        neighbourhood.setNeighbourhoodSize(ConstantControlParameter.of(niches.size()));
        Problem d = algorithm.getOptimisationProblem();
        FunctionOptimisationProblem fop = (FunctionOptimisationProblem)d;
        final Gradient df = (Gradient)fop.getFunction();
        
        ArrayList<Particle> es = Java.<Particle>List_ArrayList().f(fj.data.List.iterableList(Topologies.getNeighbourhoodBestEntities(fj.data.List.iterableList(niches), neighbourhood))
            .filter(new F<Particle, Boolean>() {
                @Override
                public Boolean f(Particle a) {
                    return df.GetGradientVectorLength((Vector)a.getCandidateSolution()) < error;
                }
            }));
        TypeList t = new TypeList();
        for (Entity e : es) {
            t.add(e.getCandidateSolution());
        }
        return t;
    }

    public void setRadius(double r) {
        neighbourhood.setRadius(ConstantControlParameter.of(r));
    }

    public void setError(Double error) {
        this.error = error;
    }
}
