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

package net.fabricmc.fabric.mixin.event.interaction.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	private boolean fabric_itemPickCancelled;

	@SuppressWarnings("deprecation")
	private ItemStack fabric_emulateOldPick() {
		MinecraftClient client = (MinecraftClient) (Object) this;
		ClientPickBlockCallback.Container ctr = new ClientPickBlockCallback.Container(ItemStack.EMPTY);
		ClientPickBlockCallback.EVENT.invoker().pick(client.player, client.crosshairTarget, ctr);
		return ctr.getStack();
	}

	@Inject(at = @At("HEAD"), method = "doItemPick", cancellable = true)
	private void fabric_doItemPickWrapper(CallbackInfo info) {
		MinecraftClient client = (MinecraftClient) (Object) this;

		// Do a "best effort" emulation of the old events.
		ItemStack stack = ClientPickBlockGatherCallback.EVENT.invoker().pick(client.player, client.crosshairTarget);

		// TODO: Remove in 0.3.0
		if (stack.isEmpty()) {
			stack = fabric_emulateOldPick();
		}

		if (stack.isEmpty()) {
			// fall through
		} else {
			info.cancel();

			// I don't like that we clone vanilla logic here, but it's our best bet for now.
			PlayerInventory playerInventory = client.player.getInventory();

			if (client.player.getAbilities().creativeMode && Screen.hasControlDown() && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				BlockEntity be = client.world.getBlockEntity(((BlockHitResult) client.crosshairTarget).getBlockPos());

				if (be != null) {
					addBlockEntityNbt(stack, be);
				}
			}

			stack = ClientPickBlockApplyCallback.EVENT.invoker().pick(client.player, client.crosshairTarget, stack);

			if (stack.isEmpty()) {
				return;
			}

			if (client.player.getAbilities().creativeMode) {
				playerInventory.addPickBlock(stack);
				client.interactionManager.clickCreativeStack(client.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
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

	@Shadow
	protected abstract void addBlockEntityNbt(ItemStack itemStack_1, BlockEntity blockEntity_1);

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I", shift = At.Shift.BEFORE), method = "doItemPick", ordinal = 0)
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

	@Shadow
	private ClientPlayerEntity player;

	@Shadow
	public abstract ClientPlayNetworkHandler getNetworkHandler();

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/network/ClientPlayerInteractionManager.interactEntityAtLocation(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/hit/EntityHitResult;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
			),
			method = "doItemUse",
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void injectUseEntityCallback(CallbackInfo ci, Hand[] hands, int i1, int i2, Hand hand, ItemStack stack, EntityHitResult hitResult, Entity entity) {
		ActionResult result = UseEntityCallback.EVENT.invoker().interact(player, player.getEntityWorld(), hand, entity, hitResult);

		if (result != ActionResult.PASS) {
			if (result.isAccepted()) {
				Vec3d hitVec = hitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
				getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.interactAt(entity, player.isSneaking(), hand, hitVec));
			}

			if (result.shouldSwingHand()) {
				player.swingHand(hand);
			}

			ci.cancel();
		}
	}
}
