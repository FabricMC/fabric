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

package net.fabricmc.fabric.test.renderer.simple.client;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;

final class FrameBakedModel implements BakedModel, FabricBakedModel {
	private final Mesh frameMesh;
	private final Sprite frameSprite;
	private final RenderMaterial translucentMaterial;
	private final RenderMaterial translucentEmissiveMaterial;

	FrameBakedModel(Mesh frameMesh, Sprite frameSprite) {
		this.frameMesh = frameMesh;
		this.frameSprite = frameSprite;

		MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();
		this.translucentMaterial = finder.blendMode(0, BlendMode.TRANSLUCENT).find();
		finder.clear();
		this.translucentEmissiveMaterial = finder.blendMode(0, BlendMode.TRANSLUCENT).emissive(0, true).find();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return Collections.emptyList(); // Renderer API makes this obsolete, so return no quads
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true; // we want the block to have a shadow depending on the adjacent blocks
	}

	@Override
	public boolean hasDepth() {
		return false;
	}

	@Override
	public boolean isSideLit() {
		return true; // we want the block to be lit from the side when rendered as an item
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return this.frameSprite;
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelHelper.MODEL_TRANSFORM_BLOCK;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		// Emit our frame mesh
		context.meshConsumer().accept(this.frameMesh);

		RenderAttachedBlockView renderAttachedBlockView = (RenderAttachedBlockView) blockView;

		// We cannot access the block entity from here. We should instead use the immutable render attachments provided by the block entity.
		@Nullable
		Block data = (Block) renderAttachedBlockView.getBlockEntityRenderAttachment(pos);

		if (data == null) {
			return; // No inner block to render
		}

		BlockState innerState = data.getDefaultState();

		// Now, we emit a transparent scaled-down version of the inner model
		// Try both emissive and non-emissive versions of the translucent material
		RenderMaterial material = pos.getX() % 2 == 0 ? translucentMaterial : translucentEmissiveMaterial;

		emitInnerQuads(context, material, () -> {
			// Use emitBlockQuads to allow for Renderer API features
			((FabricBakedModel) MinecraftClient.getInstance().getBlockRenderManager().getModel(innerState)).emitBlockQuads(blockView, innerState, pos, randomSupplier, context);
		});
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		// Emit our frame mesh
		context.meshConsumer().accept(this.frameMesh);

		// Emit a scaled-down fence for testing, trying both materials again.
		RenderMaterial material = stack.hasCustomName() ? translucentEmissiveMaterial : translucentMaterial;

		BlockState innerState = Blocks.OAK_FENCE.getDefaultState();

		emitInnerQuads(context, material, () -> {
			// Need to use the fallback consumer directly:
			// - we can't use emitBlockQuads because we don't have a blockView
			// - we can't use emitItemQuads because multipart models don't have item quads
			context.bakedModelConsumer().accept(MinecraftClient.getInstance().getBlockRenderManager().getModel(innerState), innerState);
		});
	}

	/**
	 * Emit a scaled-down version of the inner model.
	 */
	private void emitInnerQuads(RenderContext context, RenderMaterial material, Runnable innerModelEmitter) {
		// Let's push a transform to scale the model down and make it transparent
		context.pushTransform(quad -> {
			// Scale model down
			for (int vertex = 0; vertex < 4; ++vertex) {
				float x = quad.x(vertex) * 0.8f + 0.1f;
				float y = quad.y(vertex) * 0.8f + 0.1f;
				float z = quad.z(vertex) * 0.8f + 0.1f;
				quad.pos(vertex, x, y, z);
			}

			// Make the quad partially transparent
			// Change material to translucent
			quad.material(material);

			// Change vertex colors to be partially transparent
			for (int vertex = 0; vertex < 4; ++vertex) {
				int color = quad.spriteColor(vertex, 0);
				int alpha = (color >> 24) & 0xFF;
				alpha = alpha * 3 / 4;
				color = (color & 0xFFFFFF) | (alpha << 24);
				quad.spriteColor(vertex, 0, color);
			}

			// Return true because we want the quad to be rendered
			return true;
		});

		// Emit the inner block model
		innerModelEmitter.run();

		// Let's not forget to pop the transform!
		context.popTransform();
	}
}
