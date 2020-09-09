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

package net.fabricmc.fabric.mixin.event.interaction;

import net.fabricmc.fabric.api.event.player.PlayerBlockPlaceEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockItem.class)
public class MixinBlockItem
{
	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void placeBlock(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir)
	{
		boolean result = PlayerBlockPlaceEvents.BEFORE.invoker()
													  .beforeBlockPlace(
															  context.getWorld(),
															  context.getPlayer(),
															  context.getBlockPos(),
															  state,
															  context.getWorld().getBlockEntity(context.getBlockPos())
													  );

		if (!result) {
			PlayerBlockPlaceEvents.CANCELED.invoker().onBlockPlaceCanceled(
					context.getWorld(),
					context.getPlayer(),
					context.getBlockPos(),
					state,
					context.getWorld().getBlockEntity(context.getBlockPos())
			);

			cir.setReturnValue(false);
		}
	}

	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void blockPlaced(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir)
	{
		PlayerBlockPlaceEvents.AFTER.invoker()
									.afterBlockPlace(
											context.getWorld(),
											context.getPlayer(),
											context.getBlockPos(),
											state,
											context.getWorld().getBlockEntity(context.getBlockPos())
									);
	}
}
