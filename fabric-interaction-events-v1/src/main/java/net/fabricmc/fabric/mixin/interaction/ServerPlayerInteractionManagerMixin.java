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

package net.fabricmc.fabric.mixin.interaction;

import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerBlockAttackEvents;
import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerBlockInteractEvents;
import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerBlockPlaceEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerBlockBreakEvents;
import net.fabricmc.fabric.impl.interaction.InternalEvents;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {
	@Shadow
	public ServerWorld world;
	@Shadow
	public ServerPlayerEntity player;

	@Unique
	@Nullable
	private BlockState tempBlockPlaceState;

	// Block break

	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void beginBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> info, BlockState state, BlockEntity blockEntity) {
		// If ALLOW == false, fire cancel and return
		if (!ServerPlayerBlockBreakEvents.ALLOW.invoker().allowBlockBreak(this.world, this.player, pos, state, blockEntity)) {
			ServerPlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(this.world, this.player, pos, state, blockEntity);
			info.setReturnValue(false);
			return;
		}

		// Between is for fabric-events-interaction-v0: This is an internal event and is only fired if the true ALLOW event is allowed
		if (!InternalEvents.BETWEEN_BLOCK_CANCEL_AND_BREAK.invoker().betweenBlockCancelAndBreak(this.world, this.player, pos, state, blockEntity)) {
			ServerPlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(this.world, this.player, pos, state, blockEntity);
			info.setReturnValue(false);
			return;
		}

		// All events have fired and allow the block change: Fire BEFORE and then after
		ServerPlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(this.world, this.player, pos, state, blockEntity);
	}

	// Only fired if ALLOW returns true
	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBroken(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void afterBlockBroken(BlockPos pos, CallbackInfoReturnable<Boolean> info, BlockState state, BlockEntity blockEntity) {
		ServerPlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(this.world, this.player, pos, state, blockEntity);
	}

	// Block place
	@Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
	private void beforeBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
		final Item item = stack.getItem();

		// TODO: Do we need to cover buckets placing fluids?
		if (item instanceof BlockItem) {
			ItemUsageContext usageContext = new ItemUsageContext(player, hand, blockHitResult);
			ItemPlacementContext placementContext = new ItemPlacementContext(usageContext);
			BlockState futureState = ((BlockItem) item).getBlock().getPlacementState(placementContext);

			if (!ServerPlayerBlockPlaceEvents.ALLOW.invoker().allowBlockPlace(this.world, this.player, blockHitResult.getBlockPos(), futureState)) {
				// Fire cancel event
				ServerPlayerBlockPlaceEvents.CANCELED.invoker().onBlockPlaceCanceled(this.world, this.player, blockHitResult.getBlockPos(), futureState);
				info.setReturnValue(ActionResult.FAIL);
				return;
			}

			ServerPlayerBlockPlaceEvents.BEFORE.invoker().beforeBlockPlace(this.world, this.player, blockHitResult.getBlockPos(), futureState);
			this.tempBlockPlaceState = futureState;
		}
	}

	@Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", shift = At.Shift.AFTER))
	private void afterBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
		// Only fired if allowed
		Item item = stack.getItem();

		// TODO: Do we need to cover buckets placing fluids?
		if (item instanceof BlockItem && this.tempBlockPlaceState != null) {
			ServerPlayerBlockPlaceEvents.AFTER.invoker().afterBlockPlace(this.world, this.player, blockHitResult.getBlockPos(), this.tempBlockPlaceState);
		}

		this.tempBlockPlaceState = null;
	}

	// Attack block
	@Inject(method = "processBlockBreakingAction", at = @At("HEAD"), cancellable = true)
	private void startAttackingBlock(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo info) {
		// Only handle events where player attacks a block
		if (action != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
			return;
		}

		boolean result = ServerPlayerBlockAttackEvents.ALLOW.invoker().allowBlockAttack(this.player, this.world, Hand.MAIN_HAND, pos, direction);

		if (result) {
			info.cancel();
			ServerPlayerBlockAttackEvents.CANCELLED.invoker().onBlockAttackCanceled(this.player, this.world, Hand.MAIN_HAND, pos, direction);
			return;
		}

		// Internal event dispatch for backwards compat TODO

		ServerPlayerBlockAttackEvents.BEFORE.invoker().beforeBlockAttack(this.player, this.world, Hand.MAIN_HAND, pos, direction);
		// FIXME: Where do we call after?
	}

	// Use block
	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void startUsingBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
		boolean result = ServerPlayerBlockInteractEvents.ALLOW.invoker().allowBlockUse(player, player.getServerWorld(), hand, hitResult);

		if (!result) {
			// Fail the result since it was not allowed
			info.setReturnValue(ActionResult.FAIL);
			ServerPlayerBlockInteractEvents.CANCELED.invoker().onBlockUseCanceled(player, player.getServerWorld(), hand, hitResult);
			return;
		}

		// See if mods want to intercept the action - this is where old events are also implemented
		ActionResult actionResult = ServerPlayerBlockInteractEvents.INTERCEPT_DEFAULT_ACTION.invoker().interceptBlockUseAction(player, player.getServerWorld(), hand, hitResult);

		if (actionResult != ActionResult.PASS) {
			// Action was intercepted - set the return value and pass no more events
			// TODO: Fire Cancel if action result is FAIL?
			info.setReturnValue(actionResult);
			return;
		}

		ServerPlayerBlockInteractEvents.BEFORE.invoker().beforeBlockUse(player, player.getServerWorld(), hand, hitResult);
		// FIXME: Implement after event
	}

	// Use item
	@Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
	private void startUsingItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		// TODO: Implement item use events
	}
}
