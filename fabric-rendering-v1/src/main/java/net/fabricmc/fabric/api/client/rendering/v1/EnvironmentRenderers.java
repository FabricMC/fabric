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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.rendering.SkyPropertiesAccessor;

/**
 * Environmental renderers render world specific visuals of a world.
 * They may be used to render the sky, weather, or clouds.
 * The {@link SkyProperties} is the vanilla environmental renderer.
 */
@Environment(EnvType.CLIENT)
public final class EnvironmentRenderers {
	private static final Map<RegistryKey<World>, SkyRenderer> SKY_RENDERERS = new IdentityHashMap<>();
	private static final Map<RegistryKey<World>, CloudRenderer> CLOUD_RENDERERS = new HashMap<>();
	private static final Map<RegistryKey<World>, WeatherRenderer> WEATHER_RENDERERS = new HashMap<>();

	/**
	 * Registers a custom sky renderer for a DimensionType.
	 *
	 * <p>This overrides Vanilla's sky rendering.
	 * @param key A RegistryKey for your DimensionType
	 * @param renderer A {@link SkyRenderer} implementation
	 * @param override Should try to override current SkyRenderer if it exists
	 * @return if the new renderer was accepted
	 */
	public static boolean registerSkyRenderer(RegistryKey<World> key, SkyRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		if (!override && SKY_RENDERERS.containsKey(key)) {
			return false;
		} else {
			SKY_RENDERERS.put(key, renderer);
			return true;
		}
	}

	/**
	 * Registers a custom weather renderer for a DimensionType.
	 *
	 * <p>This overrides Vanilla's weather rendering.
	 * @param key A RegistryKey for your Dimension Type
	 * @param renderer A {@link WeatherRenderer} implementation
	 * @param override Should try to override current SkyRenderer if it exists
	 * @return if the new renderer was accepted
	 */
	public static boolean registerWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		if (!override && WEATHER_RENDERERS.containsKey(key)) {
			return false;
		} else {
			WEATHER_RENDERERS.putIfAbsent(key, renderer);
			return true;
		}
	}

	/**
	 * Registers a custom sky property for a DimensionType.
	 *
	 *  <p>This overrides Vanilla's default sky properties.
	 * @param key A RegistryKey for your Dimension Type
	 * @param properties The Dimension Type's sky properties
	 * @param override Should try to override current SkyRenderer if it exists
	 * @return if the new properties was accepted
	 */
	public static boolean registerSkyProperty(RegistryKey<DimensionType> key, SkyProperties properties, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(properties);

		if (!override && ((SkyPropertiesAccessor) properties).getIdentifierMap().containsKey(key.getValue())) {
			return false;
		} else {
			((SkyPropertiesAccessor) properties).getIdentifierMap().put(key.getValue(), properties);
			return true;
		}
	}

	/**
	 * Registers a custom cloud renderer for a Dimension Type.
	 *
	 * <p>This overrides Vanilla's cloud rendering.
	 *  @param key A RegistryKey for your Dimension Type
	 * @param renderer A {@link CloudRenderer} implementation
	 * @param override Should try to override current SkyRenderer if it exists
	 * @return if the new renderer was accepted
	 */
	public static boolean registerCloudRenderer(RegistryKey<World> key, CloudRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		if (!override && CLOUD_RENDERERS.containsKey(key)) {
			return false;
		} else {
			CLOUD_RENDERERS.putIfAbsent(key, renderer);
			return true;
		}
	}

	@Nullable
	public static SkyRenderer getSkyRenderer(RegistryKey<World> key) {
		return SKY_RENDERERS.get(key);
	}

	@Nullable
	public static CloudRenderer getCloudRenderer(RegistryKey<World> key) {
		return CLOUD_RENDERERS.get(key);
	}

	@Nullable
	public static WeatherRenderer getWeatherRenderer(RegistryKey<World> key) {
		return WEATHER_RENDERERS.get(key);
	}

	@FunctionalInterface
	public interface SkyRenderer {
		void render(MinecraftClient client, MatrixStack matrices, float tickDelta);
	}

	@FunctionalInterface
	public interface WeatherRenderer {
		void render(MinecraftClient client, LightmapTextureManager manager, float tickDelta, double x, double y, double z);
	}

	@FunctionalInterface
	public interface CloudRenderer {
		void render(MinecraftClient client, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, double cameraX, double cameraY, double cameraZ);
	}
}
