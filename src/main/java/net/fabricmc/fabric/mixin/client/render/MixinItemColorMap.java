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

package net.fabricmc.fabric.mixin.client.render;

import net.fabricmc.fabric.impl.client.render.ColorProviderRegistryImpl;
import net.minecraft.client.render.block.BlockColorMap;
import net.minecraft.client.render.item.ItemColorMap;
import net.minecraft.client.render.item.ItemColorMapper;
import net.minecraft.item.ItemContainer;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColorMap.class)
public class MixinItemColorMap implements ColorProviderRegistryImpl.ColorMapperHolder<ItemContainer, ItemColorMapper> {
	@Shadow
	@Final
	private IdList<ItemColorMapper> field_1996;

	@Inject(method = "method_1706", at = @At("RETURN"))
	private static void method_1706(BlockColorMap blockMap, CallbackInfoReturnable<ItemColorMap> info) {
		ColorProviderRegistryImpl.ITEM.initialize(info.getReturnValue());
	}

	@Override
	public ItemColorMapper get(ItemContainer item) {
		return field_1996.getInt(Registry.ITEM.getRawId(item.getItem()));
	}
}
