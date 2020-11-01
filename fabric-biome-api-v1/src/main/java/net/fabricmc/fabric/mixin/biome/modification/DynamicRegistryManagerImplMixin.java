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

package net.fabricmc.fabric.mixin.biome.modification;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.modification.BiomeModificationTracker;

/**
 * This Mixin allows us to track which biomes already ran through {@link net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl}
 * on a per-DynamicRegistryManager basis.
 */
@Mixin(DynamicRegistryManager.Impl.class)
public class DynamicRegistryManagerImplMixin implements BiomeModificationTracker {
	@Unique
	private final Set<RegistryKey<Biome>> modifiedBiomes = new HashSet<>();

	@Override
	public Set<RegistryKey<Biome>> fabric_getModifiedBiomes() {
		return this.modifiedBiomes;
	}
}
