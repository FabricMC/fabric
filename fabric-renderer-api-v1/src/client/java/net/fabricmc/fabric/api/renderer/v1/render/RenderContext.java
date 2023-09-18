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
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;

/**
 * This defines the instance made available to models for buffering vertex data at render time.
 *
 * <p>Only the renderer should implement or extend this interface.
 */
public interface RenderContext {
	/**
	 * Returns a {@link QuadEmitter} instance that is used to output quads.
	 * It is necessary to call {@link QuadEmitter#emit()} to output a quad.
	 *
	 * <p>The renderer may optimize certain operations such as
	 * {@link Mesh#outputTo(QuadEmitter)} when used with this emitter. Thus, using
	 * those operations is preferred to using the emitter directly. It should be
	 * used sparingly for model components that demand it - text, icons, dynamic
	 * indicators, or other elements that vary too much for static baking to be
	 * feasible.
	 *
	 * <p>Calling this method invalidates any {@link QuadEmitter} returned earlier.
	 * Will be thread-local/re-used - do not retain references.
	 */
	QuadEmitter getEmitter();

	/**
	 * Returns whether this context currently has at least one transform.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default boolean hasTransform() {
		return true;
	}

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
	 * <p>Using the {@linkplain #getEmitter() quad emitter of this context} from the inside of a quad transform is not supported.
	 */
	void pushTransform(QuadTransform transform);

	/**
	 * Removes the transformation added by the last call to {@link #pushTransform(QuadTransform)}.
	 * MUST be called before exiting from {@link FabricBakedModel} .emit... methods.
	 */
	void popTransform();

	/**
	 * Returns {@code true} if the given face will be culled away.
	 *
	 * <p>This function can be used to skip complex transformations of quads that will be culled anyway.
	 * The cull face of a quad is determined by {@link QuadView#cullFace()}.
	 * Note that if {@linkplain #hasTransform() there is a transform}, no computation should be skipped,
	 * because the cull face might be changed by the transform,
	 * or the transform might wish to receive culled faces too.
	 *
	 * <p>This function can only be used on a block render context (i.e. in {@link FabricBakedModel#emitBlockQuads}).
	 * Calling it on another context (e.g. in {@link FabricBakedModel#emitItemQuads}) will throw an exception.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default boolean isFaceCulled(@Nullable Direction face) {
		return false;
	}

	/**
	 * Returns the current transformation mode.
	 *
	 * <p>This function can only be used on an item render context (i.e. in {@link FabricBakedModel#emitItemQuads}).
	 * Calling it on another context (e.g. in {@link FabricBakedModel#emitBlockQuads}) will throw an exception.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default ModelTransformationMode itemTransformationMode() {
		return ModelTransformationMode.NONE;
	}

	@FunctionalInterface
	interface QuadTransform {
		/**
		 * Return false to filter out quads from rendering. When more than one transform
		 * is in effect, returning false means unapplied transforms will not receive the quad.
		 */
		boolean transform(MutableQuadView quad);
	}

	/**
	 * @deprecated Use {@link Mesh#outputTo(QuadEmitter)} instead.
	 */
	@Deprecated
	default Consumer<Mesh> meshConsumer() {
		return mesh -> mesh.outputTo(getEmitter());
	}

	/**
	 * @deprecated Use {@link FabricBakedModel#emitBlockQuads(BlockRenderView, BlockState, BlockPos, Supplier, RenderContext) emitBlockQuads}
	 * or {@link FabricBakedModel#emitItemQuads(ItemStack, Supplier, RenderContext) emitItemQuads} on the baked model
	 * that you want to consume instead.
	 */
	@Deprecated(forRemoval = true)
	BakedModelConsumer bakedModelConsumer();

	/**
	 * @deprecated Use {@link FabricBakedModel#emitBlockQuads(BlockRenderView, BlockState, BlockPos, Supplier, RenderContext) emitBlockQuads}
	 * or {@link FabricBakedModel#emitItemQuads(ItemStack, Supplier, RenderContext) emitItemQuads} on the baked model
	 * that you want to consume instead.
	 */
	@Deprecated(forRemoval = true)
	default Consumer<BakedModel> fallbackConsumer() {
		return bakedModelConsumer();
	}

	@Deprecated(forRemoval = true)
	interface BakedModelConsumer extends Consumer<BakedModel> {
		/**
		 * Render a baked model by processing its {@linkplain BakedModel#getQuads} using the rendered block state.
		 *
		 * <p>For block contexts, this will pass the block state being rendered to {@link BakedModel#getQuads}.
		 * For item contexts, this will pass a {@code null} block state to {@link BakedModel#getQuads}.
		 * {@link #accept(BakedModel, BlockState)} can be used instead to pass the block state explicitly.
		 */
		@Override
		void accept(BakedModel model);

		/**
		 * Render a baked model by processing its {@linkplain BakedModel#getQuads} with an explicit block state.
		 *
		 * <p>This overload allows passing the block state (or {@code null} to query the item quads).
		 * This is useful when a model is being wrapped, and expects a different
		 * block state than the one of the block being rendered.
		 *
		 * <p>For item render contexts, you can use this function if you want to render the model with a specific block state.
		 * Otherwise, use {@linkplain #accept(BakedModel)} the other overload} to render the usual item quads.
		 */
		void accept(BakedModel model, @Nullable BlockState state);
	}
}
