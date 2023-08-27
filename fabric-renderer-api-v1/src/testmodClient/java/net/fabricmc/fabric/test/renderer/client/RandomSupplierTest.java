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

package net.fabricmc.fabric.test.renderer.client;

import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

/**
 * Tests that vanilla and Fabric API give the same random results.
 *
 * <p>Never do this in a real mod, this is purely for testing!
 */
public class RandomSupplierTest implements ClientModInitializer {
	private static long previousRandom = 0;
	private static boolean hasPreviousRandom = false;

	@Override
	public void onInitializeClient() {
		var checkingModel = new RandomCheckingBakedModel();
		var weighted = new WeightedBakedModel(List.of(
				Weighted.of(checkingModel, 1),
				Weighted.of(checkingModel, 2)));
		var multipart = new MultipartBakedModel(List.of(
				Pair.of(state -> true, weighted),
				Pair.of(state -> true, weighted)));
		var weightedAgain = new WeightedBakedModel(List.of(
				Weighted.of(multipart, 1),
				Weighted.of(multipart, 2)));

		long startingSeed = 42;
		Random random = Random.create();

		random.setSeed(startingSeed);
		weightedAgain.getQuads(Blocks.STONE.getDefaultState(), null, random);

		random.setSeed(startingSeed);
		weightedAgain.getQuads(Blocks.STONE.getDefaultState(), null, random);

		Supplier<Random> randomSupplier = () -> {
			random.setSeed(startingSeed);
			return random;
		};
		weightedAgain.emitBlockQuads(null, Blocks.STONE.getDefaultState(), BlockPos.ORIGIN, randomSupplier, null);
	}

	private static class RandomCheckingBakedModel implements BakedModel {
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
			long value = random.nextLong();

			if (hasPreviousRandom) {
				if (value != previousRandom) {
					throw new AssertionError("Random value is not the same as the previous one!");
				}
			} else {
				hasPreviousRandom = true;
				previousRandom = value;
			}

			return List.of();
		}

		@Override
		public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
			getQuads(state, null, randomSupplier.get());
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
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
		public Sprite getParticleSprite() {
			return null;
		}

		@Override
		public ModelTransformation getTransformation() {
			return null;
		}

		@Override
		public ModelOverrideList getOverrides() {
			return null;
		}
	}
}
