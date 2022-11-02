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

package net.fabricmc.fabric.impl.biome.modification;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.class_7871;
import net.minecraft.class_7887;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * Utility class for accessing the worldgen data that vanilla uses to generate its vanilla datapack.
 */
@ApiStatus.Internal
public final class BuiltInRegistryKeys {
	private static final CommandRegistryWrapper.class_7874 vanillaRegistries = class_7887.method_46817();

	private BuiltInRegistryKeys() {
	}

	public static boolean isBuiltinBiome(RegistryKey<Biome> key) {
		return vanillaRegistries.method_46762(Registry.BIOME_KEY).method_46746(key).isPresent();
	}

	public static class_7871<Biome> biomeRegistryWrapper() {
		return vanillaRegistries.method_46762(Registry.BIOME_KEY);
	}
}
