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

package net.fabricmc.fabric.api.fluid.v1;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Extends the FlowableFluid class with more customizations.
 */
public interface ExtendedFlowableFluid {
	/**
	 * @return true if the fluid can drown entities.
	 */
	default boolean canDrown() {
		return true;
	}

	/**
	 * @return true if the fluid can extinguish fire.
	 */
	default boolean canExtinguishFire() {
		return true;
	}

	/**
	 * @return true if the fluid can prevent fall damage.
	 */
	default boolean canPreventFallDamage() {
		return true;
	}

	/**
	 * @return true if the fluid is swimmable.
	 */
	default boolean isSwimmable() {
		return true;
	}

	/**
	 * Get the fog color.
	 * @param entity The current entity that displays the fog.
	 */
	int getFogColor(Entity entity);

	/**
	 * Get the fog ending value.
	 * @param entity The current entity that displays the fog.
	 */
	float getFogEnd(Entity entity);

	/**
	 * Get the fog starting value.
	 * @param entity The current entity that displays the fog.
	 */
	float getFogStart(Entity entity);

	/**
	 * @return the swim sound of the fluid.
	 */
	default Optional<SoundEvent> getSwimSound() {
		return Optional.empty();
	}

	/**
	 * Get the fluid viscosity, that is equal to the pushing strength of the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	double getViscosity(World world, Entity entity);

	/**
	 * @return true if the fluid is navigable.
	 */
	default boolean isNavigable() {
		return true;
	}

	/**
	 * Event executed when an entity falls, or enters, into the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	default void onSplash(World world, Entity entity) {}

	/**
	 * Event executed when the entity is into the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	default void onSubmerged(World world, Entity entity) {}

	/**
	 * Event executed when the entity is touching the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	default void onTouching(World world, Entity entity) {}
}
