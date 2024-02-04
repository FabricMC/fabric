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

package net.fabricmc.fabric.impl.client.rendering;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.UnmodifiableView;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.biome.ColorResolver;

public final class ColorResolverRegistryImpl {
	private static final Set<ColorResolver> RESOLVERS = new HashSet<>();
	private static final Set<ColorResolver> RESOLVERS_VIEW = Collections.unmodifiableSet(RESOLVERS);

	static {
		register(BiomeColors.GRASS_COLOR);
		register(BiomeColors.FOLIAGE_COLOR);
		register(BiomeColors.WATER_COLOR);
	}

	private ColorResolverRegistryImpl() {
	}

	public static void register(ColorResolver resolver) {
		RESOLVERS.add(resolver);
	}

	@UnmodifiableView
	public static Set<ColorResolver> getAllRegistered() {
		return RESOLVERS_VIEW;
	}
}
