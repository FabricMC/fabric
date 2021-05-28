/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.energy.v1;

import net.fabricmc.fabric.api.energy.v1.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage {
	protected double storedEnergy = 0.0d;
	protected final double maxStoredEnergy;

	protected final boolean canInsert;
	protected final boolean canExtract;
	protected final double maxInsertion;
	protected final double maxExtraction;

	public EnergyStorage(double maxStoredEnergy, boolean canInsert, boolean canExtract, double maxInsertion, double maxExtraction) {
		this.maxStoredEnergy = maxStoredEnergy;
		this.canInsert = canInsert;
		this.canExtract = canExtract;
		this.maxInsertion = maxInsertion;
		this.maxExtraction = maxExtraction;
	}

	public EnergyStorage(double maxStoredEnergy, double maxInsertion, double maxExtraction) {
		this.maxStoredEnergy = maxStoredEnergy;
		this.canInsert = true;
		this.canExtract = true;
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
		double insertedEnergy = storedEnergy + energy;

		return ( (canInsert) & (energy <= maxInsertion) & (insertedEnergy <= maxStoredEnergy) );
	}

	@Override
	public boolean canExtractEnergy(double energy) {
		return ( (canExtract) & (energy <= maxExtraction) & (energy <= maxStoredEnergy) );
	}
}
