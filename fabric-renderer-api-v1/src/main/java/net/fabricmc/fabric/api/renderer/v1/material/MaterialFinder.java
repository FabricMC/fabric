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
	 * Resets this instance to default values. Values will match those
	 * in effect when an instance is newly obtained via {@link Renderer#materialFinder()}.
	 */
	MaterialFinder clear();

	/**
	 * Reserved for future use.  Behavior for values &gt; 1 is currently undefined.
	 */
	MaterialFinder spriteDepth(int depth);

	/**
	 * Defines how sprite pixels will be blended with the scene.
	 * Accepts {link @BlockRenderLayer} values and blending behavior
	 * will emulate the way that Minecraft renders those instances. This does
	 * NOT mean the sprite will be rendered in a specific render pass - some
	 * implementations may not use the standard vanilla render passes.
	 *
	 * <p>CAN be null and is null by default. A null value means the renderer
	 * will use the value normally associated with the block being rendered, or
	 * {@code TRANSLUCENT} for item renders. (Normal Minecraft rendering)
	 *
	 * @deprecated Use {@code BlendMode} version instead.
	 */
	@Deprecated
	default MaterialFinder blendMode(int spriteIndex, RenderLayer renderLayer) {
		return blendMode(spriteIndex, BlendMode.fromRenderLayer(renderLayer));
	}

	/**
	 * Defines how sprite pixels will be blended with the scene.
	 *
	 * <p>See {@link BlendMode} for more information.
	 */
	MaterialFinder blendMode(int spriteIndex, BlendMode blendMode);

	/**
	 * Vertex color(s) will be modified for quad color index unless disabled.
	 */
	MaterialFinder disableColorIndex(int spriteIndex, boolean disable);

	/**
	 * Vertex color(s) will be modified for diffuse shading unless disabled.
	 */
	MaterialFinder disableDiffuse(int spriteIndex, boolean disable);

	/**
	 * Vertex color(s) will be modified for ambient occlusion unless disabled.
	 */
	MaterialFinder disableAo(int spriteIndex, boolean disable);

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
	 * unless disabled via {@link #disableAo(int, boolean)} and {@link #disableDiffuse(int, boolean)}.
	 */
	MaterialFinder emissive(int spriteIndex, boolean isEmissive);
}
