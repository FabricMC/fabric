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

package net.fabricmc.fabric.mixin.blockrenderlayer;

import java.util.Map;

import net.minecraft.class_4696;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;

@Mixin(class_4696.class)
public class MixinBlockRenderLayer {
	@Shadow private static Map<Block, RenderLayer> field_21469;
	@Shadow private static Map<Item, RenderLayer> field_21470;
	@Shadow private static Map<Fluid, RenderLayer> field_21471;
	
	@Inject(method = "<clinit>*", at = @At("RETURN"))
	private static void onInitialize(CallbackInfo info) {
		BlockRenderLayerMapImpl.initialize(field_21469::put, field_21470::put, field_21471::put);
	}
}
