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

package net.fabricmc.fabric.mixin.object.builder;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;

@Mixin(VillagerType.class)
public interface VillagerTypeAccessor {
	@Accessor("BIOME_TO_TYPE")
	static Map<RegistryKey<Biome>, VillagerType> getBiomeTypeToIdMap() {
		throw new AssertionError("Untransformed Accessor!");
	}

	// FIXME: This should be called `register` in yarn
	@Invoker("create")
	static VillagerType callRegister(String id) {
		throw new AssertionError("Untransformed Accessor!");
	}
}
