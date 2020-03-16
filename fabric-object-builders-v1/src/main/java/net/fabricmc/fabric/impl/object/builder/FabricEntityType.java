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

package net.fabricmc.fabric.impl.object.builder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;

public class FabricEntityType<T extends Entity> extends EntityType<T> {
	private final int maxTrackDistance, trackTickInterval;
	private final Boolean alwaysUpdateVelocity;

	public FabricEntityType(EntityFactory<T> entityFactory, EntityCategory entityCategory, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFromPlayer, EntityDimensions dimensions, int maxTrackDistance, int trackTickInterval, Boolean alwaysUpdateVelocity) {
		super(entityFactory, entityCategory, saveable, summonable, fireImmune, spawnableFromPlayer, dimensions);
		this.maxTrackDistance = maxTrackDistance;
		this.trackTickInterval = trackTickInterval;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
	}

	@Override
	public int getMaxTrackDistance() {
		if (this.maxTrackDistance != -1) {
			return (this.maxTrackDistance + 15) / 16;
		}

		return super.getMaxTrackDistance();
	}

	@Override
	public int getTrackTickInterval() {
		if (this.trackTickInterval != -1) {
			return this.trackTickInterval;
		}

		return super.getTrackTickInterval();
	}

	@Override
	public boolean alwaysUpdateVelocity() {
		if (this.alwaysUpdateVelocity != null) {
			return this.alwaysUpdateVelocity;
		}

		return super.alwaysUpdateVelocity();
	}
}
