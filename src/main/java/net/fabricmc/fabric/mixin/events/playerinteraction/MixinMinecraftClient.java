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

package net.fabricmc.fabric.mixin.events.playerinteraction;

import net.fabricmc.fabric.api.event.client.player.ClientPickItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	private boolean fabric_itemPickCancelled;

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2), method = "doItemPick", ordinal = 0)
	public ItemStack modifyItemPick(ItemStack stack) {
		ClientPickItemCallback.Container ctr = new ClientPickItemCallback.Container(stack);
		//noinspection ConstantConditions
		MinecraftClient client = (MinecraftClient) (Object) this;

		boolean toContinue = ClientPickItemCallback.EVENT.invoker().pick(client.player, client.hitResult, ctr);
		if (!toContinue) {
			fabric_itemPickCancelled = true;
			return ItemStack.EMPTY;
		} else {
			fabric_itemPickCancelled = false;
			return ctr.getStack();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2), method = "doItemPick", cancellable = true)
	public void cancelItemPick(CallbackInfo info) {
		if (fabric_itemPickCancelled) {
			fabric_itemPickCancelled = false;
			info.cancel();
		}
	}
}
