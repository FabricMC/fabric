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

package net.fabricmc.fabric.api.renderer.v1.render;

import java.util.function.Consumer;

import net.minecraft.client.render.model.BakedModel;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;

/**
 * This defines the instance made available to models for buffering vertex data at render time.
 */
public interface RenderContext {
	/**
	 * Used by models to send vertex data previously baked via {@link MeshBuilder}.
	 * The fastest option and preferred whenever feasible.
	 */
	Consumer<Mesh> meshConsumer();

	/**
	 * Fabric causes vanilla baked models to send themselves
	 * via this interface. Can also be used by compound models that contain a mix
	 * of vanilla baked models, packaged quads and/or dynamic elements.
	 */
	Consumer<BakedModel> fallbackConsumer();

	/**
	 * Returns a {@link QuadEmitter} instance that emits directly to the render buffer.
	 * It remains necessary to call {@link QuadEmitter#emit()} to output the quad.
	 *
	 * <p>This method will always be less performant than passing pre-baked meshes
	 * via {@link #meshConsumer()}. It should be used sparingly for model components that
	 * demand it - text, icons, dynamic indicators, or other elements that vary too
	 * much for static baking to be feasible.
	 *
	 * <p>Calling this method invalidates any {@link QuadEmitter} returned earlier.
	 * Will be threadlocal/re-used - do not retain references.
	 */
	QuadEmitter getEmitter();

	/**
	 * Causes all models/quads/meshes sent to this consumer to be transformed by the provided
	 * {@link QuadTransform} that edits each quad before buffering. Quads in the mesh will
	 * be passed to the {@link QuadTransform} for modification before offsets, face culling or lighting are applied.
	 * Meant for animation and mesh customization.
	 *
	 * <p>You MUST call {@link #popTransform()} after model is done outputting quads.
	 *
	 * <p>More than one transformer can be added to the context.  Transformers are applied in reverse order.
	 * (Last pushed is applied first.)
	 *
	 * <p>Meshes are never mutated by the transformer - only buffered quads. This ensures thread-safe
	 * use of meshes/models across multiple chunk builders.
	 *
	 * <p>Only the renderer should implement or extend this interface.
	 */
	void pushTransform(QuadTransform transform);

	/**
	 * Removes the transformation added by the last call to {@link #pushTransform(QuadTransform)}.
	 * MUST be called before exiting from {@link FabricBakedModel} .emit... methods.
	 */
	void popTransform();

	@FunctionalInterface
	public interface QuadTransform {
		/**
		 * Return false to filter out quads from rendering. When more than one transform
		 * is in effect, returning false means unapplied transforms will not receive the quad.
		 */
		boolean transform(MutableQuadView quad);
	}
}
