package net.sourceforge.cilib.pso.particle;

import net.sourceforge.cilib.pso.crossover.CrossoverReplaceFunction;

public class CrossoverParticleBehavior extends ParticleBehavior {
	private CrossoverReplaceFunction crossover;
	
	public CrossoverParticleBehavior() {
	}
	
	public CrossoverParticleBehavior(CrossoverParticleBehavior copy) {
		super(copy);
		this.crossover = copy.crossover;
	}
	
	public CrossoverReplaceFunction getCrossover() {
		return crossover;
	}
	
	public void setCrossover(CrossoverReplaceFunction crossover) {
		this.crossover = crossover;
	}
	
	@Override
	public CrossoverParticleBehavior getClone() {
		return this;
	}
}
