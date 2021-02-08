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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
	private boolean fabric_itemPickCancelled;

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I"), method = "doItemPick", ordinal = 0)
	public ItemStack modifyItemPick(ItemStack stack) {
		MinecraftClient client = (MinecraftClient) (Object) this;
		ItemStack result = ClientPickBlockApplyCallback.EVENT.invoker().pick(client.player, client.crosshairTarget, stack);
		fabric_itemPickCancelled = result.isEmpty();
		return result;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I"), method = "doItemPick", cancellable = true)
	public void cancelItemPick(CallbackInfo info) {
		if (fabric_itemPickCancelled) {
			info.cancel();
		}
	}
}
