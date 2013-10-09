/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.controlparameter;

import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.functions.NichingFunction;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;

public class NichingRadiusControlParameter implements ControlParameter {

    @Override
    public double getParameter() {
        return ((NichingFunction) ((FunctionOptimisationProblem) AbstractAlgorithm.get().getOptimisationProblem()).getFunction()).getNicheRadius();
    }

    @Override
    public double getParameter(double min, double max) {
        return getParameter();
    }

    @Override
    public ControlParameter getClone() {
        return this;
    }

}
