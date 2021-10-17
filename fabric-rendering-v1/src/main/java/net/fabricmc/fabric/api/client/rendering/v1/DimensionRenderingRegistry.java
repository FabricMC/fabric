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

package net.fabricmc.fabric.api.client.rendering.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.DimensionRenderingRegistryImpl;

/**
 * Dimensional renderers render world specific visuals of a world.
 * They may be used to render the sky, weather, or clouds.
 * The {@link SkyProperties} is the vanilla dimensional renderer.
 */
@Environment(EnvType.CLIENT)
public interface DimensionRenderingRegistry {
	/**
	 * Sets the custom sky renderer for a {@link World}.
	 *
	 * <p>This overrides Vanilla's sky rendering.
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @param renderer A {@link SkyRenderer} implementation
	 */
	static void registerSkyRenderer(RegistryKey<World> key, SkyRenderer renderer) {
		DimensionRenderingRegistryImpl.registerSkyRenderer(key, renderer);
	}

	/**
	 * Registers a custom weather renderer for a {@link World}.
	 *
	 * <p>This overrides Vanilla's weather rendering.
	 * @param key A RegistryKey for your {@link World}
	 * @param renderer A {@link WeatherRenderer} implementation
	 */
	static void registerWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer) {
		DimensionRenderingRegistryImpl.registerWeatherRenderer(key, renderer);
	}

	/**
	 * Registers a sky property for a {@link net.minecraft.util.Identifier}.
	 *
	 * <p>This registers a new option for the "effects" entry of the dimension type json.
	 *
	 * @param key        The {@link net.minecraft.util.Identifier} for the new option entry.ide
	 * @param properties The {@link SkyProperties} option.
	 */
	static void registerSkyProperties(Identifier key, SkyProperties properties) {
		DimensionRenderingRegistryImpl.registerSkyProperties(key, properties);
	}

	/**
	 * Registers a custom cloud renderer for a {@link World}.
	 *
	 * <p>This overrides Vanilla's cloud rendering.
	 *
	 * @param key      A {@link RegistryKey} for your {@link World}
	 * @param renderer A {@link CloudRenderer} implementation
	 */
	static void registerCloudRenderer(RegistryKey<World> key, CloudRenderer renderer) {
		DimensionRenderingRegistryImpl.registerCloudRenderer(key, renderer);
	}

	/**
	 * Gets the custom sky renderer for the given {@link World}.
	 *
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @return Null if no custom sky renderer is registered for the dimension.
	 */
	@Nullable
	static SkyRenderer getSkyRenderer(RegistryKey<World> key) {
		return DimensionRenderingRegistryImpl.getSkyRenderer(key);
	}

	/**
	 * Gets the custom cloud renderer for the given {@link World}.
	 *
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @return Null if no custom cloud renderer is registered for the dimension.
	 */
	@Nullable
	static CloudRenderer getCloudRenderer(RegistryKey<World> key) {
		return DimensionRenderingRegistryImpl.getCloudRenderer(key);
	}

	/**
	 * Gets the custom weather effect renderer for the given {@link World}.
	 *
	 * @return Null if no custom weather effect renderer is registered for the dimension.
	 */
	@Nullable
	static WeatherRenderer getWeatherRenderer(RegistryKey<World> key) {
		return DimensionRenderingRegistryImpl.getWeatherRenderer(key);
	}

	/**
	 * Gets the sky property registered for an id. Returns the overworld sky property if
	 * no custom sky properties for the id are present.
	 */
	@Nullable
	static SkyProperties getSkyProperties(Identifier key) {
		return DimensionRenderingRegistryImpl.getSkyProperties(key);
	}

	@FunctionalInterface
	interface SkyRenderer {
		void render(WorldRenderContext context);
	}

	@FunctionalInterface
	interface WeatherRenderer {
		void render(WorldRenderContext context);
	}

	@FunctionalInterface
	interface CloudRenderer {
		void render(WorldRenderContext context);
	}
}
