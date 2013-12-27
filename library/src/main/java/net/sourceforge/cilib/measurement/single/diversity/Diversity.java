/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single.diversity;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.measurement.single.diversity.centerinitialisationstrategies.CenterInitialisationStrategy;
import net.sourceforge.cilib.measurement.single.diversity.centerinitialisationstrategies.GBestCenterInitialisationStrategy;
import net.sourceforge.cilib.measurement.single.diversity.normalisation.DiversityNormalisation;
import net.sourceforge.cilib.niching.NichingAlgorithm;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.distancemeasure.DistanceMeasure;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;
import net.sourceforge.cilib.entity.visitor.DiameterVisitor;
import net.sourceforge.cilib.measurement.single.diversity.normalisation.SearchSpaceNormalisation;

/**
 * TODO: Add JavaDoc.
 *
 */
public class Diversity implements Measurement<Real> {

    private static final long serialVersionUID = 7417526206433000209L;
    protected DistanceMeasure distanceMeasure;
    protected CenterInitialisationStrategy populationCenter;
    protected DiversityNormalisation normalisationParameter;

    public Diversity() {
        distanceMeasure = new EuclideanDistanceMeasure();
        populationCenter = new GBestCenterInitialisationStrategy();
        normalisationParameter = new SearchSpaceNormalisation();
    }

    public Diversity(Diversity other) {
        this.distanceMeasure = other.distanceMeasure;
        this.populationCenter = other.populationCenter;
        this.normalisationParameter = other.normalisationParameter;
    }

    @Override
    public Diversity getClone() {
        return new Diversity(this);
    }

    @Override
    public Real getValue(Algorithm algorithm) {
        fj.data.List<? extends Entity> topology;
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            topology = ((SinglePopulationBasedAlgorithm) algorithm).getTopology();
        } else {
            NichingAlgorithm na = (NichingAlgorithm) algorithm;
            topology = na.getMainSwarm().getTopology();
            for (SinglePopulationBasedAlgorithm s : na.getPopulations()) {
                topology = topology.append(s.getTopology());
            }
        }

        Vector center = populationCenter.getCenter(topology);
        double distanceSum = 0.0;

        for (Entity e : topology) {
            distanceSum += distanceMeasure.distance(center, e.getCandidateSolution());
        }

        distanceSum /= topology.length();
        distanceSum /= new DiameterVisitor().f(topology);

        return Real.valueOf(distanceSum);
    }

    /**
     * @return the distanceMeasure
     */
    public DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }

    /**
     * @param distanceMeasure the distanceMeasure to set
     */
    public void setDistanceMeasure(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }

    /**
     * @return the normalisationParameter
     */
    public DiversityNormalisation getNormalisationParameter() {
        return normalisationParameter;
    }

    /**
     * @param normalisationParameter the normalisationParameter to set
     */
    public void setNormalisationParameter(DiversityNormalisation normalisationParameter) {
        this.normalisationParameter = normalisationParameter;
    }

    /**
     * @return the populationCenter
     */
    public CenterInitialisationStrategy getPopulationCenter() {
        return populationCenter;
    }

    /**
     * @param populationCenter the populationCenter to set
     */
    public void setPopulationCenter(CenterInitialisationStrategy populationCenter) {
        this.populationCenter = populationCenter;
    }
}
