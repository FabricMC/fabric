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

import net.fabricmc.fabric.api.event.client.player.ClientPickBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
	private boolean fabric_itemPickSucceeded;
	private boolean fabric_itemPickCancelled;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemPick()V"), method = "handleInputEvents")
	private void sillyRedirection() {
		fabric_doItemPickWrapper();
	}

	private void fabric_doItemPickWrapper() {
		// I HATE EVERYTHING THAT STANDS FOR THIS CODE
		fabric_itemPickSucceeded = false;
		doItemPick();
		if (!fabric_itemPickSucceeded) {
			// vanilla method bailed early, so we have to do this absurd kludge
			ClientPickBlockCallback.Container ctr = new ClientPickBlockCallback.Container(ItemStack.EMPTY);
			//noinspection ConstantConditions
			MinecraftClient client = (MinecraftClient) (Object) this;

			if (ClientPickBlockCallback.EVENT.invoker().pick(client.player, client.hitResult, ctr)) {
				// we cannot just jump into the middle of doItemPick, so we have to
				// mimic vanilla logic here

				ItemStack stack = ctr.getStack();
				PlayerInventory playerInventory = client.player.inventory;

				if (client.player.abilities.creativeMode && Screen.isControlPressed() && client.hitResult.getType() == HitResult.Type.BLOCK) {
					BlockEntity be = client.world.getBlockEntity(((BlockHitResult) client.hitResult).getBlockPos());
					if (be != null) {
						stack = addBlockEntityNbt(stack, be);
					}
				}

				if (client.player.abilities.creativeMode) {
					playerInventory.addPickBlock(stack);
					client.interactionManager.method_2909(client.player.getStackInHand(Hand.MAIN), 36 + playerInventory.selectedSlot);
				} else {
					int slot = playerInventory.getSlotWithStack(stack);
					if (slot >= 0) {
						if (PlayerInventory.isValidHotbarIndex(slot)) {
							playerInventory.selectedSlot = slot;
						} else {
							client.interactionManager.pickFromInventory(slot);
						}
					}
				}
			}
		}
	}

	@Shadow
	public abstract void doItemPick();
	@Shadow
	public abstract ItemStack addBlockEntityNbt(ItemStack itemStack_1, BlockEntity blockEntity_1);

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2), method = "doItemPick", ordinal = 0)
	public ItemStack modifyItemPick(ItemStack stack) {
		fabric_itemPickSucceeded = true;

		ClientPickBlockCallback.Container ctr = new ClientPickBlockCallback.Container(stack);
		//noinspection ConstantConditions
		MinecraftClient client = (MinecraftClient) (Object) this;

		boolean toContinue = ClientPickBlockCallback.EVENT.invoker().pick(client.player, client.hitResult, ctr);
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
