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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_7204;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
	@Shadow
	private MinecraftClient client;
	@Shadow
	private ClientPlayNetworkHandler networkHandler;
	@Shadow
	private GameMode gameMode;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0), method = "attackBlock", cancellable = true)
	public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
		ActionResult result = AttackBlockCallback.EVENT.invoker().interact(client.player, client.world, Hand.MAIN_HAND, pos, direction);

		if (result != ActionResult.PASS) {
			// Returning true will spawn particles and trigger the animation of the hand -> only for SUCCESS.
			info.setReturnValue(result == ActionResult.SUCCESS);

			// We also need to let the server process the action if it's accepted.
			if (result.isAccepted()) {
				method_41931(client.world, id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, id));
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0), method = "updateBlockBreakingProgress", cancellable = true)
	public void method_2902(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
		if (!gameMode.isCreative()) {
			return;
		}

		ActionResult result = AttackBlockCallback.EVENT.invoker().interact(client.player, client.world, Hand.MAIN_HAND, pos, direction);

		if (result != ActionResult.PASS) {
			// Returning true will spawn particles and trigger the animation of the hand -> only for SUCCESS.
			info.setReturnValue(result == ActionResult.SUCCESS);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;method_41931(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/class_7204;)V"), method = "interactBlock", cancellable = true)
	public void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
		// hook interactBlock between the world border check and the actual block interaction to invoke the use block event first
		// this needs to be in interactBlock to avoid sending a packet in line with the event javadoc

		if (player.isSpectator()) return; // vanilla spectator check happens later, repeat it before the event to avoid false invocations

		ActionResult result = UseBlockCallback.EVENT.invoker().interact(player, player.world, hand, blockHitResult);

		if (result != ActionResult.PASS) {
			if (result == ActionResult.SUCCESS) {
				// send interaction packet to the server with a new sequentially assigned id
				method_41931(player.clientWorld, id -> new PlayerInteractBlockC2SPacket(hand, blockHitResult, id));
			}

			info.setReturnValue(result);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), method = "interactItem", cancellable = true)
	public void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		// hook interactBlock between the spectator check and sending the first packet to invoke the use item event first
		// this needs to be in interactBlock to avoid sending a packet in line with the event javadoc
		TypedActionResult<ItemStack> result = UseItemCallback.EVENT.invoker().interact(player, player.world, hand);

		if (result.getResult() != ActionResult.PASS) {
			if (result.getResult() == ActionResult.SUCCESS) {
				// send the move packet like vanilla to ensure the position+view vectors are accurate
				networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround()));
				// send interaction packet to the server with a new sequentially assigned id
				method_41931((ClientWorld) player.world, id -> new PlayerInteractItemC2SPacket(hand, id));
			}

			info.setReturnValue(result.getResult());
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), method = "attackEntity", cancellable = true)
	public void attackEntity(PlayerEntity player, Entity entity, CallbackInfo info) {
		ActionResult result = AttackEntityCallback.EVENT.invoker().interact(player, player.getEntityWorld(), Hand.MAIN_HAND /* TODO */, entity, null);

		if (result != ActionResult.PASS) {
			if (result == ActionResult.SUCCESS) {
				this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, player.isSneaking()));
			}

			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), method = "interactEntityAtLocation", cancellable = true)
	public void interactEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		ActionResult result = UseEntityCallback.EVENT.invoker().interact(player, player.getEntityWorld(), hand, entity, hitResult);

		if (result != ActionResult.PASS) {
			if (result == ActionResult.SUCCESS) {
				Vec3d hitVec = hitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
				this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interactAt(entity, player.isSneaking(), hand, hitVec));
			}

			info.setReturnValue(result);
		}
	}

	@Shadow
	public abstract void method_41931(ClientWorld clientWorld, class_7204 supplier);
}
