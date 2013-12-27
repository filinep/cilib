/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.cilib.measurement.single.diversity.normalisation;

import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 *
 * @author filipe
 */
public class SearchSpaceNormalisation implements DiversityNormalisation {

    @Override
    public double getNormalisationParameter(SinglePopulationBasedAlgorithm algorithm) {
        return ((Vector)algorithm.getOptimisationProblem().getDomain().getBuiltRepresentation()).length();
    }
    
}
