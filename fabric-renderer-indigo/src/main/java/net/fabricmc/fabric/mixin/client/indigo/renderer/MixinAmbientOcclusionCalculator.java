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

package net.fabricmc.fabric.mixin.client.indigo.renderer;

import java.util.BitSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessAmbientOcclusionCalculator;

@Mixin(targets = "net.minecraft.client.render.block.BlockModelRenderer$AmbientOcclusionCalculator")
public abstract class MixinAmbientOcclusionCalculator implements AccessAmbientOcclusionCalculator {
	@Shadow private float[] brightness;
	@Shadow private int[] light;

	@Shadow
	public abstract void apply(BlockRenderView blockRenderView, BlockState blockState, BlockPos pos, Direction face, float[] aoData, BitSet controlBits);

	@Override
	public float[] fabric_colorMultiplier() {
		return brightness;
	}

	@Override
	public int[] fabric_brightness() {
		return light;
	}

	@Override
	public void fabric_apply(BlockRenderView blockRenderView, BlockState blockState, BlockPos pos, Direction face, float[] aoData, BitSet controlBits) {
		apply(blockRenderView, blockState, pos, face, aoData, controlBits);
	}
}
