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

import java.util.Objects;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Builder used to create {@link net.minecraft.client.render.SkyProperties} instances which
 * govern various things about how a dimension renders including where thick fog should be used, what type of should be rendered, et ect.
 */
@Environment(EnvType.CLIENT)
public final class FabricSkyPropertyBuilder {
	private FogColorOverride fogColorOverride = (color, skyAngle, tickDelta) -> {
		float g = MathHelper.cos(skyAngle * 6.2831855F) - 0.0F;

		if (g >= -0.4F && g <= 0.4F) {
			float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
			float j = 1.0F - (1.0F - MathHelper.sin(i * 3.1415927F)) * 0.99F;
			j *= j;
			color[0] = i * 0.3F + 0.7F;
			color[1] = i * i * 0.7F + 0.2F;
			color[2] = i * i * 0.0F + 0.2F;
			color[3] = j;
			return true;
		} else {
			return false;
		}
	};

	private float cloudsHeight = 128.0F;
	private boolean alternateSkyColor = true;

	private AdjustFogColor adjustFogColor = (color, sunHeight) -> color.multiply((sunHeight * 0.94F + 0.06F), sunHeight * 0.94F + 0.06F, (sunHeight * 0.91F + 0.09F));
	private UseThickFog useThickFog = (camX, camY) -> false;

	private SkyProperties.SkyType skyType = SkyProperties.SkyType.NORMAL;

	private boolean brightenLighting = false;
	private boolean darkened = false;

	/**
	 * Create a new Sky Property Builder.
	 *
	 * @return a FabricSkyPropertyBuilder
	 */
	public static FabricSkyPropertyBuilder create() {
		return new FabricSkyPropertyBuilder();
	}

	/**
	 * This is used say where clouds will be rendered.
	 *
	 * @param cloudsHeight the height clouds will render at
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder cloudsHeight(float cloudsHeight) {
		this.cloudsHeight = cloudsHeight;
		return this;
	}

	/**
	 * Should the sky be slightly brighter.
	 *
	 * @param alternateSkyColor should sky be brighter
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder alternateSkyColor(boolean alternateSkyColor) {
		this.alternateSkyColor = alternateSkyColor;
		return this;
	}

	/**
	 * Transforms the given fog color based on the current height of the sun. This is used in vanilla to darken fog during night.
	 *
	 * @param adjustFogColor Spits out an adjusted RBGA value based on previous color and the sun height.
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder adjustFogColor(AdjustFogColor adjustFogColor) {
		this.adjustFogColor = adjustFogColor;
		return this;
	}

	/**
	 * Tells the client if thick fog should be render at xy coordinates.
	 *
	 * @param useThickFog Used to etermines if fog should be thick at xy camera coordinate
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder useThickFog(UseThickFog useThickFog) {
		Objects.requireNonNull(useThickFog);
		this.useThickFog = useThickFog;
		return this;
	}

	/**
	 * This is used to tell Vanilla what kind of sky to render.
	 *
	 * @param skyType Type of vanilla sky to render
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder skyType(SkyProperties.SkyType skyType) {
		Objects.requireNonNull(skyType);
		this.skyType = skyType;
		return this;
	}

	/**
	 * Will cause lighting to be higher.
	 *
	 * @param brightenLighting should lighting be higher.
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder brightenLighting(boolean brightenLighting) {
		this.brightenLighting = brightenLighting;
		return this;
	}

	/**
	 * This is used in vanilla to render sunset and sunrise fog.
	 * Function input is an array for storing color, current sky angle, and tick delta
	 * @param fogColorOverride function used to calculate fog color
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder fogColorOverride(FogColorOverride fogColorOverride) {
		Objects.requireNonNull(fogColorOverride);
		this.fogColorOverride = fogColorOverride;
		return this;
	}

	/**
	 * Says if lighting should be darker.
	 *
	 * @param darkened should lighting be darker
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder darkened(boolean darkened) {
		this.darkened = darkened;
		return this;
	}

	/**
	 * Create an instance of the SkyProperties.
	 *
	 * @return An instance of the built SkyProperties
	 */
	public SkyProperties build() {
		return new FabricSkyproperties(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened, fogColorOverride, adjustFogColor, useThickFog);
	}

	public static class FabricSkyproperties extends SkyProperties {
		private final float[] color = new float[4];
		private final FogColorOverride fogColorOverride;
		private final AdjustFogColor adjustFogColor;
		private final UseThickFog useThickFog;

		public FabricSkyproperties(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened, FogColorOverride fogColorOverride, AdjustFogColor adjustFogColor, UseThickFog useThickFog) {
			super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened);
			this.fogColorOverride = fogColorOverride;
			this.adjustFogColor = adjustFogColor;
			this.useThickFog = useThickFog;
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return adjustFogColor.adjust(color, sunHeight);
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return useThickFog.use(camX, camY);
		}

		@Override
		public float[] getFogColorOverride(float skyAngle, float tickDelta) {
			return fogColorOverride.override(color, skyAngle, tickDelta) ? color : null;
		}
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface FogColorOverride {
		/**
		 * Used to determine if the fog color should be overriden based on the current sky angle and tick delta.
		 * The array storing the color value used in the override is provided.
		 *
		 * @param color provided array containing overriding color.
		 * @param skyAngle current angle of the sky.
		 * @param tickDelta current tick delta.
		 * @return should fog.
		 */
		boolean override(float[] color, float skyAngle, float tickDelta);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface UseThickFog {
		/**
		 * Used to determine if thick fog should be used current xy coordinates of teh camera.
		 *
		 * @param camX current camera x coordinate.
		 * @param camY current camera y coordinate.
		 * @return should thick fog be used.
		 */
		boolean use(int camX, int camY);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AdjustFogColor {
		/**
		 * Used to adjust fog color based on height of the sun.
		 *
		 * @param color current fog color.
		 * @param sunHeight current height of the sun.
		 * @return adjusted fog color.
		 */
		Vec3d adjust(Vec3d color, float sunHeight);
	}
}
