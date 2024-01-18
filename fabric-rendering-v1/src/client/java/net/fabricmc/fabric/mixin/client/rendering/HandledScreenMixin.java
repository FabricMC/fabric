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

import java.util.ArrayList;
import java.util.Optional;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;
import net.fabricmc.fabric.impl.client.rendering.tooltip.MultiTooltipData;

@Mixin(HandledScreen.class)
class HandledScreenMixin {
	@WrapOperation(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;"))
	private Optional<TooltipData> addMultiData(ItemStack stack, Operation<Optional<TooltipData>> original) {
		var multiData = new MultiTooltipData(new ArrayList<>());
		TooltipDataCallback.EVENT.invoker().appendTooltipData(stack, multiData.tooltipData());

		if (multiData.tooltipData().isEmpty()) {
			return original.call(stack);
		}

		original.call(stack).ifPresent(multiData.tooltipData()::add);
		return Optional.of(multiData);
	}
}
