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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

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
	 * The singleton instance of the renderer registry.
	 * Use this instance to call the methods in this interface.
	 */
	DimensionRenderingRegistry INSTANCE = DimensionRenderingRegistryImpl.INSTANCE;

	/**
	 * sets the custom sky renderer for a {@link World}.
	 *
	 * <p>This overrides Vanilla's sky rendering.
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @param renderer A {@link SkyRenderer} implementation
	 */
	void setSkyRenderer(RegistryKey<World> key, SkyRenderer renderer);

	/**
	 * Registers a custom weather renderer for a {@link World}.
	 *
	 * <p>This overrides Vanilla's weather rendering.
	 * @param key A RegistryKey for your {@link World}
	 * @param renderer A {@link WeatherRenderer} implementation
	 */
	void setWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer);

	/**
	 * Registers a custom sky property for a {@link DimensionType}.
	 *
	 *  <p>This overrides Vanilla's default {@link SkyProperties}.
	 * @param key A {@link RegistryKey} for your {@link DimensionType}
	 * @param properties The {@link DimensionType}'s {@link SkyProperties}
	 */
	void setSkyProperties(RegistryKey<DimensionType> key, SkyProperties properties);

	/**
	 * Registers a custom cloud renderer for a {@link World}.
	 *
	 * <p>This overrides Vanilla's cloud rendering.
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @param renderer A {@link CloudRenderer} implementation
	 */
	void setCloudRenderer(RegistryKey<World> key, CloudRenderer renderer);

	/**
	 * Gets the custom sky renderer for the given {@link World}.
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @return Null if no custom sky renderer is registered for the dimension.
	 */
	@Nullable
	SkyRenderer getSkyRenderer(RegistryKey<World> key);

	/**
	 * Gets the custom cloud renderer for the given {@link World}.
	 * @param key A {@link RegistryKey} for your {@link World}
	 * @return Null if no custom cloud renderer is registered for the dimension.
	 */
	@Nullable
	CloudRenderer getCloudRenderer(RegistryKey<World> key);

	/**
	 * Gets the custom weather effect renderer for the given {@link World}.
	 * @return Null if no custom weather effect renderer is registered for the dimension.
	 */
	@Nullable
	WeatherRenderer getWeatherRenderer(RegistryKey<World> key);

	/**
	 * Gets the custom sky properties for a dimension type. Returns null if
	 * no custom sky properties are set.
	 */
	@Nullable
	SkyProperties getSkyProperties(RegistryKey<DimensionType> key);

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
