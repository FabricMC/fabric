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

package net.fabricmc.fabric.mixin.renderer.client;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

@Mixin(WeightedBakedModel.class)
public class MixinWeightedBakedModel implements FabricBakedModel {
	@Shadow
	@Final
	private int totalWeight;
	@Shadow
	@Final
	private List<WeightedPicker.Entry> models; // WeightedBakedModel.Entry is not visible
	@Unique
	boolean isVanilla = true;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void onInit(List<WeightedBakedModelEntryAccessor> models, CallbackInfo cb) {
		for (int i = 0; i < models.size(); i++) {
			if (!((FabricBakedModel) models.get(i).getModel()).isVanillaAdapter()) {
				isVanilla = false;
				break;
			}
		}
	}

	@Override
	public boolean isVanillaAdapter() {
		return isVanilla;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		((FabricBakedModel) ((WeightedBakedModelEntryAccessor) WeightedPicker.getAt(this.models, Math.abs((int) randomSupplier.get().nextLong()) % this.totalWeight)).getModel()).emitBlockQuads(blockView, state, pos, randomSupplier, context);
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		((FabricBakedModel) ((WeightedBakedModelEntryAccessor) WeightedPicker.getAt(this.models, Math.abs((int) randomSupplier.get().nextLong()) % this.totalWeight)).getModel()).emitItemQuads(stack, randomSupplier, context);
	}
}
