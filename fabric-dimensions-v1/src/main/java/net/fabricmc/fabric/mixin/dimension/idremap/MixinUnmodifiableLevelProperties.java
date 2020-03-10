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

package net.fabricmc.fabric.mixin.dimension.idremap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;

import net.fabricmc.fabric.impl.dimension.DimensionIdsHolder;

@Mixin(UnmodifiableLevelProperties.class)
public abstract class MixinUnmodifiableLevelProperties implements DimensionIdsHolder {
	@Shadow
	@Final
	private LevelProperties properties;

	/**
	 * Delegates to the main level properties.
	 */
	@Override
	public CompoundTag fabric_getDimensionIds() {
		return ((DimensionIdsHolder) this.properties).fabric_getDimensionIds();
	}
}
