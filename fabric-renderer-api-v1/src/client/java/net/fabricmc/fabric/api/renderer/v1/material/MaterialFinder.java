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

import net.minecraft.client.render.RenderLayer;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

/**
 * Finds standard {@link RenderMaterial} instances used to communicate
 * quad rendering characteristics to a {@link RenderContext}.
 *
 * <p>Must be obtained via {@link Renderer#materialFinder()}.
 */
public interface MaterialFinder {
	/**
	 * Defines how sprite pixels will be blended with the scene.
	 *
	 * <p>See {@link BlendMode} for more information.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MaterialFinder blendMode(BlendMode blendMode) {
		return blendMode(0, blendMode);
	}

	/**
	 * Vertex color(s) will be modified for quad color index unless disabled.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MaterialFinder disableColorIndex(boolean disable) {
		return disableColorIndex(0, disable);
	}

	/**
	 * When true, sprite texture and color will be rendered at full brightness.
	 * Lightmap values provided via {@link QuadEmitter#lightmap(int)} will be ignored.
	 * False by default
	 *
	 * <p>This is the preferred method for emissive lighting effects.  Some renderers
	 * with advanced lighting models may not use block lightmaps and this method will
	 * allow per-sprite emissive lighting in future extensions that support overlay sprites.
	 *
	 * <p>Note that color will still be modified by diffuse shading and ambient occlusion,
	 * unless disabled via {@link #disableAo(boolean)} and {@link #disableDiffuse(boolean)}.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MaterialFinder emissive(boolean isEmissive) {
		return emissive(0, isEmissive);
	}

	/**
	 * Vertex color(s) will be modified for diffuse shading unless disabled.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MaterialFinder disableDiffuse(boolean disable) {
		return disableDiffuse(0, disable);
	}

	/**
	 * Vertex color(s) will be modified for ambient occlusion unless disabled.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MaterialFinder disableAo(boolean disable) {
		return disableAo(0, disable);
	}

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
	 * @deprecated Use {@link #disableAo(boolean)} instead.
	 */
	@Deprecated
	default MaterialFinder disableAo(int spriteIndex, boolean disable) {
		return disableAo(disable);
	}

	/**
	 * Do not use. Does nothing.
	 */
	@Deprecated
	default MaterialFinder spriteDepth(int depth) {
		return this;
	}
}
