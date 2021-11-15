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

package net.fabricmc.fabric.mixin.content.registry;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
	@Inject(method = "getDecreasedOxidationBlock", at = @At("RETURN"), cancellable = true)
	private static void getDecreasedOxidationBlockInject(Block block, CallbackInfoReturnable<Optional<Block>> cir) {
		if (cir.getReturnValue().isEmpty()) {
			cir.setReturnValue(OxidizableBlocksRegistry.getDecreasedOxidizationBlock(block));
		}
	}

	@Inject(method = "getIncreasedOxidationBlock", at = @At("RETURN"), cancellable = true)
	private static void getIncreasedOxidationBlockInject(Block block, CallbackInfoReturnable<Optional<Block>> cir) {
		if (cir.getReturnValue().isEmpty()) {
			cir.setReturnValue(OxidizableBlocksRegistry.getIncreasedOxidizationBlock(block));
		}
	}

	@Inject(method = "getUnaffectedOxidationBlock", at = @At("RETURN"), cancellable = true)
	private static void getUnaffectedOxidationBlockInject(Block block, CallbackInfoReturnable<Block> cir) {
		if (cir.getReturnValue().equals(block)) {
			cir.setReturnValue(OxidizableBlocksRegistry.getUnaffectedOxidizationBlock(block));
		}
	}
}
