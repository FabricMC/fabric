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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.mixin.client.rendering.SkyPropertiesAccessor;

public final class DimensionRenderingRegistryImpl implements DimensionRenderingRegistry {
	Logger logger = LogManager.getLogger("FabricDimensionRenderingRegistry");
	public static final DimensionRenderingRegistryImpl INSTANCE = new DimensionRenderingRegistryImpl();
	private final Map<RegistryKey<World>, SkyRenderer> SKY_RENDERERS = new IdentityHashMap<>();
	private final Map<RegistryKey<World>, CloudRenderer> CLOUD_RENDERERS = new HashMap<>();
	private final Map<RegistryKey<World>, WeatherRenderer> WEATHER_RENDERERS = new HashMap<>();

	public void setSkyRenderer(RegistryKey<World> key, SkyRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		SkyRenderer prior = SKY_RENDERERS.get(key);

		if (!override && prior != null) {
			throw new IllegalStateException("This world already has a registered SkyRenderer.");
		} else {
			if (prior != null) logger.info("sky renderer {} replaced by {}", prior, renderer);

			SKY_RENDERERS.put(key, renderer);
		}
	}

	public void setSkyRenderer(RegistryKey<World> key, SkyRenderer renderer) {
		setSkyRenderer(key, renderer, false);
	}

	public void setWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		WeatherRenderer prior = WEATHER_RENDERERS.get(key);

		if (!override && prior != null) {
			throw new IllegalStateException("This world already has a registered WeatherRenderer.");
		} else {
			if (prior != null) logger.info("weather renderer {} replaced by {}", prior, renderer);

			WEATHER_RENDERERS.put(key, renderer);
		}
	}

	public void setWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer) {
		setWeatherRenderer(key, renderer, false);
	}

	public void setSkyProperty(RegistryKey<DimensionType> key, SkyProperties properties, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(properties);

		SkyProperties prior = SkyPropertiesAccessor.getIdentifierMap().get(key.getValue());

		if (!override && prior != null) {
			throw new IllegalStateException("This world already has a registered SkyProperties.");
		} else {
			if (prior != null) logger.info("sky property {} replaced by {}", prior, properties);

			SkyPropertiesAccessor.getIdentifierMap().put(key.getValue(), properties);
		}
	}

	public void setSkyProperty(RegistryKey<DimensionType> key, SkyProperties properties) {
		setSkyProperty(key, properties, false);
	}

	public void setCloudRenderer(RegistryKey<World> key, CloudRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		CloudRenderer prior = CLOUD_RENDERERS.get(key);

		if (!override && prior != null) {
			throw new IllegalStateException("This world already has a registered CloudRenderer.");
		} else {
			if (prior != null) logger.info("cloud renderer {} replaced by {}", prior, renderer);

			CLOUD_RENDERERS.put(key, renderer);
		}
	}

	public void setCloudRenderer(RegistryKey<World> key, CloudRenderer renderer) {
		setCloudRenderer(key, renderer, false);
	}

	@Nullable
	public SkyRenderer getSkyRenderer(RegistryKey<World> key) {
		return SKY_RENDERERS.get(key);
	}

	@Nullable
	public CloudRenderer getCloudRenderer(RegistryKey<World> key) {
		return CLOUD_RENDERERS.get(key);
	}

	@Nullable
	public WeatherRenderer getWeatherRenderer(RegistryKey<World> key) {
		return WEATHER_RENDERERS.get(key);
	}

	@Override
	public @Nullable SkyProperties getSkyProperty(RegistryKey<DimensionType> key) {
		return SkyPropertiesAccessor.getIdentifierMap().get(key);
	}
}
