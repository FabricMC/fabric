/*
 * Copyright (c) 2024 FabricMC
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

package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import net.fabricmc.fabric.FabricDev;

@Mixin({Block.class, Item.class})
public class BlockAndItemMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.LOG_CONVENTION_ISSUES")
	@ModifyExpressionValue(method = {
			"<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
			"<init>(Lnet/minecraft/item/Item$Settings;)V"
	}, at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.LOG_CONVENTION_ISSUES;
	}
}
