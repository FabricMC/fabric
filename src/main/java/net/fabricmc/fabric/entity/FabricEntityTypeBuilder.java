/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.world.World;

import java.util.function.Function;

// TODO: javadocs
public class FabricEntityTypeBuilder<T extends Entity> {
	private final Class<? extends T> entityClass;
	private final Function<? super World, ? extends T> function;
	private boolean saveable = true;
	private boolean summonable = true;
	private int trackingDistance = -1;
	private int updateIntervalTicks = -1;
	private boolean alwaysUpdateVelocity = true;

	protected FabricEntityTypeBuilder(Class<? extends T> entityClass, Function<? super World, ? extends T> function) {
		this.entityClass = entityClass;
		this.function = function;
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(Class<? extends T> entityClass) {
		return new FabricEntityTypeBuilder<>(entityClass, (w) -> null);
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(Class<? extends T> entityClass, Function<? super World, ? extends T> function) {
		return new FabricEntityTypeBuilder<>(entityClass, function);
	}

	public FabricEntityTypeBuilder<T> disableSummon() {
		this.summonable = false;
		return this;
	}

	public FabricEntityTypeBuilder<T> disableSaving() {
		this.saveable = false;
		return this;
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistance, int updateIntervalTicks) {
		return trackable(trackingDistance, updateIntervalTicks, true);
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		this.trackingDistance = trackingDistance;
		this.updateIntervalTicks = updateIntervalTicks;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		return this;
	}

	@Deprecated
	public EntityType<T> build(String id) {
		return build();
	}

	public EntityType<T> build() {
		if (this.saveable) {
			// SNIP! Modded datafixers are not supported anyway.
			// TODO: Flesh out once modded datafixers exist.
		}

		EntityType<T> type = new EntityType<>(this.entityClass, this.function, this.saveable, this.summonable, null);
		if (trackingDistance != -1) {
			EntityTrackingRegistry.INSTANCE.register(type, trackingDistance, updateIntervalTicks, alwaysUpdateVelocity);
		}
		return type;
	}
}
