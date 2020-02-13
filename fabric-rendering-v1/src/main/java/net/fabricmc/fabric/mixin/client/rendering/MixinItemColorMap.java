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

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;

@Mixin(ItemColors.class)
public class MixinItemColorMap implements ColorProviderRegistryImpl.ColorMapperHolder<ItemConvertible, ItemColorProvider> {
	@Shadow
	@Final
	private IdList<ItemColorProvider> providers;

	@Inject(method = "create", at = @At("RETURN"))
	private static void create(BlockColors blockMap, CallbackInfoReturnable<ItemColors> info) {
		ColorProviderRegistryImpl.ITEM.initialize(info.getReturnValue());
	}

	@Override
	public ItemColorProvider get(ItemConvertible item) {
		return providers.get(Registry.ITEM.getRawId(item.asItem()));
	}
}
