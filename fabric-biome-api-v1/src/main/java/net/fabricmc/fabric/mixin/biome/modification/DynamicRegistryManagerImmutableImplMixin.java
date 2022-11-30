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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.registry.DynamicRegistryManager;

import net.fabricmc.fabric.impl.biome.modification.BiomeModificationMarker;

/**
 * This Mixin allows us to keep backup copies of biomes for
 * {@link net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl} on a per-DynamicRegistryManager basis.
 */
@Mixin(DynamicRegistryManager.ImmutableImpl.class)
public class DynamicRegistryManagerImmutableImplMixin implements BiomeModificationMarker {
	@Unique
	private boolean modified;

	@Override
	public void fabric_markModified() {
		if (modified) {
			throw new IllegalStateException("This dynamic registries instance has already been modified");
		}

		modified = true;
	}
}
