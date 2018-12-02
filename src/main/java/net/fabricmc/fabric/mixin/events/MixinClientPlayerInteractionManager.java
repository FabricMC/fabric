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

package net.fabricmc.fabric.mixin.events;

import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.fabricmc.fabric.util.HandlerList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Facing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
	@Shadow
	private MinecraftClient client;
	@Shadow
	private ClientPlayNetworkHandler networkHandler;
	@Shadow
	private GameMode gameMode;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0), method = "attackBlock", cancellable = true)
	public void attackBlock(BlockPos pos, Facing facing, CallbackInfoReturnable<Boolean> info) {
		for (Object handler : ((HandlerList<PlayerInteractionEvent.Block>) PlayerInteractionEvent.BREAK_BLOCK).getBackingArray()) {
			PlayerInteractionEvent.Block event = (PlayerInteractionEvent.Block) handler;
			ActionResult result = event.interact(client.player, client.world, Hand.MAIN, pos, facing);
			if (result != ActionResult.PASS) {
				info.setReturnValue(result == ActionResult.SUCCESS);
				info.cancel();
				return;
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0), method = "method_2902", cancellable = true)
	public void method_2902(BlockPos pos, Facing facing, CallbackInfoReturnable<Boolean> info) {
		if (!gameMode.isCreative()) {
			return;
		}

		for (Object handler : ((HandlerList<PlayerInteractionEvent.Block>) PlayerInteractionEvent.BREAK_BLOCK).getBackingArray()) {
			PlayerInteractionEvent.Block event = (PlayerInteractionEvent.Block) handler;
			ActionResult result = event.interact(client.player, client.world, Hand.MAIN, pos, facing);
			if (result != ActionResult.PASS) {
				info.setReturnValue(result == ActionResult.SUCCESS);
				info.cancel();
				return;
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0), method = "interactBlock", cancellable = true)
	public void interactBlock(ClientPlayerEntity player, ClientWorld world, BlockPos pos, Facing facing, Vec3d vec, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		Object[] backingArray = ((HandlerList<PlayerInteractionEvent.BlockPositioned>) PlayerInteractionEvent.INTERACT_BLOCK).getBackingArray();
		if (backingArray.length > 0) {
			float hitX = (float) (vec.x - pos.getX());
			float hitY = (float) (vec.y - pos.getY());
			float hitZ = (float) (vec.z - pos.getZ());

			for (Object handler : backingArray) {
				PlayerInteractionEvent.BlockPositioned event = (PlayerInteractionEvent.BlockPositioned) handler;
				ActionResult result = event.interact(player, world, hand, pos, facing, hitX, hitY, hitZ);
				if (result != ActionResult.PASS) {
					if (result == ActionResult.SUCCESS) {
						this.networkHandler.sendPacket(new PlayerInteractBlockServerPacket(pos, facing, hand, hitX, hitY, hitZ));
					}
					info.setReturnValue(result);
					info.cancel();
					return;
				}
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0), method = "interactItem", cancellable = true)
	public void interactItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		for (Object handler : ((HandlerList<PlayerInteractionEvent.Item>) PlayerInteractionEvent.INTERACT_ITEM).getBackingArray()) {
			PlayerInteractionEvent.Item event = (PlayerInteractionEvent.Item) handler;
			ActionResult result = event.interact(player, world, hand);
			if (result != ActionResult.PASS) {
				info.setReturnValue(result);
				info.cancel();
				return;
			}
		}
	}
}
