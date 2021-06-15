package net.fabricmc.fabric.api.client.rendering.v1;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.mojang.datafixers.util.Function3;

public final class FabricSkyPropertyBuilder {
	private Function3<float[], Float, Float, float[]> fogColorOverride = (color, skyAngle, tickDelta) -> {
		float g = MathHelper.cos(skyAngle * 6.2831855F) - 0.0F;
		if (g >= -0.4F && g <= 0.4F) {
			float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
			float j = 1.0F - (1.0F - MathHelper.sin(i * 3.1415927F)) * 0.99F;
			j *= j;
			color[0] = i * 0.3F + 0.7F;
			color[1] = i * i * 0.7F + 0.2F;
			color[2] = i * i * 0.0F + 0.2F;
			color[3] = j;
			return color;
		} else {
			return null;
		}
	};

	private float cloudsHeight = 128.0F;
	private boolean alternateSkyColor = true;

	private BiFunction<Vec3d, Float, Vec3d> adjustFogColor = (color, sunHeight) -> color.multiply((sunHeight * 0.94F + 0.06F), sunHeight * 0.94F + 0.06F, (sunHeight * 0.91F + 0.09F));
	private BiPredicate<Integer, Integer> useThickFog = (camX, camY) -> false;

	private SkyProperties.SkyType skyType = SkyProperties.SkyType.NORMAL;

	private boolean brightenLighting = false;
	private boolean hideClouds = false;
	private boolean hideWeather = false;
	private boolean darkened = false;

	public static FabricSkyPropertyBuilder create() {
		return new FabricSkyPropertyBuilder();
	}

	public FabricSkyPropertyBuilder cloudsHeight(float cloudsHeight) {
		this.cloudsHeight = cloudsHeight;
		return this;
	}

	public FabricSkyPropertyBuilder alternateSkyColor(boolean alternateSkyColor) {
		this.alternateSkyColor = alternateSkyColor;
		return this;
	}

	public FabricSkyPropertyBuilder adjustFogColor(BiFunction<Vec3d, Float, Vec3d> adjustFogColor) {
		this.adjustFogColor = adjustFogColor;
		return this;
	}

	public FabricSkyPropertyBuilder useThickFog(BiPredicate<Integer, Integer> useThickFog) {
		Objects.requireNonNull(useThickFog);
		this.useThickFog = useThickFog;
		return this;
	}

	public FabricSkyPropertyBuilder skyType(SkyProperties.SkyType skyType) {
		Objects.requireNonNull(skyType);
		this.skyType = skyType;
		return this;
	}

	public FabricSkyPropertyBuilder brightenLighting(boolean brightenLighting) {
		this.brightenLighting = brightenLighting;
		return this;
	}

	public FabricSkyPropertyBuilder fogColorOverride(Function3<float[], Float, Float, float[]> fogColorOverride) {
		Objects.requireNonNull(fogColorOverride);
		this.fogColorOverride = fogColorOverride;
		return this;
	}

	public FabricSkyPropertyBuilder darkened(boolean darkened) {
		this.darkened = darkened;
		return this;
	}

	public FabricSkyPropertyBuilder hideWeather() {
		this.hideWeather = true;
		return this;
	}


	public FabricSkyPropertyBuilder hideClouds() {
		this.hideClouds = true;
		return this;
	}

	public SkyProperties build() {
		return new FabricSkyproperties(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened, fogColorOverride, adjustFogColor, useThickFog, hideClouds, hideWeather);
	}

	public static class FabricSkyproperties extends SkyProperties {
		private final float[] color = new float[4];
		private final Function3<float[], Float, Float, float[]> fogColorOverride;
		private final BiFunction<Vec3d, Float, Vec3d> adjustFogColor;
		private final BiPredicate<Integer, Integer> useThickFog;
		private final boolean hideClouds;
		private final boolean hideWeather;

		public FabricSkyproperties(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened, Function3<float[], Float, Float, float[]> fogColorOverride, BiFunction<Vec3d, Float, Vec3d> adjustFogColor, BiPredicate<Integer, Integer> useThickFog, boolean hideClouds, boolean hideWeather) {
			super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened);
			this.fogColorOverride = fogColorOverride;
			this.adjustFogColor = adjustFogColor;
			this.useThickFog = useThickFog;
			this.hideClouds = hideClouds;
			this.hideWeather = hideWeather;
		}
		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return adjustFogColor.apply(color, sunHeight);
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return useThickFog.test(camX, camY);
		}

		@Override
		public float[] getFogColorOverride(float skyAngle, float tickDelta) {
			return fogColorOverride.apply(color, skyAngle, tickDelta);
		}

		public boolean canHideClouds() {
			return hideClouds;
		}

		public boolean canHideWeather() {
			return hideWeather;
		}
	}
}