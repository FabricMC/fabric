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

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.block.v1.FabricBlockState;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.test.renderer.simple.RendererTest;

/**
 * Very crude implementation of a pillar block model that connects with pillars above and below.
 */
public class PillarBakedModel implements BakedModel, FabricBakedModel {
	private enum ConnectedTexture {
		ALONE, BOTTOM, MIDDLE, TOP
	}

	// alone, bottom, middle, top
	private final Sprite[] sprites;

	public PillarBakedModel(Sprite[] sprites) {
		this.sprites = sprites;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		emitQuads(context.getEmitter(), blockView, state, pos);
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		emitQuads(context.getEmitter(), null, null, null);
	}

	private void emitQuads(QuadEmitter emitter, @Nullable BlockRenderView blockView, @Nullable BlockState state, @Nullable BlockPos pos) {
		for (Direction side : Direction.values()) {
			ConnectedTexture texture = ConnectedTexture.ALONE;

			if (side.getAxis().isHorizontal() && blockView != null && state != null && pos != null) {
				boolean connectAbove = canConnect(blockView, pos.offset(Direction.UP), side, state, pos);
				boolean connectBelow = canConnect(blockView, pos.offset(Direction.DOWN), side, state, pos);

				if (connectAbove && connectBelow) {
					texture = ConnectedTexture.MIDDLE;
				} else if (connectAbove) {
					texture = ConnectedTexture.BOTTOM;
				} else if (connectBelow) {
					texture = ConnectedTexture.TOP;
				}
			}

			emitter.square(side, 0, 0, 1, 1, 0);
			emitter.spriteBake(0, sprites[texture.ordinal()], MutableQuadView.BAKE_LOCK_UV);
			emitter.spriteColor(0, -1, -1, -1, -1);
			emitter.emit();
		}
	}

	private static boolean canConnect(BlockRenderView blockView, BlockPos pos, Direction side, BlockState sourceState, BlockPos sourcePos) {
		// In this testmod we can't rely on injected interfaces - in normal mods the (FabricBlockState) cast will be unnecessary
		return ((FabricBlockState) blockView.getBlockState(pos)).getAppearance(blockView, pos, side, sourceState, sourcePos).isOf(RendererTest.PILLAR);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return List.of();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepth() {
		return false;
	}

	@Override
	public boolean isSideLit() {
		return true;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return sprites[0];
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelHelper.MODEL_TRANSFORM_BLOCK;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}
}
