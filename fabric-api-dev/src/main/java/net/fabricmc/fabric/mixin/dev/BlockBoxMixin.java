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

package net.fabricmc.fabric.mixin.dev;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.impl.FabricDevProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.util.math.BlockBox;

@Mixin(BlockBox.class)
public class BlockBoxMixin {
	@ModifyExpressionValue(method = "<init>(IIIIII)V", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private boolean mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDevProperties.THROW_ON_INVALID_BLOCK_BOXES;
	}
}
