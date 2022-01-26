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

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;

final class FrameBakedModel implements BakedModel, FabricBakedModel {
	private final Mesh frameMesh;
	private final Sprite frameSprite;

	FrameBakedModel(Mesh frameMesh, Sprite frameSprite) {
		this.frameMesh = frameMesh;
		this.frameSprite = frameSprite;
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
		return false;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getSprite() {
		return this.frameSprite;
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelTransformation.NONE;
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

		Sprite sprite = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelManager().getBlockModels().getSprite(data.getDefaultState());
		QuadEmitter emitter = context.getEmitter();

		// We can emit our quads outside of the mesh as the block being put in the frame is very much dynamic.
		// Emit the quads for each face of the block inside the frame
		for (Direction direction : Direction.values()) {
			// Add a face, with an inset to give the appearance of the block being in a frame.
			emitter.square(direction, 0.1F, 0.1F, 0.9F, 0.9F, 0.1F)
					// Set the sprite of the fact, use whole texture via BAKE_LOCK_UV
					.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV)
					// Allow textures
					// TODO: the magic values here are not documented at all and probably should be
					.spriteColor(0, -1, -1, -1, -1)
					// Emit the quad
					.emit();
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		// TODO: Implement an item test.
		// For now we will just leave this as I have not added a block item yet
	}
}
