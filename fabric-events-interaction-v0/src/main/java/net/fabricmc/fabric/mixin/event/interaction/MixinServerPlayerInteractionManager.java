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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockPlaceEvents;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
	@Shadow
	public ServerWorld world;
	@Shadow
	public ServerPlayerEntity player;

	@Inject(at = @At("HEAD"), method = "processBlockBreakingAction", cancellable = true)
	public void startBlockBreak(BlockPos pos, PlayerActionC2SPacket.Action playerAction, Direction direction, int i, CallbackInfo info) {
		if (playerAction != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) return;
		ActionResult result = AttackBlockCallback.EVENT.invoker().interact(player, world, Hand.MAIN_HAND, pos, direction);

		if (result != ActionResult.PASS) {
			// The client might have broken the block on its side, so make sure to let it know.
			this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
	public void interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
		ActionResult result = UseBlockCallback.EVENT.invoker().interact(player, world, hand, blockHitResult);

		if (result != ActionResult.PASS) {
			info.setReturnValue(result);
			info.cancel();
			return;
		}
	}

	@Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
	public void interactItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		TypedActionResult<ItemStack> result = UseItemCallback.EVENT.invoker().interact(player, world, hand);

		if (result.getResult() != ActionResult.PASS) {
			info.setReturnValue(result.getResult());
			info.cancel();
			return;
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), method = "tryBreakBlock", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, BlockEntity entity, Block block) {
		boolean result = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(this.world, this.player, pos, state, entity);

		if (!result) {
			PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(this.world, this.player, pos, state, entity);

			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBroken(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"), method = "tryBreakBlock", locals = LocalCapture.CAPTURE_FAILHARD)
	private void onBlockBroken(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, BlockEntity entity, Block block, boolean b1) {
		PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(this.world, this.player, pos, state, entity);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), method = "interactBlock", cancellable = true)
	public void beforeBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
		Item item = stack.getItem();

		if (item instanceof BlockItem) {
			ItemUsageContext usageContext = new ItemUsageContext(player, hand, blockHitResult);
			ItemPlacementContext placementContext = new ItemPlacementContext(usageContext);

			BlockState futureBlockState = ((BlockItem) item).getBlock().getPlacementState(placementContext);

			boolean result = PlayerBlockPlaceEvents.BEFORE.invoker().beforeBlockPlace(world, player, placementContext.getBlockPos(), futureBlockState);

			if (!result) {
				PlayerBlockPlaceEvents.CANCELED.invoker().onBlockPlaceCanceled(world, player, placementContext.getBlockPos(), futureBlockState);

				cir.setReturnValue(ActionResult.PASS);
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/ItemUsedOnBlockCriterion;test(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"), method = "interactBlock", cancellable = true)
	public void afterBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
		Item item = stack.getItem();

		if (item instanceof BlockItem) {
			BlockPos targetBlock = blockHitResult.getBlockPos().offset(blockHitResult.getSide());

			PlayerBlockPlaceEvents.AFTER.invoker().afterBlockPlace(world, player, targetBlock, world.getBlockState(targetBlock));
		}
	}
}
