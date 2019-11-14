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

package net.fabricmc.fabric.mixin.client.rendering;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;

@Mixin(BlockColors.class)
public class MixinBlockColorMap implements ColorProviderRegistryImpl.ColorMapperHolder<Block, BlockColorProvider> {
	@Shadow
	@Final
	private IdList<BlockColorProvider> providers;

	@Inject(method = "create", at = @At("RETURN"))
	private static void create(CallbackInfoReturnable<BlockColors> info) {
		ColorProviderRegistryImpl.BLOCK.initialize(info.getReturnValue());
	}

	@Override
	public BlockColorProvider get(Block block) {
		return providers.get(Registry.BLOCK.getRawId(block));
	}
}
