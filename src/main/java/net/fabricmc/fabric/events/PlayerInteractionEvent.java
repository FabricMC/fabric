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

package net.fabricmc.fabric.events;

import net.fabricmc.fabric.util.HandlerList;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Facing;
import net.minecraft.world.World;

/**
 * This is a class for INTERACTION EVENTS (think left-clicking/right-clicking). For block placement/break
 * events, look elsewhere - this just handles the interaction!
 *
 * CURRENT LIMITATIONS:
 *
 * - INTERACT_BLOCK/INTERACT_ITEM do not expect the ItemStack instance in the player's held hand to change!
 *   If you must do that, consider returning an ActionResult.SUCCESS and re-emitting the event in some manner!
 */
public final class PlayerInteractionEvent {
	@FunctionalInterface
	public interface Block {
		ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Facing facing);
	}

	@FunctionalInterface
	public interface BlockPositioned {
		ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Facing facing, float hitX, float hitY, float hitZ);
	}

	@FunctionalInterface
	public interface Item {
		ActionResult interact(PlayerEntity player, World world, Hand hand);
	}

	public static HandlerRegistry<Block> BREAK_BLOCK = new HandlerList<>();
	public static HandlerRegistry<BlockPositioned> INTERACT_BLOCK = new HandlerList<>();
	public static HandlerRegistry<Item> INTERACT_ITEM = new HandlerList<>();

	private PlayerInteractionEvent() {

	}
}
