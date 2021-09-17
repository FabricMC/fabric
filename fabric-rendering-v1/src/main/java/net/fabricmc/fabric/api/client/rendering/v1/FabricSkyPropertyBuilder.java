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
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import com.mojang.datafixers.util.Function3;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
	 * @param adjustFogColor Function the spits out an RBGA value based on previous color and the sun height.
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder adjustFogColor(BiFunction<Vec3d, Float, Vec3d> adjustFogColor) {
		this.adjustFogColor = adjustFogColor;
		return this;
	}

	/**
	 * Tells the client if thick fog should be render at xy coordinates.
	 *
	 * @param useThickFog Predicate that deterines if fog should be thick at xy coordinate
	 * @return a reference to the FabricItemGroupBuilder
	 */
	public FabricSkyPropertyBuilder useThickFog(BiPredicate<Integer, Integer> useThickFog) {
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
	public FabricSkyPropertyBuilder fogColorOverride(Function3<float[], Float, Float, float[]> fogColorOverride) {
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
		private final Function3<float[], Float, Float, float[]> fogColorOverride;
		private final BiFunction<Vec3d, Float, Vec3d> adjustFogColor;
		private final BiPredicate<Integer, Integer> useThickFog;

		public FabricSkyproperties(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened, Function3<float[], Float, Float, float[]> fogColorOverride, BiFunction<Vec3d, Float, Vec3d> adjustFogColor, BiPredicate<Integer, Integer> useThickFog) {
			super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened);
			this.fogColorOverride = fogColorOverride;
			this.adjustFogColor = adjustFogColor;
			this.useThickFog = useThickFog;
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
	}
}
