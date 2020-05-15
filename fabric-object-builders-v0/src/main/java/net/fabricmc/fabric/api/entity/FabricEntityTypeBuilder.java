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

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * @deprecated Please migrate to v1. Please use {@link net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder} instead.
 */
@Deprecated
public class FabricEntityTypeBuilder<T extends Entity> {
	private final net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder<T> delegate;

	protected FabricEntityTypeBuilder(SpawnGroup spawnGroup, EntityType.EntityFactory<T> function) {
		this.delegate = net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.create(spawnGroup, function);
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(SpawnGroup spawnGroup) {
		return new FabricEntityTypeBuilder<>(spawnGroup, (t, w) -> null);
	}

	/**
	 * @deprecated Use {@link FabricEntityTypeBuilder#create(SpawnGroup, EntityType.EntityFactory)}
	 */
	@Deprecated
	public static <T extends Entity> FabricEntityTypeBuilder<T> create(SpawnGroup spawnGroup, Function<? super World, ? extends T> function) {
		return create(spawnGroup, (t, w) -> function.apply(w));
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(SpawnGroup spawnGroup, EntityType.EntityFactory<T> function) {
		return new FabricEntityTypeBuilder<>(spawnGroup, function);
	}

	public FabricEntityTypeBuilder<T> disableSummon() {
		this.delegate.disableSummon();
		return this;
	}

	public FabricEntityTypeBuilder<T> disableSaving() {
		this.delegate.disableSaving();
		return this;
	}

	public FabricEntityTypeBuilder<T> setImmuneToFire() {
		this.delegate.fireImmune();
		return this;
	}

	/**
	 * @deprecated Use {@link FabricEntityTypeBuilder#size(EntityDimensions)}
	 */
	@Deprecated
	public FabricEntityTypeBuilder<T> size(float width, float height) {
		this.delegate.dimensions(EntityDimensions.changing(width, height));
		return this;
	}

	public FabricEntityTypeBuilder<T> size(EntityDimensions size) {
		this.delegate.dimensions(size);
		return this;
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks) {
		this.delegate.trackable(trackingDistanceBlocks, updateIntervalTicks);
		return this;
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		this.delegate.trackable(trackingDistanceBlocks, updateIntervalTicks, alwaysUpdateVelocity);
		return this;
	}

	public EntityType<T> build() {
		return this.delegate.build();
	}
}
