package net.fabricmc.fabric.impl.client.rendering;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.mixin.client.rendering.SkyPropertiesAccessor;

public final class DimensionRenderingRegistryImpl implements DimensionRenderingRegistry {
	public static final DimensionRenderingRegistryImpl INSTANCE = new DimensionRenderingRegistryImpl();
	private final Map<RegistryKey<World>, SkyRenderer> SKY_RENDERERS = new IdentityHashMap<>();
	private final Map<RegistryKey<World>, CloudRenderer> CLOUD_RENDERERS = new HashMap<>();
	private final Map<RegistryKey<World>, WeatherRenderer> WEATHER_RENDERERS = new HashMap<>();

	public void setSkyRenderer(RegistryKey<World> key, SkyRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		if (!override && SKY_RENDERERS.containsKey(key)) {
			throw new IllegalStateException("This world already has a registered SkyRenderer.");
		} else {
			SKY_RENDERERS.put(key, renderer);
		}
	}

	public void setSkyRenderer(RegistryKey<World> key, SkyRenderer renderer) {
		setSkyRenderer(key, renderer, false);
	}

	public void setWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		if (!override && WEATHER_RENDERERS.containsKey(key)) {
			throw new IllegalStateException("This world already has a registered WeatherRenderer.");
		} else {
			WEATHER_RENDERERS.putIfAbsent(key, renderer);
		}
	}

	public void setWeatherRenderer(RegistryKey<World> key, WeatherRenderer renderer) {
		setWeatherRenderer(key, renderer, false);
	}

	public void registerSkyProperty(RegistryKey<DimensionType> key, SkyProperties properties, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(properties);

		if (!override && ((SkyPropertiesAccessor) properties).getIdentifierMap().containsKey(key.getValue())) {
			throw new IllegalStateException("This world already has a registered sky properties.");
		} else {
			((SkyPropertiesAccessor) properties).getIdentifierMap().put(key.getValue(), properties);
		}
	}

	public void registerSkyProperty(RegistryKey<DimensionType> key, SkyProperties properties) {
		registerSkyProperty(key, properties, false);
	}

	public void setCloudRenderer(RegistryKey<World> key, CloudRenderer renderer, boolean override) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(renderer);

		if (!override && CLOUD_RENDERERS.containsKey(key)) {
			throw new IllegalStateException("This world already has a registered CloudRenderer.");
		} else {
			CLOUD_RENDERERS.putIfAbsent(key, renderer);
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
}
