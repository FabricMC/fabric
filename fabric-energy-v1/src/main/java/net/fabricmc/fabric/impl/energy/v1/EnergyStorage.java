package net.fabricmc.fabric.impl.energy.v1;

import net.fabricmc.fabric.api.energy.v1.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage {
	protected double storedEnergy = 0.0d;
	protected final double maxStoredEnergy;

	protected final double maxInsertion;
	protected final double maxExtraction;

	public EnergyStorage(double maxStoredEnergy, double maxInsertion, double maxExtraction) {
		this.maxStoredEnergy = maxStoredEnergy;
		this.maxInsertion = maxInsertion;
		this.maxExtraction = maxExtraction;
	}

	@Override
	public void insertEnergy(double energy) {
		if(canInsertEnergy(energy)) {
			storedEnergy += energy;
		}
	}

	@Override
	public double extractEnergy(double energy) {
		if(canExtractEnergy(energy)) {
			storedEnergy -= energy;

			return energy;
		}

		return 0.0d;
	}

	@Override
	public boolean canInsertEnergy(double energy) {
		return false;
	}

	@Override
	public boolean canExtractEnergy(double energy) {
		return false;
	}
}
