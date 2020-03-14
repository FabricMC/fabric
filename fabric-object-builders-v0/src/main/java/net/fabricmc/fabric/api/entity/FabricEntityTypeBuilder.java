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

package net.fabricmc.fabric.api.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.impl.object.builder.FabricEntityType;

/**
 * Extended version of {@link EntityType.Builder} with added registration for
 * server-&gt;client entity tracking values.
 *
 * @param <T> Entity class.
 */
// TODO more javadocs
public class FabricEntityTypeBuilder<T extends Entity> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final EntityCategory category;
	private final EntityType.EntityFactory<T> function;
	private boolean saveable = true;
	private boolean summonable = true;
	private int trackingDistance = -1;
	private int updateIntervalTicks = -1;
	private Boolean alwaysUpdateVelocity;
	private boolean fireImmune = false;
	private boolean spawnableFarFromPlayer;
	private int maxDespawnDistance = 128;
	private int minDespawnDistance = 32;
	private EntityDimensions size = EntityDimensions.changing(-1.0f, -1.0f);

	protected FabricEntityTypeBuilder(EntityCategory category, EntityType.EntityFactory<T> function) {
		this.category = category;
		this.function = function;
		this.spawnableFarFromPlayer = category == EntityCategory.CREATURE || category == EntityCategory.MISC;
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(EntityCategory category) {
		return new FabricEntityTypeBuilder<>(category, (t, w) -> null);
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(EntityCategory category, EntityType.EntityFactory<T> function) {
		return new FabricEntityTypeBuilder<>(category, function);
	}

	public FabricEntityTypeBuilder<T> disableSummon() {
		this.summonable = false;
		return this;
	}

	public FabricEntityTypeBuilder<T> disableSaving() {
		this.saveable = false;
		return this;
	}

	public FabricEntityTypeBuilder<T> makeFireImmune() {
		this.fireImmune = true;
		return this;
	}

	public FabricEntityTypeBuilder<T> spawnableFarFromPlayer() {
		this.spawnableFarFromPlayer = true;
		return this;
	}

	public FabricEntityTypeBuilder<T> setDimensions(EntityDimensions size) {
		this.size = size;
		return this;
	}

	public FabricEntityTypeBuilder<T> setMaxDespawnDistance(int maxDespawnDistance) {
		this.maxDespawnDistance = maxDespawnDistance;
		return this;
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks) {
		return trackable(trackingDistanceBlocks, updateIntervalTicks, true);
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		this.trackingDistance = trackingDistanceBlocks;
		this.updateIntervalTicks = updateIntervalTicks;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		return this;
	}

	public EntityType<T> build() {
		if (this.saveable) {
			// SNIP! Modded datafixers are not supported anyway.
			// TODO: Flesh out once modded datafixers exist.
		}

		return new FabricEntityType<>(this.function, this.category, this.saveable, this.summonable, this.fireImmune, spawnableFarFromPlayer, maxDespawnDistance, minDespawnDistance, size, trackingDistance, updateIntervalTicks, alwaysUpdateVelocity);
	}

	/**
	 * @deprecated Old name that will be removed in a later version. Use {@link FabricEntityTypeBuilder#setDimensions(EntityDimensions size)}.
	 */
	@Deprecated
	public FabricEntityTypeBuilder<T> size(EntityDimensions size) {
		return setDimensions(size);
	}

	/**
	 * @deprecated Old name that will be removed in a later version. Use {@link FabricEntityTypeBuilder#makeFireImmune()}.
	 */
	@Deprecated
	public FabricEntityTypeBuilder<T> setImmuneToFire() {
		return makeFireImmune();
	}
}
