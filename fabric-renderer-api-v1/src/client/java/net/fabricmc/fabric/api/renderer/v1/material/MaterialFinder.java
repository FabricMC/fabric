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

package net.fabricmc.fabric.api.renderer.v1.material;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;

/**
 * Finds standard {@link RenderMaterial} instances used to communicate
 * quad rendering characteristics to a {@link RenderContext}.
 *
 * <p>Must be obtained via {@link Renderer#materialFinder()}.
 */
public interface MaterialFinder extends MaterialView {
	/**
	 * Controls how sprite pixels should be blended with the scene.
	 *
	 * <p>The default value is {@link BlendMode#DEFAULT}.
	 *
	 * @see BlendMode
	 */
	MaterialFinder blendMode(BlendMode blendMode);

	/**
	 * Controls whether vertex colors should be modified for quad coloring. This property
	 * is inverted, so a value of {@code false} means that quad coloring will be applied.
	 *
	 * <p>The default value is {@code false}.
	 */
	MaterialFinder disableColorIndex(boolean disable);

	/**
	 * When true, sprite texture and color will be rendered at full brightness.
	 * Lightmap values provided via {@link QuadEmitter#lightmap(int)} will be ignored.
	 *
	 * <p>This is the preferred method for emissive lighting effects. Some renderers
	 * with advanced lighting pipelines may not use block lightmaps and this method will
	 * allow per-sprite emissive lighting in future extensions that support overlay sprites.
	 *
	 * <p>Note that color will still be modified by diffuse shading and ambient occlusion,
	 * unless disabled via {@link #disableDiffuse(boolean)} and {@link #ambientOcclusion(TriState)}.
	 *
	 * <p>The default value is {@code false}.
	 */
	MaterialFinder emissive(boolean isEmissive);

	/**
	 * Controls whether vertex colors should be modified for diffuse shading. This property
	 * is inverted, so a value of {@code false} means that diffuse shading will be applied.
	 *
	 * <p>The default value is {@code false}.
	 *
	 * <p>This property is guaranteed to be respected in block contexts. Some renderers may also respect it in item
	 * contexts, but this is not guaranteed.
	 */
	MaterialFinder disableDiffuse(boolean disable);

	/**
	 * Controls whether vertex colors should be modified for ambient occlusion.
	 *
	 * <p>If set to {@link TriState#DEFAULT}, ambient occlusion will be used if
	 * {@linkplain BakedModel#useAmbientOcclusion() the model uses ambient occlusion} and the block state has
	 * {@linkplain BlockState#getLuminance() a luminance} of 0. Set to {@link TriState#TRUE} or {@link TriState#FALSE}
	 * to override this behavior. {@link TriState#TRUE} will not have an effect if
	 * {@linkplain MinecraftClient#isAmbientOcclusionEnabled() ambient occlusion is disabled globally}.
	 *
	 * <p>The default value is {@link TriState#DEFAULT}.
	 *
	 * <p>This property is respected only in block contexts. It will not have an effect in other contexts.
	 */
	MaterialFinder ambientOcclusion(TriState mode);

	/**
	 * Controls whether glint should be applied.
	 *
	 * <p>If set to {@link TriState#DEFAULT}, glint will be applied in item contexts if
	 * {@linkplain ItemStack#hasGlint() the item stack has glint}. Set to {@link TriState#TRUE} or
	 * {@link TriState#FALSE} to override this behavior.
	 *
	 * <p>The default value is {@link TriState#DEFAULT}.
	 *
	 * <p>This property is guaranteed to be respected in item contexts. Some renderers may also respect it in block
	 * contexts, but this is not guaranteed.
	 */
	MaterialFinder glint(TriState mode);

	/**
	 * A hint to the renderer about how the quad is intended to be shaded, for example through ambient occlusion and
	 * diffuse shading. The renderer is free to ignore this hint.
	 *
	 * <p>The default value is {@link ShadeMode#ENHANCED}.
	 *
	 * <p>This property is respected only in block contexts. It will not have an effect in other contexts.
	 *
	 * @see ShadeMode
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MaterialFinder shadeMode(ShadeMode mode) {
		return this;
	}

	/**
	 * Copies all properties from the given {@link MaterialView} to this material finder.
	 */
	MaterialFinder copyFrom(MaterialView material);

	/**
	 * Resets this instance to default values. Values will match those
	 * in effect when an instance is newly obtained via {@link Renderer#materialFinder()}.
	 */
	MaterialFinder clear();

	/**
	 * Returns the standard material encoding all
	 * of the current settings in this finder. The settings in
	 * this finder are not changed.
	 *
	 * <p>Resulting instances can and should be re-used to prevent
	 * needless memory allocation. {@link Renderer} implementations
	 * may or may not cache standard material instances.
	 */
	RenderMaterial find();

	/**
	 * @deprecated Use {@link #blendMode(BlendMode)} instead.
	 */
	@Deprecated
	default MaterialFinder blendMode(int spriteIndex, RenderLayer renderLayer) {
		return blendMode(BlendMode.fromRenderLayer(renderLayer));
	}

	/**
	 * @deprecated Use {@link #blendMode(BlendMode)} instead.
	 */
	@Deprecated
	default MaterialFinder blendMode(int spriteIndex, BlendMode blendMode) {
		// Null check is kept for legacy reasons, but the new blendMode method will NPE if passed null!
		if (blendMode == null) {
			blendMode = BlendMode.DEFAULT;
		}

		return blendMode(blendMode);
	}

	/**
	 * @deprecated Use {@link #disableColorIndex(boolean)} instead.
	 */
	@Deprecated
	default MaterialFinder disableColorIndex(int spriteIndex, boolean disable) {
		return disableColorIndex(disable);
	}

	/**
	 * @deprecated Use {@link #emissive(boolean)} instead.
	 */
	@Deprecated
	default MaterialFinder emissive(int spriteIndex, boolean isEmissive) {
		return emissive(isEmissive);
	}

	/**
	 * @deprecated Use {@link #disableDiffuse(boolean)} instead.
	 */
	@Deprecated
	default MaterialFinder disableDiffuse(int spriteIndex, boolean disable) {
		return disableDiffuse(disable);
	}

	/**
	 * @deprecated Use {@link #ambientOcclusion(TriState)} instead.
	 */
	@Deprecated
	default MaterialFinder disableAo(int spriteIndex, boolean disable) {
		return ambientOcclusion(disable ? TriState.FALSE : TriState.DEFAULT);
	}

	/**
	 * Do not use. Does nothing.
	 */
	@Deprecated
	default MaterialFinder spriteDepth(int depth) {
		return this;
	}
}
