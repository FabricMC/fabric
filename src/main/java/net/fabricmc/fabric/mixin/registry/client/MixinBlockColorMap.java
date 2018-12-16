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

package net.fabricmc.fabric.mixin.registry.client;

import net.fabricmc.fabric.impl.registry.IdListUpdater;
import net.fabricmc.fabric.registry.ListenableRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockColorMap;
import net.minecraft.client.render.block.BlockColorMapper;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColorMap.class)
public class MixinBlockColorMap implements IdListUpdater.Container<BlockColorMapper> {
	@Shadow
	private IdList<BlockColorMapper> mappers;

	@Inject(method = "create", at = @At("RETURN"))
	private static void create(CallbackInfoReturnable<BlockColorMap> info) {
		((ListenableRegistry) Registry.BLOCK).registerListener(new IdListUpdater<Block, BlockColorMapper>((IdListUpdater.Container<BlockColorMapper>) (Object) info.getReturnValue()));
	}

	@Override
	public IdList<BlockColorMapper> getIdListForRegistryUpdating() {
		return mappers;
	}
}
