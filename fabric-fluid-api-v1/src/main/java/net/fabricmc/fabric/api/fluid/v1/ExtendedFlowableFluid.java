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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Extends the FlowableFluid class with more customizations.
 */
public interface ExtendedFlowableFluid {
	/**
	 * Get the fog color.
	 * @param focusedEntity The current entity that displays the fog.
	 */
	int getFogColor(Entity focusedEntity);

	/**
	 * Get the fog ending value.
	 * @param focusedEntity The current entity that displays the fog.
	 */
	float getFogEnd(Entity focusedEntity);

	/**
	 * Get the fog starting value.
	 * @param focusedEntity The current entity that displays the fog.
	 */
	float getFogStart(Entity focusedEntity);

	/**
	 * Get the fluid splash sound.
	 */
	Optional<SoundEvent> getSplashSound();

	/**
	 * Get the fluid pushing strength.
	 */
	double getStrength();

	/**
	 * Event executed when an entity falls, or enters, into the fluid.
	 * @param world The current world.
	 * @param pos The position where the current entity splashed.
	 * @param entity The current entity that caused the event.
	 */
	void onSplash(World world, Vec3d pos, Entity entity);
}
