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

public final class EnvironmentRenderers {
	private EnvironmentRenderers() { }

	@Environment(EnvType.CLIENT)
	private static final Map<RegistryKey<World>, SkyRenderer> SKY_RENDERERS = new IdentityHashMap<>();
	@Environment(EnvType.CLIENT)
	private static final Map<RegistryKey<World>, CloudRenderer> CLOUD_RENDERERS = new HashMap<>();
	@Environment(EnvType.CLIENT)
	private static final Map<RegistryKey<World>, WeatherRenderer> WEATHER_RENDERERS = new HashMap<>();

	/**
	 * Registers a custom sky renderer for a DimensionType.
	 *
	 * @param key A RegistryKey for your Dimension Type
	 * @param renderer A {@link SkyRenderer} implementation
	 */
	@Environment(EnvType.CLIENT)
	public static void registerSkyRenderer(RegistryKey<World> key, SkyRenderer renderer) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);
		SKY_RENDERERS.putIfAbsent(key, renderer);
	}

	/**
	 * Registers a custom rain and snow renderer for a DimensionType.
	 *
	 * @param key A RegistryKey for your Dimension Type
	 * @param renderer A {@link WeatherRenderer} implementation
	 */
	@Environment(EnvType.CLIENT)
	public static void registerWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);
		WEATHER_RENDERERS.putIfAbsent(key, renderer);
	}

	/**
	 * Registers a custom sky property for a DimensionType.
	 *
	 * @param key A RegistryKey for your Dimension Type
	 * @param properties The Dimension Type's sky properties
	 */
	@Environment(EnvType.CLIENT)
	public static void registerSkyProperty(RegistryKey<DimensionType> key, SkyProperties properties) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(properties);
		((SkyPropertiesAccessor) properties).getIdentifierMap().put(key.getValue(), properties);
	}

	/**
	 * Registers a custom cloud renderer for a Dimension Type.
	 *
	 * @param key A RegistryKey for your Dimension Type
	 * @param renderer A {@link CloudRenderer} implementation
	 */
	@Environment(EnvType.CLIENT)
	public static void registerCloudRenderer(RegistryKey<World> key, CloudRenderer renderer) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);
		CLOUD_RENDERERS.putIfAbsent(key, renderer);
	}

	@Environment(EnvType.CLIENT)
	public static SkyRenderer getSkyRenderer(RegistryKey<World> key) {
		return SKY_RENDERERS.get(key);
	}

	@Environment(EnvType.CLIENT)
	public static CloudRenderer getCloudRenderer(RegistryKey<World> key) {
		return CLOUD_RENDERERS.get(key);
	}

	@Environment(EnvType.CLIENT)
	public static WeatherRenderer getWeatherRenderer(RegistryKey<World> key) {
		return WEATHER_RENDERERS.get(key);
	}

	@FunctionalInterface
	public interface SkyRenderer {
		void render(MinecraftClient world, MatrixStack matrices, float tickDelta);
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
