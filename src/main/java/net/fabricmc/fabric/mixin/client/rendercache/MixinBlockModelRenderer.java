/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.client.rendercache;

import net.fabricmc.fabric.api.client.model.DynamicBakedModel;
import net.fabricmc.fabric.api.client.model.RenderCacheView;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {
	private static final Direction[] fabric_directionValues = Direction.values();

	// TODO NORELEASE: should this be split into two hooks to arrow overriding AO check?
	@Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
	public void tesselateSmoothRedir(ExtendedBlockView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder builder, boolean bool, Random random, long seed, CallbackInfoReturnable<Boolean> info) {
		if (model instanceof DynamicBakedModel && view instanceof RenderCacheView) {
			boolean useAO = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();

			try {
				Object renderData = ((DynamicBakedModel) model).getRenderData(state, (RenderCacheView) view, pos);

				boolean result;
				if (useAO) {
					result = fabric_tesselateSmoothDynamic(renderData, view, (DynamicBakedModel) model, state, pos, builder, bool, random, seed);
				} else {
					result = fabric_tesselateFlatDynamic(renderData, view, (DynamicBakedModel) model, state, pos, builder, bool, random, seed);
				}
				info.setReturnValue(result);
				info.cancel();
			} catch (Throwable t) {
				CrashReport report = CrashReport.create(t, "Tesselating dynamic block model");
				CrashReportElement reportElementModel = report.addElement("Dynamic block being tesselated");
				CrashReportElement.addBlockInfo(reportElementModel, pos, state);
				reportElementModel.add("Using AO", useAO);
				throw new CrashException(report);
			}
		}
	}

	public boolean fabric_tesselateSmoothDynamic(Object renderData, ExtendedBlockView view, DynamicBakedModel model, BlockState state, BlockPos pos, BufferBuilder builder, boolean allSides, Random rand, long seed) {
		// TODO NORELEASE: AmbientOcclusionCalculator is non-public... ugh...
		return fabric_tesselateFlatDynamic(renderData, view, model, state, pos, builder, allSides, rand, seed);
	}

	public boolean fabric_tesselateFlatDynamic(Object renderData, ExtendedBlockView view, DynamicBakedModel model, BlockState state, BlockPos pos, BufferBuilder builder, boolean allSides, Random rand, long seed) {
		List<BakedQuad> quads;
		BitSet bitSet = new BitSet(3);
		boolean rendered = false;

		for (Direction direction : fabric_directionValues) {
			rand.setSeed(seed);
			//noinspection unchecked
			quads = model.getQuads(renderData, state, direction, rand);
			if (!quads.isEmpty()) {
				int brightness = state.getBlockBrightness(view, pos.offset(direction));
				tesselateQuadsFlat(view, state, pos, brightness, false, builder, quads, bitSet);
				quads.clear();
				rendered = true;
			}
		}

		//noinspection unchecked
		quads = model.getQuads(renderData, state, null, rand);
		if (!quads.isEmpty()) {
			tesselateQuadsFlat(view, state, pos, -1, true, builder, quads, bitSet);
			rendered = true;
		}

		return rendered;
	}

	@Shadow
	private void tesselateQuadsFlat(ExtendedBlockView extendedBlockView_1, BlockState blockState_1, BlockPos blockPos_1, int int_1, boolean boolean_1, BufferBuilder bufferBuilder_1, List<BakedQuad> list_1, BitSet bitSet_1) {

	}
}