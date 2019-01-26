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

import net.fabricmc.fabric.api.listener.ListenerReference;
import net.fabricmc.fabric.api.listener.ListenerRegistry;
import net.fabricmc.fabric.api.listener.interaction.AttackBlockEventV1;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
	@Shadow
	public World world;
	@Shadow
	public ServerPlayerEntity player;

	private static final ListenerReference<AttackBlockEventV1> fabric_attackBlockRef = ListenerRegistry.INSTANCE.get(AttackBlockEventV1.class);

	@Inject(at = @At("HEAD"), method = "method_14263", cancellable = true)
	public void startBlockBreak(BlockPos pos, Direction direction, CallbackInfo info) {
		ActionResult result = fabric_attackBlockRef.get().interact(player, world, Hand.MAIN, pos, direction);
		if (result != ActionResult.PASS) {
			// The client might have broken the block on its side, so make sure to let it know.
			this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(world, pos));
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
	public void interactBlock(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
		for (PlayerInteractionEvent.BlockPositioned handler : ((HandlerArray<PlayerInteractionEvent.BlockPositioned>) PlayerInteractionEvent.INTERACT_BLOCK).getBackingArray()) {
			Vec3d vec = blockHitResult.getPos();
			BlockPos pos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getSide();

			float hitX = (float) (vec.x - pos.getX());
			float hitY = (float) (vec.y - pos.getY());
			float hitZ = (float) (vec.z - pos.getZ());

			ActionResult result = handler.interact(player, world, hand, pos, direction, hitX, hitY, hitZ);
			if (result != ActionResult.PASS) {
				info.setReturnValue(result);
				info.cancel();
				return;
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
	public void interactItem(PlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		for (PlayerInteractionEvent.Item handler : ((HandlerArray<PlayerInteractionEvent.Item>) PlayerInteractionEvent.INTERACT_ITEM).getBackingArray()) {
			ActionResult result = handler.interact(player, world, hand);
			if (result != ActionResult.PASS) {
				info.setReturnValue(result);
				info.cancel();
				return;
			}
		}
	}
}
